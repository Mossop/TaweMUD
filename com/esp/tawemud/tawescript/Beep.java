package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;

/**
 * Sends a beep to a player.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Beep extends SingleAction
{
	/**
	 * The player to be beeped. Must be a player.
	 *
	 * @required
	 */
	public String player;

	public boolean doAction(TaweServer server, Variables variables)
	{
		try
		{
			Item test = (Item)variables.getObject(player,server);
			if ((test!=null)&&(test.asPlayer()!=null))
			{
				test.asPlayer().displayPrompt("\u0007");
			}
		}
		catch (Exception e)
		{
		}
		return false;
	}
}
