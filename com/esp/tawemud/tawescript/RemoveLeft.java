package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;

/**
 * Removes a numer of characters from the start of a string.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class RemoveLeft extends ContentAction
{
	/**
	 * The number of characters to remove.
	 *
	 * @required
	 */
	public String length;
	/**
	 * A variable to hold the resulting string.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		int rllength = 0;
		try
		{
			rllength = Integer.parseInt(variables.parseString(length));
		}
		catch (Exception e)
		{
			rllength=0;
		}
		String mess = variables.parseString(contents.toString());
		if (rllength<mess.length())
		{
			mess=mess.substring(rllength);
		}
		else
		{
			mess="";
		}
		variables.setVariable(dest,mess);
		return false;
	}
}