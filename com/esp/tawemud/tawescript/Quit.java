package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Disconnects a player from the game.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Quit extends SingleAction
{
	/**
	 * The player to be quitted.
	 *
	 * @required
	 */
	public String item;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisone = variables.getObject(item,server);
		if ((thisone!=null)&&(thisone.asMobile()!=null))
		{
			thisone.asMobile().quit();
		}
		return false;
	}
}
