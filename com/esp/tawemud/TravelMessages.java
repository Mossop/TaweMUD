package com.esp.tawemud;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;

public class TravelMessages
{
	private HashMap messages;

	public TravelMessages()
	{
		messages = new HashMap();
		messages.put("transin","%n appears in the room.");
		messages.put("transout","%n vanishes.");
		messages.put("homein","%n has arrived home.");
		messages.put("homeout","%n has gone home.");
		messages.put("summin","%n summons someone.");
		messages.put("summout","%n has summoned someone.");
		messages.put("summtarg","You have been summoned by %n.");
		messages.put("walkin","%n walks in.");
		messages.put("walkout","%n walks out.");
		messages.put("flee","%n runs out to escape.");
		messages.put("fleein","%n runs in.");
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("TravelMessages");
		Iterator loop = messages.keySet().iterator();
		while (loop.hasNext())
		{
			String type=loop.next().toString();
			Element subnode = builder.createElement("Message");
			subnode.setAttribute("type",type);
			subnode.appendChild(builder.createTextNode(getMessage(type)));
			node.appendChild(subnode);
		}
		return node;
	}

	public void parseElement(Element node, PrintWriter out)
	{
		NodeList childs = node.getChildNodes();
		for (int loop=0; loop<childs.getLength(); loop++)
		{
			if (childs.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone=(Element)childs.item(loop);
				String text;
				if ((thisone.getFirstChild()!=null)&&(thisone.getFirstChild().getNodeType()==Node.TEXT_NODE))
				{
					text=thisone.getFirstChild().getNodeValue();
				}
				else
				{
					text="";
				}
				if (thisone.getTagName().equals("Message"))
				{
					String type=thisone.getAttribute("type");
					if (type.length()>0)
					{
						setMessage(type,text);
					}
				}
			}
		}
	}

	public String getMessage(String type)
	{
		return (String)messages.get(type.toLowerCase());
	}

	public void setMessage(String type, String message)
	{
		messages.put(type.toLowerCase(),message);
	}
}
