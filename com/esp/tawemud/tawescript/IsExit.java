package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;
import com.esp.tawemud.CodeableObject;

/**
 * Tests if an exit exists.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsExit extends TestAction
{
	/**
	 * The room to check in.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The direction to check against.
	 *
	 * @required
	 */
	public String direction;

	public boolean doTest(TaweServer server, Variables variables)
	{
		String realdir = variables.parseString(direction);
		CodeableObject room = variables.getObject(item,server);
		if ((room!=null)&&(room.asRoom()!=null))
		{
			Exit exit = room.asRoom().getExit(Room.getDirection(realdir));
			return (exit!=null);
		}
		else
		{
			return false;
		}
	}
}
