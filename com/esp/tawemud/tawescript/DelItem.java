package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Container;
import com.esp.tawemud.CodeableObject;
import java.util.Vector;
import java.util.Iterator;

/**
 * Deletes an item from the world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class DelItem extends SingleAction
{
	/**
	 * The item to be deleted.
	 *
	 * @required
	 */
	public String item;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		if ((thisitem!=null)&&(thisitem.asItem()!=null))
		{
			if (thisitem.asContainer()!=null)
			{
				Vector contents = new Vector();
				Iterator items = thisitem.asContainer().getItemContentsIterator();
				while (items.hasNext())
				{
					contents.add(items.next());
				}
				if (thisitem.asRoom()!=null)
				{
					items=thisitem.asRoom().getMobileContentsIterator();
					while (items.hasNext())
					{
						contents.add(items.next());
					}
				}
				if (contents.size()>0)
				{
					Item moved;
					Container bin = server.getWorld().findItem("permanent.bin").asContainer();
					for (int loop=0; loop<contents.size(); loop++)
					{
						moved = (Item)contents.elementAt(loop);
						moved.setLocation(bin);
					}
				}
			}
			thisitem.asItem().delete();
		}
		return false;
	}
}
