package com.esp.tawemud.items;

import java.net.Socket;
import java.io.PrintWriter;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Date;
import com.esp.tawemud.multiline.MultiLineHandler;
import java.text.SimpleDateFormat;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.IOBase;
import com.esp.tawemud.Zone;
import com.esp.tawemud.Mail;
import com.esp.tawemud.Whois;
import com.esp.tawemud.tawescript.Variables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.util.GregorianCalendar;
import java.util.Calendar;

public class Player extends Mobile implements MailHandler, Runnable
{
	private String title;
	private String mortalprompt;
	private String immortprompt;
	private String awaymessage;
	private Item homeroom;
	private int password;
	private Vector mailbox;
	private Calendar laston;
	private Calendar logon;
	private IOBase client;
	private StringBuffer commandbuffer;
	private StringBuffer multilinebuffer;
	private StringBuffer linkdeadbuffer;
	private boolean connected;
	private boolean registered;
	private long linkdeadtick;
	private Zone creationzone;
	private MultiLineHandler multiline;
	private boolean hasmultiline;
	private long lastcommand;
	private boolean warned;
	private StringBuffer origincountry;
	private StringBuffer origincity;
	private long timeon;
	private long millislogon;
	private long commandsrun;
	private String email;

	public Player(TaweServer server)
	{
		super(server);
		timeon=0;
		email="";
		commandsrun=0;
		millislogon=0;
		warned=false;
		immortprompt="@+R%a@+WVI @+R(%v)@+W>@*";
		mortalprompt="@+R%a@+Y%h/%H %m/%M@+W>@*";
		awaymessage="";
		client=null;
		title="%n";
		setValue("homeroom","newbie.start");
		mailbox = new Vector(10);
		commandbuffer = new StringBuffer();
		multilinebuffer = new StringBuffer();
		linkdeadbuffer = new StringBuffer();
		password=0;
		registered=false;
		connected=false;
		linkdeadtick=0;
		creationzone=null;
		hasmultiline=false;
		laston=new GregorianCalendar();
		origincountry=new StringBuffer();
		origincity=new StringBuffer();
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		node.setAttribute("title",title);
		node.setAttribute("password",Integer.toString(password));
		if (homeroom!=null)
		{
			node.setAttribute("homeroom",homeroom.getWorldIdentifier());
		}
		if (!awaymessage.equals(""))
		{
			node.setAttribute("awaymessage",awaymessage);
		}
		if (!email.equals(""))
		{
			node.setAttribute("email",email);
		}
		node.setAttribute("timeon",Long.toString(getRealTimeOn()));
		node.setAttribute("commandsrun",Long.toString(commandsrun));
		node.setAttribute("mortalprompt",mortalprompt);
		node.setAttribute("immortprompt",immortprompt);
		SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		node.setAttribute("laston",df.format(laston.getTime()));
		node.setAttribute("logon",df.format(laston.getTime()));
	}

	public void deleteCommandCount()
	{
		super.deleteCommandCount();
		if (commandcount==0)
		{
			displayText(commandbuffer.toString());
			commandbuffer.delete(0,commandbuffer.length());
		}
	}

	public synchronized boolean setMultiLine(MultiLineHandler newhandler)
	{
		if (hasmultiline)
		{
			return false;
		}
		else
		{
			multiline=newhandler;
			hasmultiline=true;
			return true;
		}
	}

	public synchronized boolean hasMultiLine()
	{
		return hasmultiline;
	}

	public synchronized void clearMultiLine()
	{
		hasmultiline=false;
		displayText(multilinebuffer.toString());
		multilinebuffer.delete(0,multilinebuffer.length());
	}

	public String getHost()
	{
		if (isConnected())
		{
			return client.getSocket().getInetAddress().getHostName();
		}
		else
		{
			return "";
		}
	}

	public String getAwayMessage()
	{
		return awaymessage;
	}

	public void setAwayMessage(String mess)
	{
		awaymessage=mess;
	}

	public String getEmail()
	{
		return email;
	}

	public void setEmail(String address)
	{
		email=address;
	}

	public Zone getCreationZone()
	{
		return creationzone;
	}

	public void setCreationZone(Zone newzone)
	{
		creationzone=newzone;
	}

	public synchronized boolean isRegistered()
	{
		return registered;
	}

	public synchronized void setRegistered(boolean newstate)
	{
		registered=newstate;
	}

	public synchronized boolean isConnected()
	{
		return connected;
	}

	public synchronized void setConnected(boolean newstate)
	{
		connected=newstate;
	}

	public void connect(IOBase newclient)
	{
		newclient.setPlayer(getName());
		lastcommand = System.currentTimeMillis();
		origincountry=new StringBuffer();
		origincity=new StringBuffer();
		(new Whois()).lookup(newclient.getSocket().getInetAddress(),origincity,origincountry);
		if (isRegistered())
		{
			if (isConnected())
			{
				getServer().sendWizMessage(getName()+" has reconnected",getWorldIdentifier(),getVisibility(),1);
				client.println("You have been kicked off!");
				client.close();
				while (isConnected())
				{
					try
					{
						Thread.sleep(100);
					}
					catch (Exception e)
					{
					}
				}
				client=newclient;
				setConnected(true);
				displayText("You have kicked yourself off.");
				getServer().parseCommand(this,"look");
			}
			else
			{
				client=newclient;
				setConnected(true);
				removeFlag("linkdead");
				getServer().sendWizMessage(getName()+" has regained link",getWorldIdentifier(),getVisibility(),1);
				displayText("You have regained link.");
				displayText(linkdeadbuffer.toString());
				linkdeadbuffer.delete(0,linkdeadbuffer.length());
				getServer().parseCommand(this,"look");
			}
		}
		else
		{
			client=newclient;
			setConnected(true);
			getServer().registerPlayer(this);
			setRegistered(true);
			millislogon=System.currentTimeMillis();
			logon=new GregorianCalendar();
			if ((location!=null)&&(location.getWorldIdentifier().equals("players.loggedoff")))
			{
				displayText(getWorld().getLoginMessage()+"@*");
				displayText("@+WThis mud is running @+G"+TaweServer.getVersion()+" build "+TaweServer.getBuild());
				getServer().sendWizMessage(getName()+" has logged in",getWorldIdentifier(),getVisibility(),1,true);
				if (homeroom!=null)
				{
					getServer().doMove(this,homeroom.getRoomLocation());
				}
				int mesgcount=0;
				for (int loop=0; loop<mailbox.size(); loop++)
				{
					if (!((Mail)mailbox.elementAt(loop)).isRead())
					{
						mesgcount++;
					}
				}
				if (mesgcount>1)
				{
					displayText("@+RYou have "+mesgcount+" new mails. Type mail for details.@/");
				}
				else if (mesgcount==1)
				{
					displayText("@+RYou have 1 new mail. Type mail for details.@/");
				}
				Iterator boardloop;
				if (isDPower())
				{
					boardloop = getServer().getBoards(10000);
				}
				else
				{
					boardloop = getServer().getBoards(getLevel());
				}
				while (boardloop.hasNext())
				{
					Board thisone = (Board)boardloop.next();
					if (thisone.getMailCount()>0)
					{
						if (laston.getTime().before(thisone.getMail(thisone.getMailCount()-1).getDate()))
						{
							displayText(thisone.getNewMessage());
						}
					}
				}
			}
		}
		(new Thread(this,"Player-"+getIdentifier())).start();
	}

	public String getCountry()
	{
		return origincountry.toString();
	}

	public String getCity()
	{
		return origincity.toString();
	}

	public String getIdleTime()
	{
		long idle=(System.currentTimeMillis()-lastcommand)/1000;
		String result = Long.toString(idle%60);
		if (result.length()<2)
		{
			result="0"+result;
		}
		result=((idle/60)%60)+":"+result;
		if (result.length()<5)
		{
			result="0"+result;
		}
		result=((idle/3600)%60)+":"+result;
		if (result.length()<7)
		{
			result="0"+result;
		}
		return result;
	}

	public int getIdleTimeSecs()
	{
		return (int)(System.currentTimeMillis()-lastcommand)/1000;
	}

	public IOBase getClient()
	{
		return client;
	}

	public void disconnect()
	{
		if (isConnected())
		{
			setConnected(false);
		}
	}

	public void kill()
	{
		quit();
		reset();
	}
	
	public void quit()
	{
		if (isConnected())
		{
			client.println(commandbuffer.toString());
			commandbuffer.delete(0,commandbuffer.length());
			client.close();
		}
		zone.getWorld().getServer().unregisterPlayer(this);
		setRegistered(false);
		setLocation(getStartLocation());
	}

	public void displayText(String noshow, int vis, String text)
	{
		if (isPower())
		{
			super.displayText(noshow,0,text);
		}
		else
		{
			super.displayText(noshow,vis,text);
		}
	}

	public void displayText(String text)
	{
		if (isRegistered())
		{
			if (commandcount>0)
			{
				if (commandbuffer.length()>0)
				{
					commandbuffer.append("@/");
				}
				commandbuffer.append(text).append("@*");
			}
			else if (hasMultiLine())
			{
				if (multilinebuffer.length()>0)
				{
					multilinebuffer.append("@/");
				}
				multilinebuffer.append(text).append("@*");
			}
			else if (!isConnected())
			{
				if (linkdeadbuffer.length()>0)
				{
					linkdeadbuffer.append("@/");
				}
				linkdeadbuffer.append(text).append("@*");
			}
			else
			{
				client.println("\u001B[u\u001B[100D\u001B[J"+text+"@*");
				displayPrompt();
			}
		}
	}

	public void displayPrompt()
	{
		Variables prompter= new Variables();
		prompter.setVariable("%h",Integer.toString(getHealth()));
		prompter.setVariable("%H",Integer.toString(getMaxHealth()));
		prompter.setVariable("%m",Integer.toString(getMana()));
		prompter.setVariable("%M",Integer.toString(getMaxMana()));
		prompter.setVariable("%s",Integer.toString(getSkill()));
		prompter.setVariable("%S",Integer.toString(getMaxSkill()));
		prompter.setVariable("%v",Integer.toString(getVisibility()));
		prompter.setVariable("%e",Integer.toString(getExperience()));
		prompter.setVariable("%l",Integer.toString(getLevel()));
		if (checkFlag("away"))
		{
			prompter.setVariable("%a","AFK ");
		}
		else
		{
			prompter.setVariable("%a","");
		}
		displayPrompt(prompter.parseString(getPrompt()));
	}

	public void displayPrompt(String text)
	{
		if (isRegistered())
		{
			client.printPrompt(text+"@*");
		}
	}

	public String getPrompt()
	{
		if (checkFlag("immort"))
		{
			return immortprompt;
		}
		else
		{
			return mortalprompt;
		}
	}

	public void setPrompt(String newprompt)
	{
		if (checkFlag("immort"))
		{
			immortprompt=newprompt;
		}
		else
		{
			mortalprompt=newprompt;
		}
	}

	public String getImmortPrompt()
	{
		return immortprompt;
	}

	public void setImmortPrompt(String newprompt)
	{
		immortprompt=newprompt;
	}

	public String getMortalPrompt()
	{
		return mortalprompt;
	}

	public void setMortalPrompt(String newprompt)
	{
		mortalprompt=newprompt;
	}

	public void addMail(Mail message)
	{
		displayText("@+RYou have new mail!@*");
		mailbox.addElement(message);
	}

	public int getMailCount()
	{
		return mailbox.size();
	}

	public void removeMail(int message)
	{
		mailbox.remove(message);
	}

	public Mail getMail(int message)
	{
		if (message<mailbox.size())
		{
			return (Mail)mailbox.elementAt(message);
		}
		else
		{
			return null;
		}
	}

	public String getMessages()
	{
		StringBuffer buffer = new StringBuffer("RD Msg From            Subject                                  Date@/@/");
		for (int loop=0; loop<mailbox.size(); loop++)
		{
			StringBuffer line = new StringBuffer();
			Mail message = (Mail)mailbox.elementAt(loop);
			if (message.isRead())
			{
				line.append("Y");
			}
			else
			{
				line.append("N");
			}
			String item = Integer.toString(loop+1);
			while (item.length()<3)
			{
				item=" "+item;
			}
			line.append("  "+item+" ");
			item=message.getSender();
			while (item.length()<15)
			{
				item=item+" ";
			}
			line.append(item+" "+message.getSubject());
			while (line.length()<63)
			{
				line.append(" ");
			}
			line.setLength(63);
			line.append(" ");
			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			line.append(dateformat.format(message.getDate()));
			buffer.append(line);
			buffer.append("@/");
		}
		return buffer.toString();
	}

	public MailHandler getMailHandler()
	{
		return this;
	}

	public boolean isBanned()
	{
		return checkFlag("banned");
	}

	public boolean authenticate(String test)
	{
		return ((!isBanned())&&(test.hashCode()==password));
	}

	public boolean canSee(Item item)
	{
		if (isPower())
		{
			return true;
		}
		else
		{
			if ((isWiz())&&(!isDPower())&&(!canEnterZone(item.getZone())))
			{
				return false;
			}
			else
			{
				return super.canSee(item);
			}
		}
	}

	public Item getHomeRoom()
	{
		return homeroom;
	}

	public void setHomeRoom(Item room)
	{
		homeroom=room;
	}

	public String getTitle()
	{
		Variables vars = new Variables();
		vars.setVariable("%n",getName());
		vars.setVariable("%l",getLevelName());
		return vars.parseString(title);
	}

	public void setTitle(String newtitle)
	{
		title=newtitle;
	}

	public String getLastOn()
	{
		SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		return df.format(laston.getTime());
	}

	public void setTimeOn(long newtime)
	{
		timeon=newtime;
	}

	public long getCommandsRun()
	{
		return commandsrun;
	}

	public void setCommandsRun(long value)
	{
		commandsrun=value;
	}

	public long getRealTimeOn()
	{
		if (isConnected())
		{
			return timeon+(System.currentTimeMillis()-millislogon)/1000;
		}
		else
		{
			return timeon;
		}
	}

	public int getDaysOn()
	{
		return (int)(getRealTimeOn()/86400);
	}

	public long getAverageIdleTime()
	{
		return getRealTimeOn()/commandsrun;
	}

	public String getTimeOn()
	{
		long rltimeon=getRealTimeOn();
		long secs=rltimeon%60;
		long mins=(rltimeon/60)%60;
		long hours=(rltimeon/3600)%24;
		long days=rltimeon/86400;
		return days+" days, "+hours+" hours, "+mins+" minutes and "+secs+" seconds.";
	}

	public void setLastOn(String date)
	{
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			laston.setTime(df.parse(date));
		}
		catch (Exception e)
		{
		}
	}

	public Date getLogOnDate()
	{
		return logon.getTime();
	}

	public String getLogOn()
	{
		SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		return df.format(logon.getTime());
	}

	public void setLogOn(String date)
	{
		try
		{
			SimpleDateFormat df = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			logon.setTime(df.parse(date));
		}
		catch (Exception e)
		{
		}
	}

	public void setPassword(int newpassword)
	{
		password=newpassword;
	}

	public void setRealPassword(String newpassword)
	{
		password=newpassword.hashCode();
	}

	public boolean isPower()
	{
		return (checkFlag("power")&&checkFlag("immort"));
	}

	public boolean isDPower()
	{
		return ((checkFlag("dpower")&&checkFlag("immort"))||(isPower()));
	}

	public boolean isWiz()
	{
		return ((checkFlag("immort"))||(isDPower()));
	}

	public boolean canEnterZone(Zone zone)
	{
		if ((isWiz())&&(!isDPower()))
		{
			if ((zone.checkFlag("open"))||(checkFlag(zone.getIdentifier(),"quest")))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}

	public Player asPlayer()
	{
		return this;
	}

	public void cycle()
	{
		super.cycle();
		if (isRegistered())
		{
			if (isConnected())
			{
				long idle=(System.currentTimeMillis()-lastcommand)/1000;
				long max;
				long min;
				if (isDPower())
				{
					max=10*60*60;
					min=60*60;
				}
				else if (isWiz())
				{
					max=3*60*60;
					min=60*60;
				}
				else
				{
					if (getLevel()<50)
					{
						max=30*60;
					}
					else
					{
						max=60*60;
					}
					min=max;
				}
				if (idle>=max)
				{
					warned=true;
					getServer().sendWizMessage(getName()+" has idled out",getWorldIdentifier(),getVisibility(),1);
					displayText("You have been idle for too long.");
					getServer().parseCommand(this,"quit");
				}
				else if ((max-idle<=60)&&(!warned))
				{
					warned=true;
					displayText("\u0007You will idle out in 1 minute");
				}
				else if ((idle>=min)&&(isWiz()))
				{
					short aim;
					if (isPower())
					{
						aim=10000;
					}
					else
					{
						aim=getLevel();
					}
					if (getVisibility()<aim)
					{
						displayText("You have turned invisible.");
						zone.getWorld().getServer().sendWizMessage(getName()+" has gone invisible",getWorldIdentifier(),aim,1);
						if (!checkFlag("away"))
						{
							setFlag("away");
						}
						setVisibility(aim);
					}
				}
			}
			else
			{
				if (!checkFlag("linkdead"))
				{
					setFlag("linkdead");
					getServer().sendWizMessage(getName()+" has gone linkdead",getWorldIdentifier(),getVisibility(),1);
					linkdeadtick=System.currentTimeMillis();
				}
				if ((System.currentTimeMillis()-linkdeadtick)>900000)
				{
					getServer().sendWizMessage(getName()+" has timed out",getWorldIdentifier(),getVisibility(),1);
					getServer().parseCommand(this,"quit");
				}
			}
		}
		else
		{
			if ((location!=null)&&(!location.getWorldIdentifier().equals("players.loggedoff")))
			{
				getServer().parseCommand(this,"quit");
			}
		}
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (homeroom==oldref)
		{
			if (newref!=null)
			{
				homeroom=newref.asItem();
			}
			else
			{
				homeroom=zone.findItem("newbie.start");
			}
		}
		if (creationzone==oldref)
		{
			creationzone=newref.asZone();
		}
		super.updateReferences(oldref,newref);
	}
	
	public void run()
	{
		String data;
		String lastline = "";
		client.setBlocking(true);
		while ((!client.isClosing())&&(isConnected()))
		{
			while ((!client.isClosing())&&(isConnected())&&(!client.hasData()))
			{
				client.flush();
				if (hasMultiLine())
				{
					setFlag("multiline");
					client.printPrompt("\u001B[u\u001B[100D\u001B[J"+multiline.getFirstPrompt());
					client.flush();
					while ((!multiline.isFinished())&&(!client.isClosing())&&(isConnected()))
					{
						data=client.readLine();
						if (data!=null)
						{
							client.printPrompt(multiline.sendLine(data));
							client.flush();
						}
					}
					clearMultiLine();
					removeFlag("multiline");
				}
				try
				{
					Thread.sleep(100);
				}
				catch (Exception e)
				{
				}
			}
			if ((isConnected())&&(client.hasData())&&(!client.isClosing()))
			{
				data=client.readLine();
				if (data!=null)
				{
					while (data.startsWith(" "))
					{
						data=data.substring(1);
					}
					while (data.endsWith(" "))
					{
						data=data.substring(0,data.length()-1);
					}
					if (data.equals("!"))
					{
						data=lastline;
					}
					if (data.length()>0)
					{
						lastline=data;
						lastcommand = System.currentTimeMillis();
						warned=false;
						commandsrun++;
						zone.getWorld().getServer().sendCommand(this,data);
					}
					else
					{
						displayPrompt();
					}
				}
			}
		}
		client=null;
		laston=new GregorianCalendar();
		timeon=getRealTimeOn();
		setConnected(false);
	}
}
