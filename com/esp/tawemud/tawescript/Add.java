package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Adds together two integers.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Add extends SingleAction
{
	/**
	 * The first value.
	 *
	 * @default 0
	 */
	public String value1;
	/**
	 * The second value.
	 *
	 * @default 0
	 */
	public String value2;
	/**
	 * The variable to output to.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		try
		{
			int val1 = Integer.parseInt(variables.parseString(value1));
			int val2 = Integer.parseInt(variables.parseString(value2));
			variables.setVariable(dest,Integer.toString(val1+val2));
		}
		catch (Exception e)
		{
			variables.setVariable(dest,"0");
		}
		return false;
	}
}
