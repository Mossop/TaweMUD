package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Player;
import com.esp.tawemud.TaweServer;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.LinkedList;
import java.lang.reflect.Method;

/**
 * Iterates over all players in a specified order.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IterateAllPlayers extends GroupAction
{
	/**
	 * The attribute that will determine the order, for example experience or deaths.
	 *
	 * @required
	 */
	public String order;
	/**
	 * The variable to hold the current player.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		try
		{
			Method[] methods = Class.forName(TaweServer.PACKAGE+".items.Player").getMethods();
			Method ordermethod = null;
			String ordername="get"+variables.parseString(order);
			for (int count=0; count<methods.length; count++)
			{
				if (methods[count].getName().equalsIgnoreCase(ordername))
				{
					if (methods[count].getParameterTypes().length==0)
					{
						if ((methods[count].getReturnType().getName().equals("int"))||(methods[count].getReturnType().getName().equals("long")))
						{
							ordermethod=methods[count];
						}
					}
				}
			}
			if (ordermethod!=null)
			{
				Iterator loop = server.getWorld().findZone("players").getMobileIterator();
				LinkedList players = new LinkedList();
				ListIterator cursor;
				int thisvalue;
				int cursorvalue;
				Object current;
				boolean added;
				while (loop.hasNext())
				{
					current=loop.next();
					thisvalue=Integer.parseInt(ordermethod.invoke(current,new Object[0]).toString());
					cursor=players.listIterator();
					added=false;
					while ((cursor.hasNext())&&(!added))
					{
						cursorvalue=Integer.parseInt(ordermethod.invoke(cursor.next(),new Object[0]).toString());
						if (thisvalue>cursorvalue)
						{
							cursor.previous();
							cursor.add(current);
							added=true;
						}
					}
					if (!added)
					{
						cursor.add(current);
					}
				}
				loop=players.iterator();
				while (loop.hasNext())
				{
					variables.setVariable(dest,(Player)loop.next());
					result=super.run(server,variables)||result;
				}
				return result;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
}
