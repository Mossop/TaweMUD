package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Item;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;

/**
 * Iterates over all the items a mobile is wearing in a particular place.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateWornItems extends GroupAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The place the items are worn.
	 *
	 * @required
	 */
	public String place;
	/**
	 * A variable to hold te current item.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		CodeableObject item = variables.getObject(mobile,server);
		if ((item!=null)&&(item.asMobile()!=null))
		{
			Iterator items = item.asMobile().getWorn(variables.parseString(place));
			while (items.hasNext())
			{
				variables.setVariable(dest,(Item)items.next());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
