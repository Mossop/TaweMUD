package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.CodeableObject;

/**
 * Tests whether a mobile may enter a room.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CanEnter extends TestAction
{
	/**
	 * The mobile to test with.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The room to test against.
	 *
	 * @required
	 */
	public String room;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject rlmob = variables.getObject(mobile,server);
		CodeableObject rltarget = variables.getObject(room,server);
		if ((rlmob!=null)&&(rlmob.asMobile()!=null)&&(rltarget!=null))
		{
			Zone zone = null;
			if (rltarget.asZone()!=null)
			{
				zone=rltarget.asZone();
			}
			else if (rltarget.asItem()!=null)
			{
				zone=rltarget.asItem().getZone();
			}
			if (zone!=null)
			{
				return rlmob.asMobile().canEnterZone(zone);
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
