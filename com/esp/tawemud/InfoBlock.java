package com.esp.tawemud;

import java.util.StringTokenizer;
import java.util.Vector;
import com.esp.tawemud.items.Mobile;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
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
	private Vector flags;
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
		flags = new Vector(10);
		content = new StringBuffer();
	}

	public Vector getFlags()
	{
		return flags;
	}

	public StringBuffer getContent()
	{
		return content;
	}

	/**
	 * Loads the block from an xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging.
	 */
	public void parseElement(Element node, PrintWriter out)
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
				if (!target.checkFlag((String)flags.elementAt(loop)))
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
