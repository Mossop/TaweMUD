package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Compares two strings. This is not a case sensitive check.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Compare extends TestAction
{
	/**
	 * The first string.
	 *
	 * @required
	 */
	public String string1;
	/**
	 * The second string.
	 *
	 * @required
	 */
	public String string2;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return variables.parseString(string1).equals(variables.parseString(string2));
	}
}
