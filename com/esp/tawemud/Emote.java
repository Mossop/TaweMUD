package com.esp.tawemud;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.esp.tawemud.tawescript.BaseCommand;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.items.Mobile;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.Iterator;

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
public class Emote extends BaseCommand
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
		super(30);
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
		setName(thisname);
	}

	/**
	 * Returns this emote serialized as an xml element.
	 * Currently only here to allow this class to be a BaseCommand.
	 *
	 * @param	builder	The xml document to build from.
	 * @returns	An xml element that contains all the information about this emote.
	 */
	public Element getElement(Document builder)
	{
		return null;
	}
	
	/**
	 * Sets up the emote from the xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging purposes.
	 */
	public void parseElement(Element node)
	{
		setName(node.getAttribute("name"));
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

	public String getHelp(Mobile mobile)
	{
		StringBuffer result = new StringBuffer();
		Variables vars = new Variables();
		vars.setVariable("$n",mobile.getName());
		vars.setVariable("$himselfcaller","<himself/herself>");
		vars.setVariable("$hecaller","<he/she>");
		vars.setVariable("$hiscaller","<his/her>");
		vars.setVariable("$himcaller","<him/her>");
		vars.setVariable("$herscaller","<his/hers>");
		vars.setVariable("$t","<target>");
		vars.setVariable("$himtarget","<him/her>");
		vars.setVariable("$histarget","<his/her>");
		vars.setVariable("$himselftarget","<himself/herself>");
		vars.setVariable("$hetarget","<he/she>");
		vars.setVariable("$herstarget","<his/hers>");
		vars.setVariable("$s","<text>");
		result.append("@+W"+getName()+"@*@/@/");
		if ((getAllFlag())||(getWorldFlag()))
		{
			result.append("@+WUsed on its own:@/@/");
			result.append("@+YTo others in the room@* : "+vars.parseString(getAll().toString())+"@/\n");
			result.append("@+YTo yourself@*           : "+vars.parseString(getMe().toString())+"@/\n");
			if (getSingleFlag())
			{
				result.append("@/");
			}
		}
		if (getSingleFlag())
		{
			result.append("@+WWith a mobile specified:@/@/");
			result.append("@+YTo yourself@*           : "+vars.parseString(getSender().toString())+"@/\n");
			result.append("@+YTo the target@*         : "+vars.parseString(getTarget().toString())+"@/\n");
			result.append("@+YTo others in the room@* : "+vars.parseString(getOthers().toString())+"@/\n");
		}
		return result.toString();
	}
	
	/**
	 * Calls the emote.
	 *
	 * @param	server	The current serveer process.
	 * @param	caller	The mobile running the emote.
	 * @param	found	The command the emote was invoked with.
	 * @param	args	The arguments to the emote.
	 * @returns	Whether the emote was succesfull or not.
	 */
	public boolean callCommand(TaweServer server, Mobile caller, String command, String args)
	{
		boolean worked=true;
		int vis = caller.getVisibility();
		Variables variables = new Variables();
		variables.setVariable("$n",caller.getName());
		if (caller.getGender().equals("female"))
		{
			variables.setVariable("$himselfcaller","herself");
			variables.setVariable("$hecaller","she");
			variables.setVariable("$hiscaller","her");
			variables.setVariable("$himcaller","her");
			variables.setVariable("$herscaller","hers");
		}
		else
		{
			variables.setVariable("$himselfcaller","himself");
			variables.setVariable("$hecaller","he");
			variables.setVariable("$hiscaller","his");
			variables.setVariable("$himcaller","him");
			variables.setVariable("$herscaller","his");
		}
		StringTokenizer tokens = new StringTokenizer(args);
		if ((getSingleFlag())&&(tokens.hasMoreTokens()))
		{
			String name = tokens.nextToken();
			args=args.substring(name.length());
			if (caller.getPronoun(name)!=null)
			{
				name=caller.getPronoun(name);
			}
			while (args.startsWith(" "))
			{
				args=args.substring(1);
			}
			variables.setVariable("$s",args);
			Mobile target = caller.getLocation().asRoom().findMobileByName(name);
			if ((target!=null)&&(!caller.canSee(target)))
			{
				target=null;
			}
			if ((target==null)&&(getFarFlag()))
			{
				boolean found=false;
				Iterator loop = server.getPlayers();
				while ((!found)&&(loop.hasNext()))
				{
					Mobile thisone = (Mobile)loop.next();
					if (thisone.hasName(name))
					{
						if (caller.canSee(thisone))
						{
							target=thisone;
							found=true;
						}
					}
				}
			}
			if (target!=null)
			{
				if ((!getViolentFlag())||(!target.checkFlag("peaceful")))
				{
					vis=Math.max(vis,target.getVisibility());
					variables.setVariable("$t",target.getName());
					if (target.getGender().equals("female"))
					{
						variables.setVariable("$himtarget","her");
						variables.setVariable("$histarget","her");
						variables.setVariable("$himselftarget","herself");
						variables.setVariable("$hetarget","she");
						variables.setVariable("$herstarget","hers");
					}
					else
					{
						variables.setVariable("$himtarget","him");
						variables.setVariable("$histarget","his");
						variables.setVariable("$himselftarget","himself");
						variables.setVariable("$hetarget","he");
						variables.setVariable("$herstarget","his");
					}
					String noshow=caller.getWorldIdentifier()+","+target.getWorldIdentifier();
					String others = variables.parseString(getOthers().toString());
					if (others.length()>0)
					{
						caller.getLocation().displayText(noshow,vis,others);
					}
					String sender = variables.parseString(getSender().toString());
					if (sender.length()>0)
					{
						caller.displayText(sender);
					}
					String targetmes = variables.parseString(getTarget().toString());
					if (targetmes.length()>0)
					{
						target.displayText(targetmes);
					}
				}
				else
				{
					caller.displayText("They are too peaceful for that.");
				}
			}
			else
			{
				caller.displayText("Who is that meant to be to?");
			}
		}
		else if (getAllFlag()||getWorldFlag())
		{
			variables.setVariable("$s",args);
			String all = variables.parseString(getAll().toString());
			if (all.length()>0)
			{
				if (getWorldFlag())
				{
					Iterator loop = server.getPlayers();
					while (loop.hasNext())
					{
						Mobile thisone = (Mobile)loop.next();
						if (!thisone.equals(caller))
						{
							thisone.displayText(vis,all);
						}
					}
				}
				else
				{
					caller.getLocation().displayText(caller.getWorldIdentifier(),vis,all);
				}
			}
			String me = variables.parseString(getMe().toString());
			if (me.length()>0)
			{
				caller.displayText(variables.parseString(me));
			}
		}
		else
		{
			if (getSingleFlag())
			{
				caller.displayText("Who is that meant to be to?");
			}
		}
		return worked;
	}
}
