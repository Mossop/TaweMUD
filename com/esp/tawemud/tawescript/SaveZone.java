package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Saves a zone.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SaveZone extends TestAction
{
	/**
	 * The zone to save.
	 *
	 * @required
	 */
	public String zone;

	public boolean doTest(TaweServer server, Variables variables)
	{
		if (zone.length()==0)
		{
			server.getWorld().save();
			return true;
		}
		else
		{
			CodeableObject test = variables.getObject(zone,server);
			if ((test!=null)&&(test.asZone()!=null))
			{
				return test.asZone().save();
			}
			else
			{
				return false;
			}
		}
	}
}
