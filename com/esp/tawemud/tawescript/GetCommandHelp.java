package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.util.Iterator;

/**
 * Returns the help for a command.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetCommandHelp extends TestAction
{
	/**
	 * The command to find out about.
	 *
	 * @required
	 */
	public String command;
	/**
	 * The mobile who wants to know about the command.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The variable to hold the result.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject rlmobile = variables.getObject(mobile,server);
		if ((rlmobile!=null)&&(rlmobile.asMobile()!=null))
		{
			Iterator loop = server.getWorld().getCommandsIterator();
			boolean done=false;
			String rlcommand = variables.parseString(command).toLowerCase();
			while ((loop.hasNext())&&(!done))
			{
				BaseCommand thisone = (BaseCommand)loop.next();
				if (thisone.getName().toLowerCase().startsWith(rlcommand))
				{
					String text = thisone.getHelp(rlmobile.asMobile());
					if (text!=null)
					{
						done=true;
						variables.setVariable(dest,text);
					}
				}
			}
			return done;
		}
		else
		{
			return false;
		}
	}
}