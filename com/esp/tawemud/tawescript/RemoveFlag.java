package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Removes a flag from an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class RemoveFlag extends SingleAction
{
	/**
	 * The item to remove the flag from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The flag to remove.
	 *
	 * @required
	 */
	public String flag;
	/**
	 * The type of flag to remove, see Check for more information.
	 *
	 * @default normal
	 */
	public String type = "normal";

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		String rltype=variables.parseString(type);
		if (thisitem!=null)
		{
			if (rltype.length()==0)
			{
				rltype="normal";
			}
			thisitem.removeFlag(variables.parseString(flag),rltype);
		}
		return false;
	}
}
