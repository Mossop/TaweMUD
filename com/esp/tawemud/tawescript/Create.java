package com.esp.tawemud.tawescript;

import java.lang.reflect.Constructor;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;

/**
 * Creates a new item in the world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Create extends TestAction
{
	/**
	 * The zone to create the item in.
	 *
	 * @required
	 */
	public String zone;
	/**
	 * The type of item to create.
	 *
	 * @required
	 */
	public String type;
	/**
	 * The new identifier for the item.
	 *
	 * @required
	 */
	public String identifier;

	public boolean doTest(TaweServer server, Variables variables)
	{
		String rltype = variables.parseString(type);
		String rlid = variables.parseString(identifier);
		Zone rlzone = server.getWorld().findZone(variables.parseString(zone));
		if (rlzone!=null)
		{
			if (rlid.indexOf(".")<0)
			{
				Item thisitem	= rlzone.getDefaultItem(rltype);
				if (thisitem!=null)
				{
					thisitem.setIdentifier(rlid);
					thisitem.setZone(rlzone);
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
		else
		{
			return false;
		}
	}
}
