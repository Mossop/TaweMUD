package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.util.Iterator;

/**
 * Schedules an autorun special to run. Simply specify how far in the future the
 * special should run.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Schedule extends TestAction
{
	/**
	 * The item with the special.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The identifier of the special.
	 *
	 * @required
	 */
	public String special;
	/**
	 * The number of hours.
	 *
	 * @default 0
	 */
	public String hours = "0";
	/**
	 * The number of minutes.
	 *
	 * @default 0
	 */
	public String minutes = "0";
	/**
	 * The number of seconds.
	 *
	 * @default 0
	 */
	public String seconds = "0";
	/**
	 * The number of milliseconds.
	 *
	 * @default 0
	 */
	public String millis = "0";

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(item,server);
		int rlhours;
		try
		{
			rlhours=Integer.parseInt(variables.parseString(hours));
		}
		catch (NumberFormatException e)
		{
			rlhours=0;
		}
		int rlminutes;
		try
		{
			rlminutes=Integer.parseInt(variables.parseString(minutes));
		}
		catch (NumberFormatException e)
		{
			rlminutes=0;
		}
		int rlseconds;
		try
		{
			rlseconds=Integer.parseInt(variables.parseString(seconds));
		}
		catch (NumberFormatException e)
		{
			rlseconds=0;
		}
		int rlmillis;
		try
		{
			rlmillis=Integer.parseInt(variables.parseString(millis));
		}
		catch (NumberFormatException e)
		{
			rlmillis=0;
		}
		if (test!=null)
		{
			Iterator loop = test.getSpecialsIterator();
			boolean found=false;
			while ((!found)&&(loop.hasNext()))
			{
				Special thisone = (Special)loop.next();
				if ((thisone.getType()==Special.ST_AUTORUN)&&(thisone.getName().equalsIgnoreCase(variables.parseString(special))))
				{
					found=true;
					long rltime = System.currentTimeMillis()+rlmillis+1000*(rlseconds+60*(rlminutes+60*(rlhours)));
					thisone.setNextTime(rltime);
				}
			}
			return found;
		}
		else
		{
			return false;
		}
	}
}
