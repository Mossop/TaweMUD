package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.CodeableObject;

/**
 * Searches for an item with a given name in a container.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FindItem extends TestAction
{
	/**
	 * The container to search in.
	 *
	 * @required
	 */
	public String container;
	/**
	 * The name of the item to look for.
	 *
	 * @required
	 */
	public String name;
	/**
	 * The variable to hold the items identifier.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject place = variables.getObject(container,server);
		if ((place!=null)&&(place.asContainer()!=null))
		{
			Item item = place.asContainer().findItemByName(variables.parseString(name));
			if (item!=null)
			{
				variables.setVariable(dest,item);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
