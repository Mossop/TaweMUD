package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Gets a dynamically created variable from an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetVariable extends SingleAction
{
	/**
	 * The item to get the variable from.
	 */
	public String item;
	/**
	 * The name of the variable.
	 *
	 * @required
	 */
	public String name;
	/**
	 * A variable to hold the value.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		if (thisitem!=null)
		{
			variables.setVariable(dest,thisitem.getVariable(variables.parseString(name)));
		}
		return false;
	}
}
