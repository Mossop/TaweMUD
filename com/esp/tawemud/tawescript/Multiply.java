package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Multiplies two values together.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Multiply extends SingleAction
{
	/**
	 * The first value.
	 *
	 * @required
	 */
	public String value1;
	/**
	 * The second value.
	 *
	 * @required
	 */
	public String value2;
	/**
	 * A variable to hold the result.
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
			variables.setVariable(dest,Integer.toString(val1*val2));
		}
		catch (Exception e)
		{
			variables.setVariable(dest,"0");
		}
		return false;
	}
}
