package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Moves an item to a new loction without triggering any specials.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class QuietMove extends TestAction
{
	/**
	 * The item to move.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The new location.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject thisone = variables.getObject(item,server);
		CodeableObject rldest = variables.getObject(dest,server);
		if ((rldest!=null)&&(thisone!=null)&&(rldest.asContainer()!=null)&&(thisone.asItem()!=null))
		{
			if ((thisone.asMobile()==null)||(thisone.asMobile().canEnterZone(rldest.asItem().getZone())))
			{
				thisone.asItem().setLocation(rldest.asContainer());
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