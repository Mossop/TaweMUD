package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;

/**
 * Removes an exit from a room
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class DelExit extends SingleAction
{
	/**
	 * The room holding the exit.
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
	/**
	 * The type of exit. If this is start then the reset exit will be removed, otherwise the
	 * current exit will be removed.
	 */
	public String type;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String realdir = variables.parseString(direction);
		CodeableObject room = variables.getObject(item,server);
		if ((room!=null)&&(room.asRoom()!=null))
		{
			if (variables.parseString(type).equals("start"))
			{
				room.asRoom().removeStartExit(Room.getDirection(realdir));
			}
			else
			{
				room.asRoom().removeExit(Room.getDirection(realdir));
			}
		}
		return false;
	}
}
