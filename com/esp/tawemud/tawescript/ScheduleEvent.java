package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.Event;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Schedules a system event to run. Simply specify how far in the future the
 * event should run and when to warn the players.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ScheduleEvent extends TestAction
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
	 * A comma separated when the players should be warned (in minutes).
	 */
	public String warnings;
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
	/**
	 * The number of minutes before the event to start warning people.
	 *
	 * @default	-1
	 */
	public String start = "-1";
	/**
	 * The warning message to display to players.
	 *
	 * %m in the message will be substitued for the number of minutes till the
	 * event occurs.
	 */
	public String message = "";
	/**
	 * The visibility to warn at.
	 *
	 * @default	0
	 */
	public String visibility = "0";
	/**
	 * The wiz level to warn at:
	 *
	 * 1 = wizard
	 * 2 = demipower
	 * 3 = power
	 *
	 * @default	0
	 */
	public String wizlevel = "0";
	
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
		int wiz;
		try
		{
			wiz=Integer.parseInt(variables.parseString(wizlevel));
		}
		catch (NumberFormatException e)
		{
			wiz=0;
		}
		int vis;
		try
		{
			vis=Integer.parseInt(variables.parseString(visibility));
		}
		catch (NumberFormatException e)
		{
			vis=0;
		}
		long minsbefore;
		try
		{
			minsbefore=Integer.parseInt(variables.parseString(start));
		}
		catch (NumberFormatException e)
		{
			minsbefore=-1;
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
					LinkedList warningtimes = new LinkedList();
					StringTokenizer tokens = new StringTokenizer(variables.parseString(warnings),",");
					while (tokens.hasMoreTokens())
					{
						try
						{
							long mins = Long.parseLong(tokens.nextToken());
							mins=mins*60000;
							warningtimes.add(new Long(rltime-mins));
						}
						catch (Exception e)
						{
						}
					}
					if (minsbefore>=0)
					{
						minsbefore*=60000;
						minsbefore=rltime-minsbefore;
					}
					else
					{
						minsbefore=0;
					}
					Event newevent = new Event(minsbefore,rltime,warningtimes,vis,wiz,thisone,variables.parseString(message));
					server.addEvent(newevent);
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
