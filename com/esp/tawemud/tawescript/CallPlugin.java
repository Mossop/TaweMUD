package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Calls a plugin function.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CallPlugin extends TestAction
{
	/**
	 * The player calling the function.
	 *
	 * @required
	 */
	public String player;
	/**
	 * The name of the plugin.
	 *
	 * @required
	 */
	public String plugin;
	/**
	 * What function to call on the plugin.
	 */
	public String function;
	/**
	 * Options to pass to the function.
	 */
	public String options;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject mobile = variables.getObject(player,server);
		if ((mobile!=null)&&(mobile.asMobile()!=null))
		{
			return server.callPluginFunction(mobile.asMobile(),variables.parseString(plugin),variables.parseString(function),variables.parseString(options));
		}
		else
		{
			return false;
		}
	}
}
