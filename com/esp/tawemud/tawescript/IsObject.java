package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Tests the type of the item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsObject extends TestAction
{
	/**
	 * The identifier to check.
	 *
	 * @required
	 */
	public String item;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(item,server);
		return (test!=null);
	}
}
