package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Tests if a player is a Power.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsPower extends TestAction
{
	/**
	 * The player to check.
	 *
	 * @required
	 */
	public String item;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(item,server);
		if ((test!=null)&&(test.asPlayer()!=null))
		{
			return (test.asPlayer().isPower());
		}
		else
		{
			return false;
		}
	}
}
