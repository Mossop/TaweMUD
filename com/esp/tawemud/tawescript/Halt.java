package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Halts the mud.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Halt extends SingleAction
{
	public boolean doAction(TaweServer server, Variables variables)
	{
		server.sendWizMessage("Process is being halted","",0,2);
		server.halt();
		return false;
	}
}
