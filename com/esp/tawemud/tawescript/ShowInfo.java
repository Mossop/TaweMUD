package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Displays an info page to an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ShowInfo extends TestAction
{
	/**
	 * The item.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The name of the info book.
	 *
	 * @required
	 */
	public String book;
	/**
	 * The page to display.
	 *
	 * @required
	 */
	public String page;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject rlitem = variables.getObject(item,server);
		if ((rlitem!=null)&&(rlitem.asMobile()!=null))
		{
			String result = server.getWorld().getInfoPage(variables.parseString(book),variables.parseString(page),rlitem.asMobile());
			if (result!=null)
			{
				rlitem.asItem().displayText(0,result);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
