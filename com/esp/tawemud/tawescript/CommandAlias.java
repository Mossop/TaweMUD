package com.esp.tawemud.tawescript;

import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class CommandAlias implements BaseCommand
{
	private String name;
	private String realcommand;
	
	public String getName()
	{
		return name;
	}
	
	public String getHelp(Mobile mobile)
	{
		return "This command is an alias for "+realcommand+". Try the help for that.";
	}

	public void parseElement(Element node)
	{
		name=node.getAttribute("alias");
		realcommand=node.getAttribute("command");
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("CommandAlias");
		node.setAttribute("alias",name);
		node.setAttribute("command",realcommand);
		return node;
	}

	public boolean callCommand(TaweServer server, Mobile caller, String found, String args)
	{
		server.parseCommand(caller,realcommand+" "+args);
		return true;
	}
}
