package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;

/**
 * Returns a substring from the given text.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Substring extends ContentAction
{
	/**
	 * The character to start from.
	 *
	 * @default 0
	 */
	public String start;
	/**
	 * The character to end with.
	 * Defaults to the end of the text.
	 */
	public String end;
	/**
	 * A variable to hold the substring.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String mess = variables.parseString(contents.toString());
		int rlstart = 0;
		try
		{
			rlstart = Integer.parseInt(variables.parseString(start));
		}
		catch (Exception e)
		{
			rlstart=0;
		}
		int rlend = mess.length();
		try
		{
			rlend = Integer.parseInt(variables.parseString(end));
		}
		catch (Exception e)
		{
			rlend=0;
		}
		if (rlend>mess.length())
		{
			rlend=mess.length();
		}
		if (rlstart<0)
		{
			rlstart=0;
		}
		if (rlstart>=rlend)
		{
			mess="";
		}
		else
		{
			mess=mess.substring(rlstart,rlend);
		}
		variables.setVariable(dest,mess);
		return false;
	}
}