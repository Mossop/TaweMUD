package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import java.util.StringTokenizer;

/**
 * Iterates over a set of items.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Iterate extends GroupAction
{
	/**
	 * The items to iterate over, comma delimitted. Trailing commas do not matter.
	 *
	 * @required
	 */
	public String items;
	/**
	 * The variable to hold the current item.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		StringTokenizer tokens = new StringTokenizer(variables.parseString(items),",");
		while (tokens.hasMoreTokens())
		{
			variables.setVariable(dest,tokens.nextToken());
			result=super.run(server,variables)||result;
		}
		return result;
	}
}