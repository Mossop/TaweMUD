package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Message;

/**
 * Stores text in a new variable.
 *
 * @version 1.0
 * @author  Dave Townsend
 */
public class Concat extends SingleAction
{
	/**
	 * The text to be stored.
	 */
	public String string;
	/**
	 * The new variable.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		variables.setVariable(dest,variables.parseString(string));
		return false;
	}
}
