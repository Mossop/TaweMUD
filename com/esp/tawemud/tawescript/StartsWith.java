package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Checks if some text starts with a specified string.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class StartsWith extends TestAction
{
	/**
	 * The text to check.
	 *
	 * @required
	 */
	public String string;
	/**
	 * The string to look for at the start of the text.
	 *
	 * @required
	 */
	public String sub;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return variables.parseString(string).toLowerCase().startsWith(variables.parseString(sub).toLowerCase());
	}
}
