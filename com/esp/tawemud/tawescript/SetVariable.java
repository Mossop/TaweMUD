package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Sets a dynamic variable on an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetVariable extends SingleAction
{
	/**
	 * The item to set the variable on.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The name of the variable.
	 *
	 * @required
	 */
	public String name;
	/**
	 * The value to set the variable to.
	 *
	 * @required
	 */
	public String value;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		if (thisitem!=null)
		{
			thisitem.setVariable(variables.parseString(name),variables.parseString(value));
		}
		return false;
	}
}
