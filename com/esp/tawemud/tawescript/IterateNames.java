package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import java.util.Vector;

/**
 * Iterates over all the names an item has.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateNames extends GroupAction
{
	/**
	 * The item.
	 *
	 * @required
	 */
	public String item;
	/**
	 * A variable to hold the names.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		CodeableObject rlitem = variables.getObject(item,server);
		if ((rlitem!=null)&&(rlitem.asItem()!=null))
		{
			Vector contents = new Vector(rlitem.asItem().getNames());
			for (int loop=0; loop<contents.size(); loop++)
			{
				variables.setVariable(dest,contents.elementAt(loop).toString());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
