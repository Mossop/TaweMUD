package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Starts a mobile tracing commands and specials.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class StartTrace extends SingleAction
{
	/**
	 * The mobile to see the trace output.
	 *
	 * @required
	 */
	public String mobile;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject dest = variables.getObject(mobile,server);
		if ((dest!=null)&&(dest.asMobile()!=null))
		{
			server.startTrace(dest.asMobile());
		}
		return false;
	}
}

