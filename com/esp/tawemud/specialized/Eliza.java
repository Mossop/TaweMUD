package com.esp.tawemud.specialized;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class Eliza
{
	/**
	 * The array of key words to trigger responses.
	 */
	private static final String[] keywords = 
							{"CAN YOU","CAN I","YOU ARE","YOURE","I DONT",
               "I FEEL","WHY DONT YOU","WHY CANT I","ARE YOU",
               "I CANT","I AM","I'M","YOU","I WANT","WHAT",
               "HOW","WHO","WHERE","WHEN","WHY","NAME","CAUSE",
               "SORRY","DREAM","HELLO","HI","MAYBE","NO",
               "YOUR","ALWAYS","THINK","ALIKE","YES","FRIEND",
               "COMPUTER","NO KEY FOUND","REPEAT INPUT"};

	/**
	 * Contains the start index to the response strings.
	 */
	private static final int[] keyindex = 
				{1, 4, 6, 6,10,14,17,20,22,25,
        28,28,32,35,40,40,40,40,40,40,
        49,51,55,59,63,63,64,69,74,76,
        80,83,90,93,99,106,113};

	/**
	 * Contains the end index to the response strings.
	 */
	private static final int[] keyend = 
				{3, 5, 9, 9,13,16,19,21,24,27,
        32,32,34,39,48,48,48,48,48,48,
        50,54,58,62,68,63,68,73,75,79,
        82,89,92,98,105,112,116};

	/**
	 * The words that need to be changed for the response.
	 */
	private static final String[] conjarray1 =
    		{"are","were","you","your","Ive","Im","me"};
  /**
   * And the words to change them to.
 	 */  		
	private static final String[] conjarray2 =
    		{"am","was","me","my","you've","you're","you"};

	private String lasttext = null;
	
	private List responses;
	
	private int[] responseindex;
	
	private StringWriter buffer;
	
	private PrintWriter log;
	
	public Eliza(String responsefile)
	{
		super();
		buffer = new StringWriter();
		log = new PrintWriter(buffer);
		responses = new ArrayList();
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(responsefile));
			String line = in.readLine();
			while (line!=null)
			{
				responses.add(line);
				line=in.readLine();
			}
			in.close();
		}
		catch (IOException e)
		{
			System.err.println("Error reading response file - "+e.getMessage());
			e.printStackTrace();
		}
		resetSession();
	}
	
	public void resetSession()
	{
		responseindex = new int[keyindex.length];
		for (int loop=0; loop<keyindex.length; loop++)
		{
			responseindex[loop]=keyindex[loop];
		}
		lasttext=null;
	}
	
	private String trim(String text)
	{
		while (!Character.isJavaIdentifierPart(text.charAt(0)))
		{
			text=text.substring(1);
		}
		while (!Character.isJavaIdentifierPart(text.charAt(text.length()-1)))
		{
			text=text.substring(0,text.length()-1);
		}
		int pos = text.indexOf('\'');
		while (pos>=0)
		{
			text=text.substring(0,pos)+text.substring(pos+1);
		}
		return text;
	}
	
	private int findKey(StringBuffer text)
	{
		String scantext=text.toString().toUpperCase();
		int loop=0;
		int pos;
		boolean found=false;
		while ((!found)&&(loop<keywords.length))
		{
			pos=scantext.indexOf(keywords[loop]);
			if ((pos>=0)&&((pos==0)||(scantext.charAt(pos-1)==' '))&&(((pos+keywords[loop].length())==scantext.length())||(scantext.charAt(pos+keywords[loop].length())==' ')))
			{
				found=true;
				text.delete(0,scantext.indexOf(keywords[loop])+keywords[loop].length());
				if (text.length()>0)
				{
					text.delete(0,1);
				}
			}
			else
			{
				loop++;
			}
		}
		if (found)
		{
			return loop;
		}
		else
		{
			return keywords.length-2;
		}
	}
	
	private void conjugate(StringBuffer text)
	{
		int pos;
		for (int loop=0; loop<conjarray1.length; loop++)
		{
			do
			{
				pos=text.toString().indexOf(conjarray1[loop]);
				if ((pos>=0)&&((pos==0)||(text.charAt(pos-1)==' '))&&(((pos+conjarray1[loop].length())==text.length())||(text.charAt(pos+conjarray1[loop].length())==' ')))
				{
					text.delete(pos,pos+conjarray1[loop].length());
					text.insert(pos,conjarray2[loop]);
				}
			} while (pos>=0);
		}
	}
	
	private void addResponse(StringBuffer text, int key)
	{
		String start = (String)responses.get(responseindex[key]);
		responseindex[key]++;
		if (responseindex[key]>keyend[key])
		{
			responseindex[key]=keyindex[key];
		}
		if (start.endsWith("*"))
		{
			start=start.substring(0,start.length()-1);
			text.insert(0,start+" ");
			text.append("?");
		}
		else if (start.endsWith("@"))
		{
			start=start.substring(0,start.length()-1);
			text.insert(0,start+" ");
			text.append(".");
		}
		else
		{
			text.delete(0,text.length());
			text.append(start);
		}
	}
	
	public String getResponse(String text)
	{
		log.println("User  : "+text);
		text=trim(text);
		StringBuffer response = new StringBuffer(text);
		int key;
		if (text.equalsIgnoreCase(lasttext))
		{
			key=keywords.length-1;
		}
		else
		{
			key = findKey(response);
		}
		lasttext=text;
		conjugate(response);
		addResponse(response,key);
		log.println("Eliza : "+response);
		return response.toString();
	}
	
	public String getLog()
	{
		log.flush();
		return buffer.toString();
	}
}
