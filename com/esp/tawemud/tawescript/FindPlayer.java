package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Player;
import java.util.Iterator;

/**
 * Finds a currently logged in player anywhere in the world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FindPlayer extends TestAction
{
	/**
	 * The name of the player.
	 *
	 * @required
	 */
	public String name;
	/**
	 * The variable to hold the players identifier.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		Iterator loop = server.getPlayers();
		boolean result = false;
		String realname = variables.parseString(name);
		while (loop.hasNext())
		{
			Player thisone = (Player)loop.next();
			if (thisone.hasName(realname))
			{
				result=true;
				variables.setVariable(dest,thisone);
			}
		}
		return result;
	}
}
