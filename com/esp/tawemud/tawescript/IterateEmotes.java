package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Player;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Emote;
import java.util.Iterator;

/**
 * Iterates over all the fixed emotes.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateEmotes extends GroupAction
{
	/**
	 * A variable to hold the current emote.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		Iterator loop = server.getWorld().getEmoteIterator();
		while (loop.hasNext())
		{
			Emote thisone = (Emote)loop.next();
			variables.setVariable(dest,thisone.getName());
			result=super.run(server,variables)||result;
		}
		return result;
	}
}
