package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Resets a zone.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Reset extends TestAction
{
	/**
	 * The zone to reset.
	 *
	 * @required
	 */
	public String zone;

	public boolean doTest(TaweServer server, Variables variables)
	{
		if (zone.length()==0)
		{
			server.getWorld().reset();
			return true;
		}
		else
		{
			CodeableObject test = variables.getObject(zone,server);
			if ((test!=null)&&(test.asZone()!=null))
			{
				test.asZone().reset();
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
