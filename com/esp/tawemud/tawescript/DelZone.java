package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Deletes a zone from the world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class DelZone extends SingleAction
{
	/**
	 * The zone to be deleted.
	 *
	 * @required
	 */
	public String zone;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thiszone = variables.getObject(zone,server);
		if ((thiszone!=null)&&(thiszone.asZone()!=null))
		{
			server.getWorld().removeZone(thiszone.asZone());
		}
		return false;
	}
}
