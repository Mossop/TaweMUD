package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.InfoPage;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Document;

public class CommandAlias implements BaseCommand
{
	private String name;
	private String realcommand;
	private InfoPage help;
	
	public String getName()
	{
		return name;
	}
	
	public String getHelp(Mobile mobile)
	{
		if (help!=null)
		{
			return help.formatPage(mobile);
		}
		else
		{
			return "This command is an alias for "+realcommand+". Try the help for that.";
		}
	}

	public void parseElement(Element node)
	{
		name=node.getAttribute("alias");
		realcommand=node.getAttribute("command");
		Node child = node.getFirstChild();
		while (child!=null)
		{
			if ((child.getNodeType()==Node.ELEMENT_NODE)&&(child.getNodeName().equals("InfoPage")))
			{
				help = new InfoPage();
				help.parseElement((Element)child);
			}
			child=child.getNextSibling();
		}
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("CommandAlias");
		node.setAttribute("alias",name);
		node.setAttribute("command",realcommand);
		if (help!=null)
		{
			node.appendChild(help.getElement(builder));
		}
		return node;
	}

	public boolean callCommand(TaweServer server, Mobile caller, String found, String args)
	{
		server.parseCommand(caller,realcommand+" "+args);
		return true;
	}
}
