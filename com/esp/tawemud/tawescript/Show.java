package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Displays text to an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Show extends ContentAction
{
	/**
	 * The item to show the text to.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The visibility to display the text at.
	 *
	 * @default 0
	 */
	public String visibility;
	/**
	 * Items that should not see the text.
	 */
	public String noshow;

	public boolean doAction(TaweServer server, Variables variables)
	{
		int vis = 0;
		try
		{
			vis = Integer.parseInt(variables.parseString(visibility));
		}
		catch (Exception e)
		{
			vis=0;
		}
		String mess = variables.parseString(contents.toString());
		if (mess.length()>0)
		{
			CodeableObject dest = variables.getObject(item,server);
			if ((dest!=null)&&(dest.asItem()!=null))
			{
				dest.asItem().displayText(variables.parseString(noshow),vis,mess);
			}
		}
		return false;
	}
}

