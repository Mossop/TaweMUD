package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.multiline.MultiLineAttribute;
import java.lang.reflect.Method;

/**
 * Sets an attribute on an item using a multiline input.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetMultiLine extends TestAction
{
	/**
	 * The item to be changed.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The attribute to set.
	 *
	 * @required
	 */
	public String attribute;
	/**
	 * The player to supply the input.
	 *
	 * @required
	 */
	public String player;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		CodeableObject thisplayer = variables.getObject(player,server);
		if ((thisitem!=null)&&(thisplayer!=null)&&(thisplayer.asPlayer()!=null))
		{
			return thisplayer.asPlayer().setMultiLine(new MultiLineAttribute(thisitem,attribute));
		}
		else
		{
			return false;
		}
	}
}
