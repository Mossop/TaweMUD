package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;

/**
 * Gets the destination of an exit.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetExit extends SingleAction
{
	/**
	 * The room to get the exit from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The direction the exit is in.
	 *
	 * @required
	 */
	public String direction;
	/**
	 * A variable to hold the destination.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String realdir = variables.parseString(direction);
		CodeableObject room = variables.getObject(item,server);
		if ((room!=null)&&(room.asRoom()!=null))
		{
			Exit exit = room.asRoom().getExit(Room.getDirection(realdir));
			if (exit!=null)
			{
				variables.setVariable(dest,exit.getDestination());
			}
		}
		return false;
	}
}
