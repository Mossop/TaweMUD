package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Moves an item to a new container, triggering any necessary specials.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Move extends TestAction
{
	/**
	 * The item.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The new location./
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject thisone = variables.getObject(item,server);
		CodeableObject rldest = variables.getObject(dest,server);
		if ((rldest!=null)&&(thisone!=null)&&(thisone.asItem()!=null)&&(rldest.asContainer()!=null))
		{
			if ((thisone.asMobile()==null)||(thisone.asMobile().canEnterZone(rldest.asItem().getZone())))
			{
				server.doMove(thisone.asItem(),rldest.asContainer());
				return true;
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
