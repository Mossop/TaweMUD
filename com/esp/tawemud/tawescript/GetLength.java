package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;

/**
 * Works out the length of some text (including colour information)
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetLength extends ContentAction
{
	/**
	 * The variable to hold the length in.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String mess = variables.parseString(contents.toString());
		variables.setVariable(dest,Integer.toString(mess.length()));
		return false;
	}
}