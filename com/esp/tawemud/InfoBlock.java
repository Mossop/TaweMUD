package com.esp.tawemud;

import java.util.StringTokenizer;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import com.esp.tawemud.items.Mobile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.PrintWriter;

/**
 * A block of information within an info page.
 *
 * Each block can have a visibility and a set of required flags that the reader must
 * have to be able to see the block.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class InfoBlock
{
	/**
	 * The visibility of this block.
	 */
	private int visibility;
	/**
	 * The flags on this block.
	 */
	private List flags;
	/**
	 * The text within this block.
	 */
	private StringBuffer content;

	/**
	 * Creates a new empty block.
	 */
	public InfoBlock()
	{
		visibility=0;
		flags = new LinkedList();
		content = new StringBuffer();
	}

	public StringBuffer getContent()
	{
		return content;
	}

	/**
	 * Serializes this infoblock as an xml element.
	 *
	 * @param	builder	The xml document to build from.
	 * @returns	This infoblock as an xml element.
	 */
	public Element getElement(Document builder)
	{
		Element node = builder.createElement("InfoBlock");
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
		node.appendChild(builder.createTextNode(content.toString()));
		return node;
	}
	
	/**
	 * Loads the block from an xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging.
	 */
	public void parseElement(Element node)
	{
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
			if (nodes.item(loop).getNodeType()==Node.TEXT_NODE)
			{
				content = new StringBuffer(nodes.item(loop).getNodeValue());
			}
		}
	}

	/**
	 * Returns this block formatted for the given mobile.
	 *
	 * Generally this just checks if the mobile can see the block or not.
	 *
	 * @param target  The mobile trying to view the block
	 * @return  The content of this block as the mobile can see it, or null if the mobile can see nothing
	 */
	public StringBuffer formatBlock(Mobile target)
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
				return content;
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
