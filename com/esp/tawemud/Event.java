package com.esp.tawemud;

import com.esp.tawemud.tawescript.Special;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.items.Mobile;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;

/**
 * When a system task must be scheduled, an event object is created. It is cycled with the
 * server.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Event
{
	/**
	 * Dont tell anyone about the event until after this time.
	 */
	private long start;
	/**
	 * The time that the special should get run.
	 */
	private long time;
	/**
	 * A list of times when a warning should be given.
	 */
	private List warnings;
	/**
	 * The visibility of the warnings.
	 */
	private int visibility;
	/**
	 * The wiz level of the warnings.
	 */
	private int wizlevel;
	/**
	 * The message to display.
	 */
	private String message;
	/**
	 * The special to run.
	 */
	private Special special;
	
	public Event(long start, long time, List warningtimes, int vis, int wiz, Special special, String message)
	{
		this.start=start;
		this.time=time;
		visibility=vis;
		wizlevel=wiz;
		this.special=special;
		this.message=message;
		warnings = new LinkedList(warningtimes);
		Collections.sort(warnings);
	}
	
	public void notify(Mobile mobile)
	{
		long currenttime=System.currentTimeMillis();
		if (currenttime>=start)
		{
			List needflags = new LinkedList();
			String colour = "";
			boolean good=true;
			if (wizlevel==1)
			{
				good=mobile.isWiz();
				colour="@+M";
			}
			else if (wizlevel==2)
			{
				good=mobile.isDPower();
				colour="@+G";
			}
			else if (wizlevel==3)
			{
				good=mobile.isPower();
				colour="@+R";
			}
			if (good)
			{
				Variables vars = new Variables();
				long mins = Math.round(((time-System.currentTimeMillis())*0.001)/60);
				vars.setVariable("%m",String.valueOf(mins));
				String display = vars.parseString("@+W["+colour+message+"@+W]@*");
				if (!mobile.checkFlag("NoNotify"))
				{
					display=display+"\u0007";
				}
				mobile.displayText(visibility,display);
			}
		}
	}
	
	public boolean cycle(TaweServer server)
	{
		long currenttime=System.currentTimeMillis();
		if (currenttime>=time)
		{
			special.run(server,new Variables());
			return true;
		}
		else if (warnings.size()>0)
		{
			if (currenttime>=((Long)warnings.get(0)).longValue())
			{
				while ((warnings.size()>0)&&(currenttime>=((Long)warnings.get(0)).longValue()))
				{
					warnings.remove(0);
				}
				Iterator loop = server.getPlayers();
				while (loop.hasNext())
				{
					notify((Mobile)loop.next());
				}
			}
		}
		return false;
	}
}
