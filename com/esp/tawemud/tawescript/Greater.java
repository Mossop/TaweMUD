package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Tests if value1 is larger than value2.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Greater extends TestAction
{
	/**
	 * The first value.
	 *
	 * @required
	 */
	public String value1;
	/**
	 * The second value.
	 *
	 * @required
	 */
	public String value2;

	public boolean doTest(TaweServer server, Variables variables)
	{
		int val1 = Integer.parseInt(variables.parseString(value1));
		int val2 = Integer.parseInt(variables.parseString(value2));
		return val1>val2;
	}
}
