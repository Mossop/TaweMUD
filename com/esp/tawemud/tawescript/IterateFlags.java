package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterates over an items flags.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateFlags extends GroupAction
{
	/**
	 * The item to check.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The type of flag to iterate over. See Check for more information.
	 *
	 * @default normal
	 */
	public String type;
	/**
	 * A variable to hold the current flag.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		CodeableObject rlitem = variables.getObject(item,server);
		String rltype = variables.parseString(type).toLowerCase();
		if (rlitem!=null)
		{
			if (rltype.length()==0)
			{
				rltype="normal";
			}
			LinkedList newlist = new LinkedList();
			Iterator contents = rlitem.getFlagIterator(rltype);
			if (contents==null)
			{
				contents=rlitem.getFlagIterator();
			}
			while (contents.hasNext())
			{
				newlist.add(contents.next());
			}
			contents=newlist.iterator();
			while (contents.hasNext())
			{
				variables.setVariable(dest,contents.next().toString());
				result=super.run(server,variables)||result;
			}
		}
		return result;
	}
}
