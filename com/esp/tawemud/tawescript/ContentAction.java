package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public abstract class ContentAction extends SingleAction
{
	protected String contents;

	public ContentAction()
	{
		contents="";
	}

	public boolean parseTextElement(String text)
	{
		contents+=text;
		return true;
	}
	
	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		if (contents.length()>0)
		{
			node.appendChild(builder.createTextNode(contents));
		}
		return node;
	}

	public int getType()
	{
		return Action.AT_CONTENTACTION;
	}

	public String getContents()
	{
		return contents;
	}

	public void setContents(String data)
	{
		contents=data;
	}
}
