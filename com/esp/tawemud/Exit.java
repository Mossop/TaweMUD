package com.esp.tawemud;

import java.util.Vector;
import com.esp.tawemud.tawescript.Special;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.items.Mobile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.util.Iterator;

/**
 * Holds information about an exit from one room to another.
 *
 * Specifically this allows the exit to have more information than just a destination, specials for example.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Exit
{
	/**
	 * The destination room.
	 */
	private String destination;
	/**
	 * Specials on the exit.
	 */
	private Vector specials;
	/**
	 * The room that this exit exists in.
	 */
	private Room room;

	/**
	 * Create a new exit from the given room.
	 *
	 * @param thisroom  The room the exit is in
	 */
	public Exit(Room thisroom)
	{
		specials = new Vector(10);
		room=thisroom;
	}

	/**
	 * Creates a new exit to the given room.
	 *
	 * @param thisroom  The room the exit is in
	 * @param dest  The destination room
	 */
	public Exit(Room thisroom, String dest)
	{
		this(thisroom);
		destination=dest;
	}

	/**
	 * Loads information about the exit from an xml element.
	 *
	 * @param node  The xml element
	 */
	public void parseElement(Element node)
	{
		destination=node.getAttribute("destination");
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				if (thisone.getTagName().equals("Special"))
				{
					Special newspecial = new Special(room);
					newspecial.parseElement(thisone);
					addSpecial(newspecial);
				}
			}
		}
	}

	/**
	 * Returns this exit serialized as an xml element.
	 *
	 * @param builder An xml document used to create the element
	 * @return  The object as an xml element
	 */
	public Element getElement(Document builder)
	{
		Element node = builder.createElement("Exit");
		node.setAttribute("destination",destination);
		Iterator loop = specials.iterator();
		while (loop.hasNext())
		{
			node.appendChild(((Special)loop.next()).getElement(builder));
		}
		return node;
	}

	public void setDestination(String room)
	{
		destination=room;
	}

	public String getDestination()
	{
		return destination;
	}

	/**
	 * Add a special to the exit.
	 *
	 * @param newspecial  The special to be added
	 */
	public void addSpecial(Special newspecial)
	{
		specials.addElement(newspecial);
	}

	/**
	 * Runs specials as a mobile walks out the exit.
	 *
	 * @param mobile  The mobile that is following the exit
	 */
	public boolean runSpecials(Mobile mobile)
	{
		int loop = 0;
		boolean done=false;
		Variables variables = new Variables();
		variables.setVariable("$0",mobile.getWorldIdentifier());
		while ((!done)&&(loop<specials.size()))
		{
			done=((Special)specials.elementAt(loop)).run(room.getZone().getWorld().getServer(),variables);
			loop++;
		}
		return done;
	}
}
