package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Stops mobiles tracing command and special activity.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class StopTrace extends SingleAction
{
	public boolean doAction(TaweServer server, Variables variables)
	{
		server.stopTrace();
		return false;
	}
}

