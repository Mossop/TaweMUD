package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.items.Mobile;

/**
 * Searches a room for a mobile with a given name.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FindMobile extends TestAction
{
	/**
	 * The room to look in.
	 *
	 * @required
	 */
	public String container;
	/**
	 * The name of the mobile to look for.
	 *
	 * @required
	 */
	public String name;
	/**
	 * The variable to hold the mobiles identifier.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject place = variables.getObject(container,server);
		if ((place!=null)&&(place.asRoom()!=null))
		{
			Mobile item = place.asRoom().findMobileByName(variables.parseString(name));
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
