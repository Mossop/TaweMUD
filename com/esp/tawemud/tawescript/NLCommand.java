package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import com.esp.tawemud.InfoPage;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.items.Item;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

public class NLCommand implements BaseCommand
{
	private GroupAction code;
	private CodeableObject owner;
	private List specs;
	private String name;
	private String version;
	private InfoPage help;
	
	public NLCommand(CodeableObject owner)
	{
		super();
		this.owner=owner;
		code = new GroupAction();
		code.setOwner(owner);
		code.setName("Code");
		specs = new LinkedList();
		version="0.00";
	}

	public String getName()
	{
		return name;
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
	
	public int getPriority()
	{
		return 10;
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
		if (node.getTagName().equals("Code"))
		{
			code.parseElement(node);
			return true;
		}
		else if (node.getTagName().equals("Spec"))
		{
			while (text.endsWith(" "))
			{
				text=text.substring(0,text.length()-1);
			}
			while (text.startsWith(" "))
			{
				text=text.substring(1);
			}
			Spec newspec = new Spec();
			newspec.setName(node.getAttribute("name"));
			newspec.setType(node.getAttribute("type"));
			newspec.setLevel(node.getAttribute("level"));
			newspec.setSpecText(text);
			specs.add(newspec);
			return true;
		}
		else if (node.getTagName().equals("InfoPage"))
		{
			help = new InfoPage();
			help.parseElement(node);
			return true;
		}
		else
		{
			return false;
		}
	}

	public void parseElement(Element node)
	{
		version=node.getAttribute("version");
		name=node.getAttribute("name");
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
		Element node = builder.createElement("NLCommand");
		node.setAttribute("name",getName());
		node.setAttribute("version",version);
		if (help!=null)
		{
			node.appendChild(help.getElement(builder));
		}
		int loop = 0;
		while (loop<specs.size())
		{
			Element subnode = ((Spec)specs.get(loop)).getElement(builder);
			node.appendChild(subnode);
			loop++;
		}
		node.appendChild(code.getElement(builder));
		return node;
	}

	public boolean callCommand(TaweServer server, Mobile caller, String calledname, String args)
	{
		boolean found=false;
		int loop=0;
		while ((!found)&&(loop<specs.size()))
		{
			Variables vars = new Variables();
			if (((Spec)specs.get(loop)).matches(caller,args,vars))
			{
				//caller.displayText("Matched spec "+((Spec)specs.get(loop)).getName());
				vars.setVariable("$owner",owner.toString());
				vars.setVariable("$spec",((Spec)specs.get(loop)).getName());
				vars.setVariable("$0",caller);
				vars.setVariable("$1",calledname);
				found=code.run(server,vars);
			}
			loop++;
		}
		return found;
	}
}
