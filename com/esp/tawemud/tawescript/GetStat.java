package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Return some status information from the server.
 *
 * @author  Dave Townsend
 * @version 1.1
 */
public class GetStat extends SingleAction
{
	/**
	 * What status to get. Possible values are timer, log, time, cycletime, freememory,
	 * totalmemory and threads.
	 *
	 * @required
	 */
	public String stat;
	/**
	 * A variable to hold the result.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String realstat = variables.parseString(stat).toLowerCase();
		if (realstat.equals("timer"))
		{
			variables.setVariable(dest,Long.toString(System.currentTimeMillis()%31536000));
		}
		else if (realstat.equals("log"))
		{
			variables.setVariable(dest,server.getMudLog().toString());
		}
		else if (realstat.equals("time"))
		{
			SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
			variables.setVariable(dest,formatter.format(new Date()));
		}
		else if (realstat.equals("cycletime"))
		{
			variables.setVariable(dest,Long.toString(server.getCycleTime()));
		}
		else if (realstat.equals("freememory"))
		{
			variables.setVariable(dest,Long.toString(Runtime.getRuntime().freeMemory()/1024));
		}
		else if (realstat.equals("totalmemory"))
		{
			variables.setVariable(dest,Long.toString(Runtime.getRuntime().totalMemory()/1024));
		}
		else if (realstat.equals("version"))
		{
			variables.setVariable(dest,TaweServer.getVersion());
		}
		else if (realstat.equals("build"))
		{
			variables.setVariable(dest,TaweServer.getBuild());
		}
		else if (realstat.equals("threads"))
		{
			ThreadGroup grp = Thread.currentThread().getThreadGroup();
			while (grp.getParent()!=null)
			{
				grp=grp.getParent();
			}
			variables.setVariable(dest,Integer.toString(grp.activeCount()));
		}
		return false;
	}
}
