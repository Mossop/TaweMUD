package com.esp.tawemud.tawescript;

import com.esp.tawemud.Zone;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterates over all the items in a zone.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateZoneItems extends GroupAction
{
	/**
	 * The zone.
	 *
	 * @required
	 */
	public String zone;
	/**
	 * A variable to hold the items.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		try
		{
			Zone thiszone = (Zone)variables.getObject(zone,server);
			if (thiszone!=null)
			{
				Iterator loop = thiszone.getItemIterator();
				LinkedList items = new LinkedList();
				while (loop.hasNext())
				{
					items.add(loop.next());
				}
				loop=items.iterator();
				while (loop.hasNext())
				{
					variables.setVariable(dest,(Item)loop.next());
					result=super.run(server,variables)||result;
				}
			}
			return result;
		}
		catch (Exception e)
		{
			return false;
		}
	}
}
