package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Player;
import com.esp.tawemud.TaweServer;
import java.util.Vector;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Iterates over all the logged in players in level order.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class IteratePlayers extends GroupAction
{
	/**
	 * A variable to hold the current player.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		Iterator loop = server.getPlayers();
		LinkedList powers = new LinkedList();
		LinkedList dpowers = new LinkedList();
		LinkedList wiz = new LinkedList();
		LinkedList mortals = new LinkedList();
		LinkedList current;
		Player thisone;
		while (loop.hasNext())
		{
			thisone = (Player)loop.next();
			if (thisone.isPower())
			{
				current=powers;
			}
			else if (thisone.isDPower())
			{
				current=dpowers;
			}
			else if (thisone.isWiz())
			{
				current=wiz;
			}
			else
			{
				current=mortals;
			}
			int count=0;
			while ((count<current.size())&&(((Player)current.get(count)).getLevel()>thisone.getLevel()))
			{
				count++;
			}
			current.add(count,thisone);
		}
		LinkedList sorted = new LinkedList();
		sorted.addAll(powers);
		sorted.addAll(dpowers);
		sorted.addAll(wiz);
		sorted.addAll(mortals);
		loop=sorted.iterator();
		while (loop.hasNext())
		{
			variables.setVariable(dest,(Player)loop.next());
			result=super.run(server,variables)||result;
		}
		return result;
	}
}
