package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;

/**
 * Iterates over all the places on the body an item takes up when worn.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateWornPlaces extends GroupAction
{
	/**
	 * The item to check.
	 *
	 * @required
	 */
	public String item;
	/**
	 * A varaible to hold each place.
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
			Iterator places = rlitem.asItem().getWearPlaces().iterator();
			while (places.hasNext())
			{
				variables.setVariable(dest,places.next().toString());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
