package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import com.esp.tawemud.CodeableObject;

public abstract class Action implements Cloneable
{
	public final static byte AT_SINGLEACTION = 0;
	public final static byte AT_CONTENTACTION = 1;
	public final static byte AT_TESTACTION = 2;
	public final static byte AT_GROUPACTION = 3;
	private Action parent;
	private String name;
	private CodeableObject owner = null;
	
	public Action()
	{
		name=getClass().getName().substring(TaweServer.PACKAGE.length()+12);
	}

	public long getLastTick()
	{
		if (parent!=null)
		{
			return parent.getLastTick();
		}
		else
		{
			return 0;
		}
	}

	public void setOwner(CodeableObject newowner)
	{
		owner=newowner;
	}
	
	public CodeableObject getOwner()
	{
		if (owner!=null)
		{
			return owner;
		}
		else if (parent!=null)
		{
			return parent.getOwner();
		}
		else
		{
			return null;
		}
	}

	public void setParent(Action thisparent)
	{
		parent=thisparent;
	}

	public void setName(String newname)
	{
		name=newname;
	}
	
	public String getName()
	{
		return name;
	}

	public abstract boolean parseSubElement(Element node, String text);

	public boolean parseTextElement(String text)
	{
		return false;
	}

	public void parseElement(Element node)
	{
		NamedNodeMap attrs = node.getAttributes();
		for (int loop=0; loop<attrs.getLength(); loop++)
		{
			Node attr = attrs.item(loop);
			try
			{
				Field field = getClass().getDeclaredField(attr.getNodeName());
				if (!Modifier.isFinal(field.getModifiers()))
				{
					field.set(this,attr.getNodeValue());
				}
			}
			catch (Exception e)
			{
			}
		}
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
			if (child.getNodeType()==Node.TEXT_NODE)
			{
				parseTextElement(child.getNodeValue());
			}
			child=child.getNextSibling();
		}
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement(name);
		Field[] attrs = getClass().getDeclaredFields();
		for (int loop=0; loop<attrs.length; loop++)
		{
			if (!Modifier.isFinal(attrs[loop].getModifiers()))
			{
				try
				{
					node.setAttribute(attrs[loop].getName(), attrs[loop].get(this).toString());
				}
				catch (Exception e)
				{
				}
			}
		}
		return node;
	}

	public abstract int getType();

	public abstract boolean run(TaweServer server, Variables variables);
}
