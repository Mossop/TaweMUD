package com.esp.tawemud.tawescript;

import com.esp.tawemud.Zone;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;

/**
 * Iterates over all the zones in the world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateZones extends GroupAction
{
	/**
	 * A variable to hold each zone.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		Iterator loop = server.getWorld().getZoneIterator();
		while (loop.hasNext())
		{
			variables.setVariable(dest,(Zone)loop.next());
			result=super.run(server,variables)||result;
		}
		return result;
	}
}
