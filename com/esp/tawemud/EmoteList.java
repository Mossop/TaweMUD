package com.esp.tawemud;

import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import org.xml.sax.SAXException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import com.esp.tawemud.xml.XmlLoader;

/**
 * Maintains a list of emotes in an order.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class EmoteList
{
	/**
	 * The list of emotes.
	 */
	private List emotes;
	/**
	 * The URL of the world directory.
	 */
	private String worldurl;

	/**
	 * Sets up the emote list.
	 *
	 * @param worldurl  The URL of the world directory
	 */
	public EmoteList(String worldurl)
	{
		emotes=new LinkedList();
		this.worldurl=worldurl;
	}

	/**
	 * Returns an iterator to the emotes.
	 *
	 * @return  An iterator to the emotes
	 */
	public ListIterator getIterator()
	{
		return emotes.listIterator();
	}

	/**
	 * Loads emotes from the given xml file.
	 *
	 * @param file  The file to load from.
	 * @param out A PrintWriter for logging.
	 */
	public boolean loadFrom(String file, PrintWriter out)
	{
		out.println("Loading emotes from "+file);
		try
		{
			Document doc = XmlLoader.parse(worldurl+file,out);
			if (doc.getDocumentElement()!=null)
			{
				parseElement(doc.getDocumentElement(),out);
				return true;
			}
			else
			{
				out.println(file+" is an empty emotes file");
				return false;
			}
		}
		catch (SAXException ex)
		{
			return false;
		}
		catch (Exception e)
		{
			out.println("There was an error loading the file - "+e.getMessage());
			e.printStackTrace(out);
			return false;
		}
	}

	/**
	 * Loads emotes from an EmoteList xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging
	 */
	public void parseElement(Element node, PrintWriter out)
	{
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				if (thisone.getTagName().equals("Emote"))
				{
					Emote newemote = new Emote();
					newemote.parseElement(thisone,out);
					addEmote(newemote);
				}
				else if (thisone.getTagName().equals("IncludeEmotes"))
				{
					loadFrom(thisone.getAttribute("file"),out);
				}
			}
		}
	}

	/**
	 * Finds an emote with a particular name.
	 *
	 * @param command The emote to find
	 * @return  The found emote, or null if none was found
	 */
	public Emote findEmote(String command)
	{
		ListIterator loop = emotes.listIterator();
		boolean found=false;
		Emote result=null;
		while ((!found)&&(loop.hasNext()))
		{
			result=(Emote)loop.next();
			if (result.getName().startsWith(command))
			{
				found=true;
			}
			else
			{
				result=null;
			}
		}
		return result;
	}

	/**
	 * Adds an emote to the list.
	 *
	 * @param emote The emote to be added
	 */
	public void addEmote(Emote emote)
	{
		int pos=0;
		ListIterator loop = emotes.listIterator();
		Emote next;
		if (!loop.hasNext())
		{
			next=null;
		}
		else
		{
			next=(Emote)loop.next();
			loop.previous();
		}
		while ((next!=null)&&(next.getName().compareTo(emote.getName())<0))
		{
			loop.next();
			if (loop.hasNext())
			{
				next=(Emote)loop.next();
				loop.previous();
			}
			else
			{
				next=null;
			}
		}
		loop.add(emote);
	}
}
