package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Tests if a player is a Wizard.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsWiz extends TestAction
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
			return (test.asPlayer().isWiz());
		}
		else
		{
			return false;
		}
	}
}
