package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Loads a zone.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class LoadZone extends TestAction
{
	/**
	 * The zone name.
	 *
	 * @required
	 */
	public String zone;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject test = variables.getObject(zone,server);
		if ((test==null)||(!test.getIdentifier().equals("players")))
		{
			StringWriter buffer = new StringWriter();
			PrintWriter out = new PrintWriter(buffer);
			String real = variables.parseString(zone);
			boolean result = server.getWorld().loadZone(real,out);
			out.flush();
			server.setMudLog(buffer.toString());
			if (result)
			{
				server.getWorld().findZone(real).reset();
			}
			return result;
		}
		else
		{
			return false;
		}
	}
}
