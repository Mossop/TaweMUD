package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Mobile;
import java.util.StringTokenizer;

public class Command extends GroupAction implements BaseCommand
{
	private String name;
	private String args;
	private String version;
	private CodeableObject owner;

	public Command(CodeableObject owner)
	{
		super();
		this.owner=owner;
		args="10";
		name="";
		version="0.00";
	}

	public CodeableObject getOwner()
	{
		return owner;
	}

	public int getArgCount()
	{
		return Integer.parseInt(args);
	}

	public String getName()
	{
		return name;
	}

	public void parseElement(Element node)
	{
		super.parseElement(node);
		name=node.getAttribute("name");
		args=node.getAttribute("args");
		version=node.getAttribute("version");
	}

	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		node.setAttribute("name",name);
		node.setAttribute("args",args);
		node.setAttribute("version",version);
		return node;
	}

	public boolean callCommand(TaweServer server, Mobile caller, String found, String args)
	{
		int loop=0;
		StringTokenizer tokens;
		Variables variables = new Variables();
		variables.setVariable("$0",caller);
		variables.setVariable("$1",found);
		tokens = new StringTokenizer(args);
		while ((tokens.hasMoreTokens())&&(loop<getArgCount()))
		{
			String thisarg=tokens.nextToken();
			if (caller.getPronoun(thisarg)!=null)
			{
				thisarg=caller.getPronoun(thisarg);
			}
			variables.setVariable("$"+(loop+2),thisarg);
			loop++;
		}
		if (tokens.hasMoreTokens())
		{
			String lastarg = tokens.nextToken("");
			while (lastarg.startsWith(" "))
			{
				lastarg=lastarg.substring(1);
			}
			variables.setVariable("$"+(loop+2),lastarg);
		}
		else
		{
			//variables.setVariable("$"+(loop+2),"");
		}
		return run(server,variables);
	}
}
