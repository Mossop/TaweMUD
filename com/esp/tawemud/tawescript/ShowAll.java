package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Item;
import java.util.StringTokenizer;
import java.util.LinkedList;

/**
 * Display text to everyone connected to the mud.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ShowAll extends ContentAction
{
	/**
	 * The visibility to display the text at.
	 *
	 * @default  0
	 */
	public String visibility;
	/**
	 * Users who should not see the text.
	 */
	public String noshow;
	/**
	 * Flags users must have to be able to see the text.
	 */
	public String musthave;
	/**
	 * Flags users cant have to be able to see the text.
	 */
	public String canthave;

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
			LinkedList needflags = new LinkedList();
			StringTokenizer tokens = new StringTokenizer(variables.parseString(musthave),",");
			while (tokens.hasMoreTokens())
			{
				needflags.add(tokens.nextToken());
			}
			LinkedList badflags = new LinkedList();
			tokens = new StringTokenizer(variables.parseString(canthave),",");
			while (tokens.hasMoreTokens())
			{
				badflags.add(tokens.nextToken());
			}
			server.sendAllMessage(vis,mess,variables.parseString(noshow),needflags,badflags);
		}
		return false;
	}
}

