package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Level;
import java.text.SimpleDateFormat;

/**
 * Gets some information about a level.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetLevelInfo extends SingleAction
{
	/**
	 * The level to get information on.
	 *
	 * @required
	 */
	public String level;
	/**
	 * A variable to hold the number of quest points required.
	 *
	 * @required
	 */
	public String qpoints;
	/**
	 * A variable to hold the experience required.
	 *
	 * @required
	 */
	public String experience;

	public boolean doAction(TaweServer server, Variables variables)
	{
		int lev=0;
		try
		{
			lev=Integer.parseInt(variables.parseString(level));
		}
		catch (Exception e)
		{
		}
		Level thisone=server.getWorld().getLevel(lev);
		if (thisone!=null)
		{
			variables.setVariable(qpoints,Integer.toString(thisone.getQPoints()));
			variables.setVariable(experience,Integer.toString(thisone.getExperience()));
		}
		else
		{
			qpoints="-1";
			experience="-1";
		}
		return false;
	}
}