package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Removes a name from an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class DelName extends SingleAction
{
	/**
	 * The item to remove the name from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The name to be removed.
	 *
	 * @required
	 */
	public String name;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		if ((thisitem!=null)&&(thisitem.asItem()!=null))
		{
			thisitem.asItem().delName(new StringBuffer(variables.parseString(name)));
		}
		return false;
	}
}
