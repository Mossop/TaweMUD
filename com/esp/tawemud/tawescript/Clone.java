package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Item;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Clones an existing item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Clone extends TestAction
{
	/**
	 * The item to be cloned.
	 *
	 * @required
	 */
	public String item;
	/**
	 * An identifier for the new item.
	 *
	 * @required
	 */
	public String identifier;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		if ((thisitem!=null)&&(thisitem.asItem()!=null))
		{
			String rlid = variables.parseString(identifier);
			if (rlid.indexOf(".")<0)
			{
				Item newitem = thisitem.asItem().clone(rlid);
				return (newitem!=null);
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
