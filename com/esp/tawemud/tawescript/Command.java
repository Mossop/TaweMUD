package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.InfoPage;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Mobile;
import java.util.StringTokenizer;

public class Command extends BaseCommand
{
	private String args;
	private String version;
	private InfoPage help;
	private GroupAction script;
	
	public Command(CodeableObject owner)
	{
		super(10);
		args="10";
		version="0.00";
		script = new GroupAction();
		script.setName("Command");
		script.setOwner(owner);
	}

	public int getArgCount()
	{
		return Integer.parseInt(args);
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
			return script.parseSubElement(node,text);
		}
	}

	public void parseElement(Element node)
	{
		setName(node.getAttribute("name"));
		args=node.getAttribute("args");
		version=node.getAttribute("version");
		Node child = node.getFirstChild();
		String text;
		Element thisone;
		while (child!=null)
		{
			if (child.getNodeType()==Node.ELEMENT_NODE)
			{
				if ((child.getFirstChild()!=null)&&(child.getFirstChild().getNodeType()==Node.TEXT_NODE))
				{
					text=child.getFirstChild().getNodeValue();
				}
				else
				{
					text="";
				}
				parseSubElement((Element)child,text);
			}
			child=child.getNextSibling();
		}
	}

	public Element getElement(Document builder)
	{
		Element node = script.getElement(builder);
		if (help!=null)
		{
			node.insertBefore(help.getElement(builder),node.getFirstChild());
		}
		node.setAttribute("name",getName());
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
		return script.run(server,variables);
	}
}
