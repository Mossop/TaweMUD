package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Sets a flag on an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetFlag extends SingleAction
{
	/**
	 * The item to set the flag on.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The flag to set.
	 *
	 * @required
	 */
	public String flag;
	/**
	 * The type of flag to set. See Check for more information.
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
			thisitem.setFlag(variables.parseString(flag),rltype);
		}
		return false;
	}
}
