package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Converts a short direction (n,s,e,w etc) to its longer form (North, South, East, West etc)
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetLongDirection extends SingleAction
{
	/**
	 * The short direction to be converted.
	 *
	 * @required
	 */
	public String direction;
	/**
	 * A variable to hold the result.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String realdir = variables.parseString(direction).toLowerCase();
		String longdir = "";
		if (realdir.equals("n"))
		{
			longdir="North";
		}
		else if (realdir.equals("e"))
		{
			longdir="East";
		}
		else if (realdir.equals("s"))
		{
			longdir="South";
		}
		else if (realdir.equals("w"))
		{
			longdir="West";
		}
		else if (realdir.equals("ne"))
		{
			longdir="NorthEast";
		}
		else if (realdir.equals("se"))
		{
			longdir="SouthEast";
		}
		else if (realdir.equals("sw"))
		{
			longdir="SouthWest";
		}
		else if (realdir.equals("nw"))
		{
			longdir="NorthWest";
		}
		else if (realdir.equals("u"))
		{
			longdir="Up";
		}
		else if (realdir.equals("d"))
		{
			longdir="Down";
		}
		variables.setVariable(dest,longdir);
		return false;
	}
}
