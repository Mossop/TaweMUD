package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Checks to see if some text is contained in a larger string.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Contains extends TestAction
{
	/**
	 * The string to check within.
	 *
	 * @required
	 */
	public String string;
	/**
	 * The substring to look for.
	 *
	 * @required
	 */
	public String sub;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return (variables.parseString(string).toLowerCase().indexOf(variables.parseString(sub).toLowerCase())>=0);
	}
}
