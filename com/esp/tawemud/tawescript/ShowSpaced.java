package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.PlayerIO;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Displays text to an item, spaced out in aligned columns.
 * Include the text as the content of the tag, comma separated.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ShowSpaced extends ContentAction
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
		StringTokenizer tokens = new StringTokenizer(variables.parseString(contents.toString()),",");
		LinkedList blocks = new LinkedList();
		int maxsize=0;
		while (tokens.hasMoreTokens())
		{
			String next = tokens.nextToken();
			maxsize=Math.max(maxsize,PlayerIO.stripColour(next).length());
			blocks.add(next);
		}
		maxsize++;
		CodeableObject dest = variables.getObject(item,server);
		if ((dest!=null)&&(dest.asMobile()!=null))
		{
			int cols=dest.asMobile().getTerminalWidth()/maxsize;
			maxsize=dest.asMobile().getTerminalWidth()/cols;
			int currentcol=1;
			Iterator loop = blocks.iterator();
			StringBuffer line = new StringBuffer();
			while (loop.hasNext())
			{
				String next = (String)loop.next();
				line.append(next);
				if ((loop.hasNext())&&(currentcol<cols))
				{
					for (int spaces=(maxsize-PlayerIO.stripColour(next).length()); spaces>0; spaces--)
					{
						line.append(" ");
					}
					currentcol++;
				}
				else
				{
					currentcol=1;
					dest.asMobile().displayText(vis,line.toString());
					line.delete(0,line.length());
				}
			}
		}
		return false;
	}
}
