package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Container;
import com.esp.tawemud.TaweServer;
import java.util.ListIterator;
import java.util.LinkedList;

/**
 * A standard for loop. It is possible for the contents never to get executed.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ForLoop extends GroupAction
{
	/**
	 * The start value of the loop.
	 *
	 * @default 0
	 */
	public String start;
	/**
	 * The end value of the loop.
	 *
	 * @default  0
	 */
	public String end;
	/**
	 * The step value for the loop. Can be positive or negative.
	 *
	 * @default  1
	 */
	public String step;
	/**
	 * The variable to hold the loop value during each run.
	 *
	 * @required
	 */
	public String dest;

	public boolean run(TaweServer server, Variables variables)
	{
		boolean result = false;
		int loopstart = 0;
		try
		{
			loopstart = Integer.parseInt(variables.parseString(start));
		}
		catch (Exception e)
		{
		}
		int loopend = 0;
		try
		{
			loopend = Integer.parseInt(variables.parseString(end));
		}
		catch (Exception e)
		{
		}
		int loopstep = 1;
		try
		{
			loopstep = Integer.parseInt(variables.parseString(step));
		}
		catch (Exception e)
		{
		}
		for (int loop=loopstart; (((loopstep<0)&&(loop>=loopend))||((loopstep>0)&&(loop<=loopend))); loop+=loopstep)
		{
			variables.setVariable(dest,Integer.toString(loop));
			result=super.run(server,variables)||result;
		}
		return result;
	}
}
