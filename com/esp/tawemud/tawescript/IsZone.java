package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Tests the type of the item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsZone extends TestAction
{
	/**
	 * The identifier to check.
	 *
	 * @required
	 */
	public String string;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(string,server);
		if (test!=null)
		{
			return (test.asZone()!=null);
		}
		else
		{
			return false;
		}
	}
}
