package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.CodeableObject;
import java.util.LinkedList;
import java.util.Iterator;

/**
 * Finds an item in the vicinity of the mobile. Will search in the mobiles
 * inventory and the room, only checking things the mobile can see.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class FindLocalItem extends TestAction
{
	/**
	 * The mobile to search from.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The name of the item to look for.
	 *
	 * @required
	 */
	public String name;
	/**
	 * The variable to hold the items identifier.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject place = variables.getObject(mobile,server);
		if ((place!=null)&&(place.asMobile()!=null))
		{
			LinkedList items = new LinkedList();
			Iterator loop = place.asMobile().getLocation().getItemContentsIterator();
			while (loop.hasNext())
			{
				items.add(loop.next());
			}
			loop=place.asMobile().asMobile().getItemContentsIterator();
			while (loop.hasNext())
			{
				items.add(loop.next());
			}
			String rlname = variables.parseString(name);
			String val = "";
			while ((rlname.length()>0)&&(Character.isDigit(rlname.charAt(rlname.length()-1))))
			{
				val=rlname.substring(rlname.length()-1)+val;
				rlname=rlname.substring(0,rlname.length()-1);
			}
			if (rlname.length()>0)
			{
				int count;
				if (val.length()>0)
				{
					count=Integer.parseInt(val);
				}
				else
				{
					count=1;
				}
				loop = items.iterator();
				Item result = null;
				int current=0;
				Item thisitem;
				while ((loop.hasNext())&&(result==null))
				{
					thisitem=(Item)loop.next();
					if ((place.asMobile().canSee(thisitem))&&(thisitem.hasName(rlname)))
					{
						current++;
						if (count==current)
						{
							result=thisitem;
						}
					}
				}
				if (result!=null)
				{
					variables.setVariable(dest,result);
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
		else
		{
			return false;
		}
	}
}
