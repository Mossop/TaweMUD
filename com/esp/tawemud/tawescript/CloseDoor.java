package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Closes a door pair.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CloseDoor extends SingleAction
{
	/**
	 * The door to be closed.
	 *
	 * @required
	 */
	public String door;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(door,server);
		if ((thisitem!=null)&&(thisitem.asDoor()!=null))
		{
			thisitem.asDoor().close();
		}
		return false;
	}
}
