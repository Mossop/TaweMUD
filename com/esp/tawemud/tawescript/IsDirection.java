package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;

/**
 * Checks if a string can be understood as a direction.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsDirection extends TestAction
{
	/**
	 * The string to check.
	 *
	 * @required
	 */
	public String direction;

	public boolean doTest(TaweServer server, Variables variables)
	{
		String real = variables.parseString(direction);
		return (Room.getDirection(real)>-1);
	}
}