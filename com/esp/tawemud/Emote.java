package com.esp.tawemud;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.PrintWriter;

/**
 * Holds the information about a specilized emote.
 *
 * Each emote could be run in one of two ways, targetted or general. In the general form
 * the user will see one message and everyone else in the room (or possibly the world) will
 * see another. In the targetted form, the user will see a message, the target a different message
 * and everyone else in the user's and target's rooms will see another message.
 * An emote can be considered violent, in which case it cannot be used on peaceful things, and when
 * used on a non-player it will start a fight.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Emote
{
	/**
	 * The message others in the room should see (general).
	 */
	private StringBuffer all;
	/**
	 * The message the user should see (general).
	 */
	private StringBuffer me;
	/**
	 * The message the user should see (targetted).
	 */
	private StringBuffer sender;
	/**
	 * The message the target should see (targetted).
	 */
	private StringBuffer target;
	/**
	 * The message others in the room should see (targetted).
	 */
	private StringBuffer others;
	/**
	 * The name of the emote.
	 */
	private String name;
	/**
	 * Whether the emote is considered violent or not.
	 */
	private boolean violent;
	/**
	 * If the emote can be used in general form.
	 */
	private boolean allflag;
	/**
	 * If the emote can be used in targetted form.
	 */
	private boolean single;
	/**
	 * If when targetted the target can be in a different room to the user.
	 */
	private boolean far;
	/**
	 * If when general everyone in the world should see the message.
	 */
	private boolean world;

	/**
	 * Initialises to default values.
	 */
	public Emote()
	{
		name="";
		all = new StringBuffer();
		me = new StringBuffer();
		sender = new StringBuffer();
		target = new StringBuffer();
		others = new StringBuffer();
		far=false;
		world=false;
		single=false;
		allflag=false;
		violent=false;
	}

	/**
	 * Creates a new emote with the given name.
	 *
	 * @param thisname  The name of the emote
	 */
	public Emote(String thisname)
	{
		this();
		name=thisname;
	}

	/**
	 * Sets up the emote from the xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging purposes.
	 */
	public void parseElement(Element node, PrintWriter out)
	{
		name=node.getAttribute("name");
		if (node.getAttribute("all").length()>0)
		{
			allflag=Boolean.valueOf(node.getAttribute("all")).booleanValue();
		}
		if (node.getAttribute("single").length()>0)
		{
			single=Boolean.valueOf(node.getAttribute("single")).booleanValue();
		}
		if (node.getAttribute("far").length()>0)
		{
			far=Boolean.valueOf(node.getAttribute("far")).booleanValue();
		}
		if (node.getAttribute("world").length()>0)
		{
			world=Boolean.valueOf(node.getAttribute("world")).booleanValue();
		}
		if (node.getAttribute("violent").length()>0)
		{
			violent=Boolean.valueOf(node.getAttribute("violent")).booleanValue();
		}
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				StringBuffer text;
				if ((thisone.getFirstChild()!=null)&&(thisone.getFirstChild().getNodeType()==Node.TEXT_NODE))
				{
					text = new StringBuffer(thisone.getFirstChild().getNodeValue());
				}
				else
				{
					text = new StringBuffer();
				}
				if (thisone.getTagName().equals("All"))
				{
					all=text;
				}
				else if (thisone.getTagName().equals("Me"))
				{
					me=text;
				}
				else if (thisone.getTagName().equals("Sender"))
				{
					sender=text;
				}
				else if (thisone.getTagName().equals("Target"))
				{
					target=text;
				}
				else if (thisone.getTagName().equals("Others"))
				{
					others=text;
				}
			}
		}
	}

	public boolean getFarFlag()
	{
		return far;
	}

	public boolean getSingleFlag()
	{
		return single;
	}

	public boolean getAllFlag()
	{
		return allflag;
	}

	public boolean getWorldFlag()
	{
		return world;
	}

	public boolean getViolentFlag()
	{
		return violent;
	}

	public StringBuffer getAll()
	{
		return all;
	}

	public StringBuffer getMe()
	{
		return me;
	}

	public StringBuffer getTarget()
	{
		return target;
	}

	public StringBuffer getSender()
	{
		return sender;
	}

	public StringBuffer getOthers()
	{
		return others;
	}

	public String getName()
	{
		return name;
	}
}
