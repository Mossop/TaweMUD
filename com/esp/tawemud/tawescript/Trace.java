package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Traces a section of code.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Trace extends GroupAction
{
	/**
	 * The mobile to see the trace output.
	 *
	 * @required
	 */
	public String mobile;

	public boolean run(TaweServer server, Variables variables)
	{
		CodeableObject dest = variables.getObject(mobile,server);
		if ((dest!=null)&&(dest.asMobile()!=null))
		{
			server.startTrace(dest.asMobile());
		}
		boolean result = super.run(server,variables);
		if ((dest!=null)&&(dest.asMobile()!=null))
		{
			server.stopTrace();
		}
		return result;
	}
}