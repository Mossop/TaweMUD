package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;

/**
 * Adds a name to an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class AddName extends SingleAction
{
	/**
	 * The item to add the name to.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The name to be added.
	 *
	 * @required
	 */
	public String name;

	public boolean doAction(TaweServer server, Variables variables)
	{
		try
		{
			Item thisitem = (Item)variables.getObject(item,server);
			thisitem.addName(new StringBuffer(variables.parseString(name)));
		}
		catch (Exception e)
		{
		}
		return false;
	}
}
