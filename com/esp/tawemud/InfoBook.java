package com.esp.tawemud;

import java.util.HashMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import java.io.PrintWriter;

/**
 * A collection of info pages.
 */
public class InfoBook
{
	/**
	 * The name of this book.
	 */
	private String name;
	/**
	 * The pages in this book.
	 */
	private HashMap pages;

	/**
	 * Creates a new empty book.
	 */
	public InfoBook()
	{
		name="";
		pages = new HashMap();
	}

	public String getName()
	{
		return name;
	}

	/**
	 * Returns the requested page from this book.
	 *
	 * @param page  the requested page.
	 * @return  The requested page or null if it does not exist.
	 */
	public InfoPage getPage(String page)
	{
		return (InfoPage)pages.get(page);
	}

	/**
	 * Adds a new page to the book.
	 *
	 * @param page  The new page to be added
	 */
	public void addPage(InfoPage page)
	{
		pages.put(page.getName(),page);
	}

	/**
	 * Loads a book from an xml element.
	 *
	 * @param node  The xml element
	 * @param out A PrintWriter for logging
	 */
	public void parseElement(Element node, PrintWriter out)
	{
		name=node.getAttribute("name").toLowerCase();
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				if (thisone.getTagName().equals("InfoPage"))
				{
					InfoPage page = new InfoPage();
					page.parseElement(thisone);
					addPage(page);
				}
			}
		}
	}
}
