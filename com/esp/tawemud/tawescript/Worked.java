package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Flags that a command was succesfull, or a subroutine passed.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Worked extends SingleAction
{
	public boolean doAction(TaweServer server, Variables variables)
	{
		return true;
	}
}
