package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Tests if a mobile can see an object.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CanSee extends TestAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The item to test.
	 *
	 * @required
	 */
	public String target;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject rlmob = variables.getObject(mobile,server);
		CodeableObject rltarget = variables.getObject(target,server);
		if ((rlmob!=null)&&(rlmob.asMobile()!=null)&&(rltarget!=null)&&(rltarget.asItem()!=null))
		{
			return rlmob.asMobile().canSee(rltarget.asItem());
		}
		else
		{
			return false;
		}
	}
}
