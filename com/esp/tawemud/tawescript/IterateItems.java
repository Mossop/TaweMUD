package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Container;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.util.ListIterator;
import java.util.LinkedList;

/**
 * Iterates over all items in a container.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateItems extends GroupAction
{
	/**
	 * The container
	 *
	 * @required
	 */
	public String container;
	/**
	 * A variable to hold the current item.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		CodeableObject item = variables.getObject(container,server);
		if ((item!=null)&&(item.asContainer()!=null))
		{
			LinkedList list = new LinkedList();
			ListIterator items = item.asContainer().getItemContentsIterator();
			while (items.hasNext())
			{
				list.add(items.next());
			}
			items=list.listIterator();
			while (items.hasNext())
			{
				variables.setVariable(dest,(Item)items.next());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
