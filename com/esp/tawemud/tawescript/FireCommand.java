package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Makes a mobile run a command.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FireCommand extends ContentAction
{
	/**
	 * The mobile to run the command.
	 *
	 * @required
	 */
	public String item;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject object = variables.getObject(item,server);
		if ((object!=null)&&(object.asMobile()!=null))
		{
			String mess = variables.parseString(contents.toString());
			server.parseCommand(object.asMobile(),mess);
		}
		return false;
	}
}
