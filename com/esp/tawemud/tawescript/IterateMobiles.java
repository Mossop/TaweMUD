package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Item;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.util.ListIterator;
import java.util.LinkedList;

/**
 * Iterates over all the mobiles in a room.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateMobiles extends GroupAction
{
	/**
	 * The room to look in.
	 *
	 * @required
	 */
	public String container;
	/**
	 * A variable to hold the current mobile.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		CodeableObject item = variables.getObject(container,server);
		if ((item!=null)&&(item.asRoom()!=null))
		{
			ListIterator mobiles = item.asRoom().getMobileContentsIterator();
			LinkedList list = new LinkedList();
			while (mobiles.hasNext())
			{
				list.add(mobiles.next());
			}
			mobiles=list.listIterator();
			while (mobiles.hasNext())
			{
				variables.setVariable(dest,(Item)mobiles.next());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
