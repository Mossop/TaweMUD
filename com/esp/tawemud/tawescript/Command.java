package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.InfoPage;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Mobile;
import java.util.StringTokenizer;

public class Command extends GroupAction implements BaseCommand
{
	private String name;
	private String args;
	private String version;
	private InfoPage help;
	
	public Command(CodeableObject owner)
	{
		super();
		setOwner(owner);
		args="10";
		name="";
		version="0.00";
	}

	public int getArgCount()
	{
		return Integer.parseInt(args);
	}

	public int getPriority()
	{
		return 10;
	}
	
	public int compareTo(Object o)
	{
		if (o instanceof BaseCommand)
		{
			BaseCommand target = (BaseCommand)o;
			if (getPriority()==target.getPriority())
			{
				return getName().compareToIgnoreCase(target.getName());
			}
			else
			{
				return getPriority()-target.getPriority();
			}
		}
		else
		{
			throw new ClassCastException("Object given is not a BaseCommand");
		}
	}
	
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
			return null;
		}
	}
	
	public boolean parseSubElement(Element node, String text)
	{
		if (node.getTagName().equals("InfoPage"))
		{
			help = new InfoPage();
			help.parseElement(node);
			return true;
		}
		else
		{
			return super.parseSubElement(node,text);
		}
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
		if (help!=null)
		{
			node.insertBefore(help.getElement(builder),node.getFirstChild());
		}
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
