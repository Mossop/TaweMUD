package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Loads a plugin into the server.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class LoadPlugin extends TestAction
{
	/**
	 * The class name of the plugin.
	 *
	 * @required
	 */
	public String classname;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return server.loadPlugin(variables.parseString(classname));
	}
}
