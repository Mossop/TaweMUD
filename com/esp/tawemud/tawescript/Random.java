package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import java.util.StringTokenizer;
import java.util.LinkedList;

/**
 * Picks a random value.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Random extends SingleAction
{
	/**
	 * A comma seperated list of values to be chosen from.
	 *
	 * @required
	 */
	public String items;
	/**
	 * A variable to hold the chosen value.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		StringTokenizer tokens = new StringTokenizer(variables.parseString(items),",");
		LinkedList list = new LinkedList();
		while (tokens.hasMoreTokens())
		{
			list.add(tokens.nextToken());
		}
		variables.setVariable(dest,list.get((new java.util.Random()).nextInt(list.size())).toString());
		return false;
	}
}