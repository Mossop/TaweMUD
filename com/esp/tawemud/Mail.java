package com.esp.tawemud;

import java.io.PrintWriter;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Holds a mail message.
 *
 * These mails exist in players as mails, and boards as bulletin board posts.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Mail
{
	/**
	 * The mails subject.
	 */
	private StringBuffer subject;
	/**
	 * The text of the message.
	 */
	private StringBuffer content;
	/**
	 * The person who sent the message.
	 */
	private String sender;
	/**
	 * Whether the message has been read or not.
	 */
	private boolean read;
	/**
	 * The date the message was sent.
	 */
	private Date date;

	/**
	 * Creates a blank mail.
	 */
	public Mail()
	{
		subject = new StringBuffer("");
		content = new StringBuffer("");
		sender="";
		date = new Date();
		read=false;
	}

	/**
	 * Creates a mail setting certain parameters.
	 *
	 * @param thesender Who sent the message.
	 * @param isread  If the message is read or not (&quot;true&quot; or &quot;false&quot;)
	 * @param thisdate  The date the message was sent (in text format)
	 */
	public Mail(String thesender, String isread, String thisdate)
	{
		this();
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		try
		{
			date=formatter.parse(thisdate);
		}
		catch (Exception e)
		{
		}
		sender=thesender;
		read=(new Boolean(isread)).booleanValue();
	}

	/**
	 * Returns this mail as an xml element.
	 *
	 * @param builder An xml document to build the element with
	 */
	public Element getElement(Document builder)
	{
		Element node = builder.createElement("Mail");
		node.setAttribute("sender",sender);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		node.setAttribute("date",formatter.format(date));
		if (read)
		{
			node.setAttribute("read","true");
		}
		else
		{
			node.setAttribute("read","false");
		}
		Element subj = builder.createElement("Subject");
		subj.appendChild(builder.createTextNode(subject.toString()));
		Element cont = builder.createElement("Content");
		cont.appendChild(builder.createTextNode(content.toString()));
		node.appendChild(subj);
		node.appendChild(cont);
		return node;
	}

	/**
	 * Loads this mail from an xml element.
	 *
	 * @param node  the xml element
	 * @param out A PrintWriter for logging
	 */
	public void parseElement(Element node, PrintWriter out)
	{
		sender=node.getAttribute("sender");
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		try
		{
			date=formatter.parse(node.getAttribute("date"));
		}
		catch (Exception e)
		{
		}
		read=Boolean.valueOf(node.getAttribute("read")).booleanValue();
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				StringBuffer text;
				Node sub = thisone.getFirstChild();
				if (sub!=null)
				{
					text=new StringBuffer(sub.getNodeValue());
				}
				else
				{
					text = new StringBuffer();
				}
				if (thisone.getTagName().equals("Subject"))
				{
					subject=text;
				}
				else if (thisone.getTagName().equals("Content"))
				{
					content=text;
				}
			}
		}
	}

	public Date getDate()
	{
		return date;
	}

	public void setDate(Date newdate)
	{
		date=newdate;
	}

	public StringBuffer getSubject()
	{
		return subject;
	}

	public void setSubject(StringBuffer newsubject)
	{
		subject=newsubject;
	}

	public StringBuffer getContent()
	{
		return content;
	}

	public void setContent(StringBuffer newcontent)
	{
		content=newcontent;
	}

	public String getSender()
	{
		return sender;
	}

	public void setSender(String newsender)
	{
		sender=newsender;
	}

	public boolean isRead()
	{
		return read;
	}

	public void setRead(boolean newread)
	{
		read=newread;
	}
}
