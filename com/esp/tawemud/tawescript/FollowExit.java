package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;

/**
 * Runs specials on an exit. If the test returns true then for some special
 * action was taken and nothing more needs be done.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FollowExit extends TestAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The direction of the exit.
	 *
	 * @required
	 */
	public String direction;

	public boolean doTest(TaweServer server, Variables variables)
	{
		boolean result = false;
		int dir = Room.getDirection(variables.parseString(direction));
		CodeableObject thisone = variables.getObject(item,server);
		if ((thisone!=null)&&(thisone.asMobile()!=null)&&(dir>-1))
		{
			Room room = thisone.asMobile().getLocation().asRoom();
			Exit exit = room.getExit(dir);
			Item dest = server.getWorld().findItem(exit.getDestination());
			if (dest!=null)
			{
				result=!thisone.asMobile().canEnterZone(dest.getZone());
			}
			if (!result)
			{
				if (exit!=null)
				{
					result=exit.runSpecials(thisone.asMobile());
				}
			}
			else
			{
				thisone.asMobile().displayText("You cant go there!");
			}
		}
		return result;
	}
}
