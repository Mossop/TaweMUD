package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Checks if a string is a valid number.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsNumeric extends TestAction
{
	/**
	 * The string to check.
	 *
	 * @required
	 */
	public String string;

	public boolean doTest(TaweServer server, Variables variables)
	{
		try
		{
			Integer.parseInt(variables.parseString(string));
			return true;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
