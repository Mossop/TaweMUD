package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;

/**
 * Adds padding to the left of a string to force it up to a certain length.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class PadLeft extends ContentAction
{
	/**
	 * The length the text should be.
	 *
	 * @required
	 */
	public String length;
	/**
	 * The characters to pad with.
	 *
	 * @default " "
	 */
	public String pad=" ";
	/**
	 * The variable to hold the result.
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String padding=variables.parseString(pad);
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
		int current=PlayerIO.stripColour(mess).length();
		while (current<rllength)
		{
			mess=padding+mess;
			current++;
		}
		if (current>rllength)
		{
			mess=mess.substring(0,mess.length()-(current-rllength));
		}
		variables.setVariable(dest,mess);
		return false;
	}
}