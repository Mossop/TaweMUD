package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Kills a mobile in the game.
 * Simply quits the mobile, then resets the mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Kill extends SingleAction
{
	/**
	 * The player to be killed.
	 *
	 * @required
	 */
	public String item;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisone = variables.getObject(item,server);
		if ((thisone!=null)&&(thisone.asMobile()!=null))
		{
			thisone.asMobile().kill();
		}
		return false;
	}
}
