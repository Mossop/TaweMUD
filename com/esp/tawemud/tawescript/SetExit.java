package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.Exit;

/**
 * Makes an exit linking two rooms.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetExit extends SingleAction
{
	/**
	 * The starting room.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The direction for the exit.
	 *
	 * @required
	 */
	public String direction;
	/**
	 * The destination room.
	 *
	 * @required
	 */
	public String dest;
	/**
	 * The type of exit, see DelExit for more information.
	 */
	public String type;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(item,server);
		if ((test!=null)&&(test.asRoom()!=null))
		{
			int direc = Room.getDirection(variables.parseString(direction));
			if (direc>-1)
			{
				CodeableObject destroom = variables.getObject(dest,server);
				if ((destroom!=null)&&(destroom.asRoom()!=null))
				{
					if (variables.parseString(type).equals("start"))
					{
						test.asRoom().addStartExit(new Exit(test.asRoom(),variables.parseString(dest)),direc);
					}
					else
					{
						test.asRoom().addExit(new Exit(test.asRoom(),variables.parseString(dest)),direc);
					}
				}
			}
		}
		return false;
	}
}