package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;

/**
 * Tests if a string has no value.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsNull extends TestAction
{
	/**
	 * The string to check.
	 *
	 * @required
	 */
	public String string;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return (string.equals(variables.parseString(string)))||(variables.parseString(string).equals(""));
	}
}
