package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Checks if a player is logged in.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsLoggedIn extends TestAction
{
	/**
	 * The player to check.
	 *
	 * @required
	 */
	public String player;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(player,server);
		if ((test!=null)&&(test.asPlayer()!=null))
		{
			return (test.asPlayer().isConnected());
		}
		else
		{
			return false;
		}
	}
}
