package com.esp.tawemud;

import java.util.List;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;
import com.esp.tawemud.items.Mobile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.PrintWriter;

/**
 * A page within an info book.
 *
 * Each page is made up of a number of blocks, each of which may or may not be visible to a mobile.
 * Likewise the entire page may or may not be visible to a mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class InfoPage
{
	/**
	 * The blocks in this page.
	 */
	private List blocks;
	/**
	 * The visibility of the page.
	 */
	private int visibility;
	/**
	 * The flags on this page.
	 */
	private List flags;
	/**
	 * The name of this page.
	 */
	private String name;

	/**
	 * Creates a new empty page.
	 */
	public InfoPage()
	{
		name="";
		visibility=0;
		blocks = new LinkedList();
		flags = new LinkedList();
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Serializes this infopage as an xml element.
	 *
	 * @param	builder	The xml document to build from.
	 * @returns	This infopage as an xml element.
	 */
	public Element getElement(Document builder)
	{
		Element node = builder.createElement("InfoPage");
		node.setAttribute("name",name);
		node.setAttribute("vis",String.valueOf(visibility));
		StringBuffer list = new StringBuffer();
		Iterator flagloop = flags.iterator();
		while (flagloop.hasNext())
		{
			list.append(flagloop.next());
			if (flagloop.hasNext())
			{
				list.append(",");
			}
		}
		node.setAttribute("flags",list.toString());
		for (int loop=0; loop<blocks.size(); loop++)
		{
			node.appendChild(((InfoBlock)blocks.get(loop)).getElement(builder));
		}
		return node;
	}
	
	/**
	 * Loads the page from an xml element.
	 *
	 * @param node  The xml element
	 */
	public void parseElement(Element node)
	{
		name=node.getAttribute("name").toLowerCase();
		StringTokenizer tokens = new StringTokenizer(node.getAttribute("flags"),",");
		while (tokens.hasMoreTokens())
		{
			flags.add(tokens.nextToken());
		}
		if (node.getAttribute("vis").length()>0)
		{
			visibility=Integer.parseInt(node.getAttribute("vis"));
		}
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				if (thisone.getTagName().equals("InfoBlock"))
				{
					InfoBlock block = new InfoBlock();
					block.parseElement(thisone);
					blocks.add(block);
				}
			}
		}
	}

	/**
	 * Provides the text of the page as a mobile would see it.
	 *
	 * If the mobile can see the page then this returns all of the blocks of the page
	 * formatted for the mobile.
	 *
	 * @param target  The mobile looking at the page
	 * @return  null if the mobile cannot see the page, or the text that the mobile can see
	 */
	public String formatPage(Mobile target)
	{
		if (target.getLevel()>=visibility)
		{
			boolean good = true;
			for (int loop=0; loop<flags.size(); loop++)
			{
				if (!target.checkFlag((String)flags.get(loop)))
				{
					good=false;
				}
			}
			if (good)
			{
				StringBuffer buffer = new StringBuffer();
				for (int loop=0; loop<blocks.size(); loop++)
				{
					StringBuffer thisone = ((InfoBlock)blocks.get(loop)).formatBlock(target);
					if (thisone!=null)
					{
						buffer.append(thisone);
					}
				}
				return buffer.toString();
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
