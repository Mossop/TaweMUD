package com.esp.tawemud;

import java.io.OutputStream;
import java.util.StringTokenizer;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import com.esp.tawemud.ServerBase;

/**
 * Handles the text input and output to the player.
 *
 * Works on a socket, continually filling an line input buffer.
 * Also formats text with colours and linefeeds. Any linefeeds or tabs in text sent to players
 * is stripped. Only spaces are left. For a linefeed the characters @/ are used. Colours are
 * represented with @+Z, @-Z or @=XY where X,Y and Z are characters that represent a colour. +
 * sets the foreground colour, - the background and = both. @* resets to the default colour.
 *
 * @author  Dave Townsend
 * @version 1.9
 */
public class PlayerIO implements IOBase, Runnable
{
	/**
	 * The name of the player this IO is for.
	 */
	private String player;
	/**
	 * The server.
	 */
	private ServerBase server;
	/**
	 * The socket we are working with.
	 */
	private Socket client;
	/**
	 * Whether we should be sending colours out or not.
	 */
	private boolean sendcolor;
	/**
	 * The current position of the cursor in the players telnet window.
	 */
	private int cursorpos;
	/**
	 * The width of the players telnet window.
	 */
	private int width;
	/**
	 * The height of the players telnet window.
	 */
	private int height;
	/**
	 * If the connection is closed.
	 */
	private boolean closed;
	/**
	 * If the connection is about to close.
	 */
	private boolean closing;
	/**
	 * If the readLine() method is blocking or not.
	 *
	 * @see #readLine()
	 */
	private boolean blocking;
	/**
	 * The lines that are waiting for processing.
	 */
	private List inputbuffer;
	/**
	 * The text in the current line.
	 */
	private StringBuffer charbuffer;
	/**
	 * The text waiting to be displayed to the player.
	 */
	private List outputbuffer;
	/**
	 * Whether we are echoing text back to the player.
	 */
	private boolean echoing;
	/**
	 * The outputstream of our socket.
	 */
	private OutputStream out;
	/**
	 * If we know the size of the players telnet window yet.
	 */
	private boolean gotsize;

	/**
	 * The telnet character Interpret As Command.
	 */
	private static final char IAC = '\u00FF';
	/**
	 * The telnet command for DONT.
	 */
	private static final char DONT = '\u00FE';
	/**
	 * The telnet command for DO.
	 */
	private static final char DO = '\u00FD';
	/**
	 * The telnet command for WONT.
	 */
	private static final char WONT = '\u00FC';
	/**
	 * The telnet command for WILL.
	 */
	private static final char WILL = '\u00FB';
	/**
	 * The telnet command for SB.
	 */
	private static final char SB = '\u00FA';

	/**
	 * The telnet option ECHO.
	 */
	private static final char ECHO = '\u0001';
	/**
	 * The telnet option SUPPRESS GO AHEAD.
	 */
	private static final char SUPPRESS_GA = '\u0003';
	/**
	 * The telnet option NAWS.
	 */
	private static final char NAWS = '\u001F';

	/**
	 * Initialises the connection.
	 *
	 * Does a little handshaking with the client and sets up the input and output
	 * buffers. Starts the thread running for input.
	 */
	public PlayerIO(Socket ourclient, boolean isblocking, ServerBase server) throws IOException
	{
		this.server=server;
		client=ourclient;
		cursorpos=0;
		client.setSoTimeout(100);
		out=client.getOutputStream();
		writeDont(ECHO);
		writeWill(ECHO);
		writeWill(SUPPRESS_GA);
		writeDo(NAWS);
		echoing=true;
		sendcolor=true;
		width=80;
		height=25;
		closed=false;
		closing=false;
		blocking=isblocking;
		inputbuffer = Collections.synchronizedList(new LinkedList());
		outputbuffer = Collections.synchronizedList(new LinkedList());
		charbuffer = new StringBuffer();
		gotsize=false;
		(new Thread(this,"PlayerIO")).start();
	}

	/**
	 * Sets the server we can use.
	 *
	 * @param server	The new server
	 */
	public void setServer(ServerBase server)
	{
		this.server=server;
	}
	
	/**
	 * Sets the name of our player.
	 *
	 * @param name	The players name
	 */
	public void setPlayer(String name)
	{
		player=name;
	}
	
	/**
	 * Sets the size of the players telnet screen.
	 *
	 * @param newwidth  The width in characters.
	 * @param newheight The height is characters.
	 */
	private void setSize(int newwidth, int newheight)
	{
		synchronized (outputbuffer)
		{
			height=newheight;
			width=newwidth;
		}
		setContinue(true);
	}

	/**
	 * Checks if we have the players telnet size. We cannot display any output until we
	 * have the players size, or had a response to say that we wont be getting a size.
	 *
	 * @return  True if we know the size
	 */
	private synchronized boolean canContinue()
	{
		return gotsize;
	}

	/**
	 * Sets whether we have a size or not.
	 *
	 * @param value True if we have the size
	 */
	private synchronized void setContinue(boolean value)
	{
		gotsize=value;
	}

	/**
	 * A helper method to convert raw text into formatted text for diplay to a player.
	 *
	 * The text is converted simply, a tab becomes 2 spaces, a newline becomes @/ and
	 * carraige returns are stripped.
	 *
	 * @param text  The raw text
	 * @return  The formatted text
	 */
	public static String convertText(String text)
	{
		StringTokenizer tokens = new StringTokenizer(text,"\t\r\n",true);
		StringBuffer buffer = new StringBuffer();
		while (tokens.hasMoreTokens())
		{
			String next = tokens.nextToken();
			if (next.equals("\t"))
			{
				buffer.append("  ");
			}
			else if (next.equals("\n"))
			{
				buffer.append("@/\n");
			}
			else if (next.equals("\r"))
			{
			}
			else
			{
				buffer.append(next);
			}
		}
		return buffer.toString();
	}

	/**
	 * Strips any colour sequences from the given text.
	 *
	 * Mainly used to figure out how much actual printable text is there.
	 *
	 * @param text  The formatted text
	 * @return  The same text with no colour information
	 */
	public static String stripColour(String text)
	{
		while (text.indexOf("@")>=0)
		{
			int pos=text.indexOf("@");
			String nextchar=text.substring(pos+1,pos+2);
			if ((nextchar.equals("+"))||(nextchar.equals("-")))
			{
				text=text.substring(0,pos)+text.substring(pos+3);
			}
			else if (nextchar.equals("="))
			{
				text=text.substring(0,pos)+text.substring(pos+4);
			}
			else if (nextchar.equals("*"))
			{
				text=text.substring(0,pos)+text.substring(pos+2);
			}
		}
		return text;
	}

	/**
	 * Works out what ansi code we need for a particular colour.
	 *
	 * @param color The colour character
	 * @param foreground  True if we are setting the foreground, false for the background
	 * @return  The numeric part of the ansi code needed to produce the desired colour
	 */
	private static String getValue(String color,boolean foreground)
	{
		String real = color.toUpperCase();
		String value;
		if (foreground)
		{
			if (real.equals(color))
			{
				value="1;3";
			}
			else
			{
				value="0;3";
			}
		}
		else
		{
			value="4";
		}
		if (real.equals("L"))
		{
			value=value+"0";
		}
		else if (real.equals("R"))
		{
			value=value+"1";
		}
		else if (real.equals("G"))
		{
			value=value+"2";
		}
		else if (real.equals("Y"))
		{
			value=value+"3";
		}
		else if (real.equals("B"))
		{
			value=value+"4";
		}
		else if (real.equals("M"))
		{
			value=value+"5";
		}
		else if (real.equals("C"))
		{
			value=value+"6";
		}
		else if (real.equals("W"))
		{
			value=value+"7";
		}
		else
		{
			value="0";
		}
		return value;
	}

	/**
	 * Returns our socket.
	 *
	 * @return  The socket we are conencted to
	 */
	public Socket getSocket()
	{
		return client;
	}

	/**
	 * Sends a WILL telnet command to the client.
	 *
	 * @param code  The option we are going to perform
	 */
	private void writeWill(char code)
	{
		//System.out.println("SENT WILL "+((int)code));
		writeString(""+IAC+WILL+code);
	}

	/**
	 * Sends a WONT telnet command to the client.
	 *
	 * @param code  The option we are not going to perform
	 */
	private void writeWont(char code)
	{
		//System.out.println("SENT WONT "+((int)code));
		writeString(""+IAC+WONT+code);
	}

	/**
	 * Sends a DO telnet command to the client.
	 *
	 * @param code  The option we wish the client to perform
	 */
	private void writeDo(char code)
	{
		//System.out.println("SENT DO "+((int)code));
		writeString(""+IAC+DO+code);
	}

	/**
	 * Sends a DONT telnet command to the client.
	 *
	 * @param code  The option we dont want the client to perform
	 */
	private void writeDont(char code)
	{
		//System.out.println("SENT DONT "+((int)code));
		writeString(""+IAC+DONT+code);
	}

	/**
	 * Writes a new line to the client.
	 */
	private void writeNewLine()
	{
		synchronized(out)
		{
			try
			{
				out.write(new byte[] {13,10});
				out.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes some raw text to the client.
	 *
	 * @param data  The text to send
	 */
	private void writeString(String data)
	{
		synchronized (out)
		{
			try
			{
				out.write(data.getBytes());
				out.flush();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set where we think the clients cursor is.
	 *
	 * @param pos The new position
	 */
	private synchronized void setPos(int pos)
	{
		cursorpos=pos%width;
		while (cursorpos<0)
		{
			cursorpos+=width;
		}
	}

	/**
	 * Returns the position we think the clients cursor is in.
	 *
	 * @return  The cursor position
	 */
	private synchronized int getPos()
	{
		return cursorpos;
	}

	public synchronized void setEchoing(boolean value)
	{
		echoing=value;
	}

	private synchronized boolean isEchoing()
	{
		return echoing;
	}

	/**
	 * Echoes some text back to the client if echoing is enabled.
	 *
	 * @param data  The text to echo back
	 */
	private void echoString(String data)
	{
		if (isEchoing())
		{
			writeString(data);
		}
	}

	/**
	 * Echoes a new line back to the client if echoing is enabled.
	 */
	private void echoNewLine()
	{
		if (isEchoing())
		{
			writeNewLine();
		}
	}

	/**
	 * Flushes the output buffer to the client.
	 */
	public void flush()
	{
		boolean needbuffer = outputbuffer.size()>0;
		while (outputbuffer.size()>0)
		{
			Object thisset=outputbuffer.get(0);
			if (thisset instanceof List)
			{
				writeString("\u001B["+width+"D\u001B[J");
				send((List)thisset);
			}
			else
			{
				writeString(thisset.toString());
			}
			outputbuffer.remove(0);
		}
		if (needbuffer)
		{
			synchronized(charbuffer)
			{
				writeString(charbuffer.toString()+"\u001B[6n");
			}
		}
	}

	/**
	 * Sends lines to the client. If there are more lines than the clients window
	 * can display then a pager is used.
	 *
	 * @param lines The lines to send
	 */
	private void send(List lines)
	{
		boolean wasblocking = isBlocking();
		boolean done = false;
		int loop = 0;
		int screenlines = 0;
		setBlocking(true);
		int pagecount=0;
		int pages=(int)Math.ceil((lines.size()*1.00)/(height-2));
		while ((!done)&&(loop<lines.size()))
		{
			writeString(lines.get(loop).toString());
			loop++;
			if (loop<lines.size())
			{
				writeNewLine();
				screenlines++;
				if (screenlines==(height-2))
				{
					pagecount++;
					done=doPager(pagecount,pages);
					screenlines=0;
				}
			}
		}
		setBlocking(wasblocking);
	}

	/**
	 * Perform some paging.
	 *
	 * @param page  The current page number
	 * @param total The total pages
	 * @return  True if the player wishes to stop the paging, false to continue
	 */
	private boolean doPager(int page, int total)
	{
		writeNewLine();
		writeString("\u001B[1;36m[Page "+page+"/"+total+"]\u001B[0m"+charbuffer.toString());
		String line=readLine();
		writeString("\u001B[0A\u001B[2K\u001B[0A");
		if ((line==null)||(line.toLowerCase().startsWith("q")))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Converts the @ sequences for colour into the real ansi codes. Also performs
	 * line wrapping, building the text into a list of lines. These are then added to the
	 * output buffer.
	 *
	 * @param text  The text to be displayed
	 */
	private void colorize(String text)
	{
		LinkedList lines = new LinkedList();
		StringBuffer buffer = new StringBuffer();
		int pos = 0;
		int newpos=text.indexOf("@",pos);
		char command;
		String color;
		int colorlength=0;
		int rllength;

		while (newpos>=0)
		{
			buffer.append(text.substring(pos,newpos));
			rllength=buffer.length()-colorlength;
			while (rllength>width)
			{
				int scanpos=width+colorlength;
				while ((scanpos>=0)&&(buffer.charAt(scanpos)!=' '))
				{
					scanpos--;
				}
				if (scanpos<0)
				{
					scanpos=width+colorlength;
				}
				lines.add(new StringBuffer(buffer.substring(0,scanpos)));
				buffer.delete(0,scanpos+1);
				rllength=buffer.length();
				colorlength=0;
			}
			pos=newpos;
			command=text.charAt(pos+1);
			color="";
			if (command=='@')
			{
				buffer.append("@");
				pos+=2;
			}
			else if (command=='*')
			{
				color="\u001B[0m";
				pos+=2;
			}
			else if (command=='/')
			{
				lines.add(buffer);
				buffer = new StringBuffer();
				colorlength=0;
				pos+=2;
			}
			else if (command=='+')
			{
				color="\u001B["+getValue(text.substring(pos+2,pos+3),true)+"m";
				pos+=3;
			}
			else if (command=='-')
			{
				color="\u001B["+getValue(text.substring(pos+2,pos+3),false)+"m";
				pos+=3;
			}
			else if (command=='=')
			{
				color="\u001B["+getValue(text.substring(pos+2,pos+3),true)+";"+getValue(text.substring(pos+3,pos+4),false)+"m";
				pos+=4;
			}
			else
			{
				buffer.append("@");
				pos+=1;
			}
			if (sendcolor)
			{
				buffer.append(color);
				colorlength=colorlength+color.length();
			}
			newpos=text.indexOf("@",pos);
		}
		buffer.append(text.substring(pos));
		rllength=buffer.length()-colorlength;
		while (rllength>width)
		{
			int scanpos=width+colorlength;
			while ((scanpos>=0)&&(buffer.charAt(scanpos)!=' '))
			{
				scanpos--;
			}
			if (scanpos==0)
			{
				scanpos=width+colorlength;
			}
			lines.add(new StringBuffer(buffer.substring(0,scanpos)));
			buffer.delete(0,scanpos+1);
			rllength=buffer.length();
			colorlength=0;
		}
		lines.add(buffer);
		outputbuffer.add(lines);
	}

	/**
	 * Display some text to the player.
	 *
	 * Actually waits till we have the players terminal screen size then performs a little
	 * preprocessing before calling colorize()
	 *
	 * @param text  The text to be displayed
	 * @see #colorize(String
	 * )
	 */
	private void print(String text)
	{
		while ((!canContinue())&&(!isClosing()))
		{
			try
			{
				Thread.sleep(100);
			}
			catch (Exception e)
			{
			}
		}
		StringBuffer buffer = new StringBuffer();
		StringTokenizer splitter = new StringTokenizer(text,"\n\r\t\f");
		while (splitter.hasMoreTokens())
		{
			buffer.append(splitter.nextToken());
			if ((splitter.hasMoreTokens())&&(buffer.length()>0)&&(!buffer.toString().endsWith("@/"))&&(!buffer.toString().endsWith(" ")))
			{
				buffer.append(" ");
			}
		}
		colorize(buffer.toString());
	}

	/**
	 * Puts some text into the output buffer with no processing.
	 *
	 * Used to display text that may have colour codes in it without extracting them.
	 *
	 * @param text  The text to be displayed
	 */
	private void printRaw(String text)
	{
		synchronized (outputbuffer)
		{
			outputbuffer.add(text);
		}
	}

	/**
	 * Adds a prompt to the output buffer. This will not have a linefeed appended and
	 * extra codes are sent to determine the current cursor position.
	 *
	 * @param text  The prompt
	 */
	public void printPrompt(String text)
	{
		print(text);
	}

	/**
	 * Adds some text to the output buffer. This will have a linefeed appended.
	 *
	 * @param text  The text to be displayed
	 */
	public void println(String text)
	{
		print(text+"@/");
	}

	/**
	 * Checks if there is a line of text waiting in the input buffer.
	 *
	 * @return  True if the inputbuffer has any data in it
	 */
	public boolean hasData()
	{
		return (inputbuffer.size()>0);
	}

	/**
	 * Reads a line from the input buffer.
	 *
	 * If the class is set to be blocking then this method will block until a line
	 * is available, or the connection is closed.
	 *
	 * @return  A line of text, or null if the connection has closed or there is no line
	 * @return  waiting and the class is not blocking
	 */
	public String readLine()
	{
		String result = null;
		while ((!hasData())&&(isBlocking())&&(!isClosing()))
		{
			try
			{
				Thread.sleep(10);
			}
			catch (Exception e)
			{
			}
		}
		if (hasData())
		{
			result=(String)inputbuffer.get(0);
			inputbuffer.remove(0);
		}
		return result;
	}

	/**
	 * Returns whether the class is blocking or not.
	 *
	 * @return  True if the class is blocking, False otherwise
	 */
	public synchronized boolean isBlocking()
	{
		return blocking;
	}

	/**
	 * Sets whether or not the class is blocking.
	 *
	 * @param isit  True to set the class to be blocking
	 */
	public synchronized void setBlocking(boolean isit)
	{
		blocking=isit;
	}

	/**
	 * Checks if the connection is totally closed.
	 *
	 * @return  True if the connection is closed, False otherwise
	 */
	public synchronized boolean isClosed()
	{
		return closed;
	}

	/**
	 * Checks if the connection is about to close or is already closed.
	 *
	 * @return True if the connection is closing, false otherwise
	 */
	public synchronized boolean isClosing()
	{
		return closing;
	}

	/**
	 * Closes the connection.
	 */
	public synchronized void close()
	{
		closing=true;
	}

	/**
	 * Strips a telnet command from the character input buffer.
	 *
	 * @param line  The character buffer
	 * @param pos The position the code was discovered at
	 */
	private void stripTelnetCode(StringBuffer line,int pos)
	{
		line.delete(pos,pos+1);
		if (pos<line.length())
		{
			char command=line.charAt(pos);
			line.delete(pos,pos+1);
			if ((command>=SB)&&(command<IAC)&&(pos<line.length()))
			{
				char option=line.charAt(pos);
				line.delete(pos,pos+1);
				if (command==SB)
				{
					int endpos=line.toString().indexOf("\u00FF\u00F0");
					if (endpos>=pos)
					{
						String code=line.substring(pos,endpos);
						line.delete(pos,endpos+2);
						if ((option==NAWS)&&(code.length()==4))
						{
							int cols = (int)code.charAt(1);
							int rows = (int)code.charAt(3);
							cols=cols+((int)code.charAt(0))*256;
							rows=rows+((int)code.charAt(2))*256;
							setSize(cols,rows);
						}
					}
				}
				else if (command==WILL)
				{
					//System.out.println("RCVD WILL "+((int)option));
				}
				else if (command==WONT)
				{
					//System.out.println("RCVD WONT "+((int)option));
					if (option==NAWS)
					{
						setContinue(true);
					}
				}
				else if (command==DO)
				{
					//System.out.println("RCVD DO "+((int)option));
				}
				else if (command==DONT)
				{
					//System.out.println("RCVD DONT "+((int)option));
				}
			}
		}
	}

	/**
	 * Strips an escape code from the character input buffer.
	 *
	 * @param line  The character buffer
	 * @param pos The position the code was discovered at
	 */
	private void stripEscapeCode(StringBuffer line, int pos)
	{
		line.delete(pos,pos+1);
		if (pos<line.length())
		{
			switch (line.charAt(pos))
			{
				case '[':
					if ((pos+1)<line.length())
					{
						if (Character.isDigit(line.charAt(pos+1)))
						{
							int loop=pos+2;
							while ((loop<line.length())&&((Character.isDigit(line.charAt(loop))||(line.charAt(loop)==';'))))
							{
								loop++;
							}
							if (loop<line.length())
							{
								if (line.charAt(loop)=='~')
								{
									String value = line.substring(pos+1,loop);
									try
									{
										int rlvalue = Integer.parseInt(value);
										if (rlvalue==11)
										{
											inputbuffer.add("help");
										}
										else if (rlvalue==3)
										{
											backspace();
										}
									}
									catch (Exception e)
									{
									}
								}
								else if (line.charAt(loop)=='R')
								{
									String report = line.substring(pos+2,loop);
									int splitpos=report.indexOf(";");
									if (splitpos>=0)
									{
										setPos(Integer.parseInt(report.substring(splitpos+1))-1);
									}
								}
								line.delete(pos,loop+1);
							}
							else
							{
								line.delete(pos,pos+2);
							}
						}
						else
						{
							if ((pos+1)<line.length())
							{
								switch (line.charAt(pos+1))
								{
									default:
										line.delete(pos,pos+2);
								}
							}
							else
							{
								line.delete(pos,pos+1);
							}
						}
					}
					else
					{
						line.delete(pos,pos+1);
					}
					break;
				case 'O':
					switch (line.charAt(pos+1))
					{
						case 'P':
							inputbuffer.add("help");
							break;
					}
					line.delete(pos,pos+2);
					break;
			}
		}
	}

	/**
	 * Echos a backspace to the player.
	 */
	private void backspace()
	{
		if (charbuffer.length()>0)
		{
			charbuffer.delete(charbuffer.length()-1,charbuffer.length());
			if (getPos()==0)
			{
				echoString("\u001B[A\u001B["+(width-1)+"C\u001B[K");
			}
			else
			{
				echoString("\u001B[D\u001B[K");
			}
			setPos(getPos()-1);
		}
	}

	/**
	 * Handles incoming data from the player.
	 */
	public void run()
	{
		try
		{
			byte[] bytebuffer = new byte[512];
			InputStream in = client.getInputStream();
			StringBuffer line;
			int count;
			int linecount;
			long[] linetimes = new long[] {1000,1000,1000,1000,1000};
			long now;
			long avgtotal;
			long last = System.currentTimeMillis();
			StringBuffer section = new StringBuffer();
			do
			{
				linecount=0;
				try
				{
					count=in.read(bytebuffer);
				}
				catch (InterruptedIOException e)
				{
					count=0;
				}
				if (count>0)
				{
					synchronized (charbuffer)
					{
						section.append(new String(bytebuffer,0,count));
						/*for (int loop=0; loop<section.length(); loop++)
						{
							System.out.print(((int)section.charAt(loop))+" ");
						}
						System.out.println();*/
						while (section.length()>0)
						{
							switch (section.charAt(0))
							{
								case '\u00B1':
								case '\u0008':
								case '\u007F':
									backspace();
									section.delete(0,1);
									break;
								case '\u001B':
									stripEscapeCode(section,0);
									break;
								case '\u00FF':
									stripTelnetCode(section,0);
									break;
								case '\r':
									now=System.currentTimeMillis();
									if (charbuffer.length()>0)
									{
										avgtotal=0;
										for (int loop=1; loop<linetimes.length; loop++)
										{
											linetimes[loop-1]=linetimes[loop];
											avgtotal+=linetimes[loop];
										}
										linetimes[linetimes.length-1]=(now-last)/charbuffer.length();
										avgtotal+=linetimes[linetimes.length-1];
										if ((avgtotal/linetimes.length)<30)
										{
											server.sendWizMessage("Possible scripting from "+player,"",0,2,true);
										}
									}
									last=now;
									inputbuffer.add(charbuffer.toString());
									/*for (int loop=0; loop<charbuffer.length(); loop++)
									{
										System.out.print(((int)charbuffer.charAt(loop))+" ");
									}
									System.out.println();*/
									charbuffer.delete(0,charbuffer.length());
									echoNewLine();
									setPos(0);
									section.delete(0,1);
									linecount++;
									break;
								case '\n':
									section.delete(0,1);
									break;
								default:
									if (((section.charAt(0)>=32)&&(section.charAt(0)<127))||(section.charAt(0)==163))
									{
										charbuffer.append(section.charAt(0));
										echoString(section.substring(0,1));
										setPos(getPos()+1);
										section.delete(0,1);
									}
									else
									{
										section.delete(0,1);
									}
							}
						}
					}
				}
				/*if (linecount>1)
				{
					System.out.println("Possible scripting");
				}*/
			} while ((!isClosing())&&(count>=0));
			flush();
			in.close();
			out.close();
			client.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		synchronized (this)
		{
			closed=true;
			closing=true;
		}
	}
}
