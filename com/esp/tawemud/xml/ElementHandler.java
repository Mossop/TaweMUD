package com.esp.tawemud.xml;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import java.io.PrintWriter;

public class ElementHandler extends BaseHandler
{
	private Document builder;
	private Node node;

	public ElementHandler(Document builder, Node node, PrintWriter out)
	{
		super(out);
		this.builder=builder;
		this.node=node;
	}

	public void startElement(String name, Attributes attrs)
	{
		Element element = builder.createElement(name);
		for (int loop=0; loop<attrs.getLength(); loop++)
		{
			element.setAttribute(attrs.getLocalName(loop),attrs.getValue(loop));
		}
		node.appendChild(element);
		registerHandler(new ElementHandler(builder,element,out));
	}

	public void endElement(String name) throws SAXException
	{
		super.endElement(name);
		Node current = node.getFirstChild();
		while ((current!=null)&&(current.getNextSibling()!=null))
		{
			if ((current.getNodeType()==Node.TEXT_NODE)&&(current.getNextSibling().getNodeType()==Node.TEXT_NODE))
			{
				current.setNodeValue(current.getNodeValue()+current.getNextSibling().getNodeValue());
				node.removeChild(current.getNextSibling());
			}
			else
			{
				current=current.getNextSibling();
			}
		}
	}

	public void skippedEntity(String name)
	{
		out.println("Skipped "+name+" entity.@/");
	}

	public void characters(char[] ch, int start, int length)
	{
		node.appendChild(builder.createTextNode(new String(ch,start,length)));
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
	{
		characters(ch,start,length);
	}
}
