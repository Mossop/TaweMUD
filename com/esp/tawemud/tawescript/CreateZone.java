package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;

/**
 * Creates a new zone.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CreateZone extends SingleAction
{
	/**
	 * The identifier for the new zone.
	 *
	 * @required
	 */
	public String string;

	public boolean doAction(TaweServer server, Variables variables)
	{
		Zone newzone = new Zone(server.getWorld());
		newzone.setIdentifier(variables.parseString(string));
		server.getWorld().addZone(newzone);
		return false;
	}
}
