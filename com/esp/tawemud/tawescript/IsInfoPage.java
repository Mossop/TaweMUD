package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;

/**
 * Checks if an info page exists.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IsInfoPage extends TestAction
{
	/**
	 * The book to check in.
	 *
	 * @required
	 */
	public String book;
	/**
	 * The page to look for.
	 *
	 * @required
	 */
	public String page;

	public boolean doTest(TaweServer server, Variables variables)
	{
		return server.getWorld().isInfoPage(variables.parseString(book),variables.parseString(page));
	}
}
