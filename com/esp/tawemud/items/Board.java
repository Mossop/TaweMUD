package com.esp.tawemud.items;

import java.text.SimpleDateFormat;
import java.util.Vector;
import java.util.Iterator;
import java.io.PrintWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Mail;
import com.esp.tawemud.tawescript.Variables;

public class Board extends Item implements MailHandler
{
	private Vector mailbox;
	private int level;
	private String updatemessage;

	public Board(TaweServer server)
	{
		super(server);
		server.registerBoard(this);
		mailbox = new Vector(10);
		level=10000;
		updatemessage="@+RThere are new posts on the %n at %l";
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		node.setAttribute("level",Integer.toString(level));
		node.setAttribute("updatemessage",updatemessage);
	}

	public boolean hasName(String name)
	{
		if (name.toLowerCase().equals("food"))
		{
			return true;
		}
		else
		{
			return super.hasName(name);
		}
	}

	public String getUpdateMessage()
	{
		return updatemessage;
	}

	public void setUpdateMessage(String message)
	{
		updatemessage=message;
	}

	public int getLevel()
	{
		return level;
	}

	public void setLevel(int value)
	{
		level=value;
	}

	public String getStoreableDescription()
	{
		return super.getDescription();
	}

	public String getDescription()
	{
		return super.getDescription()+"@/"+getMessages();
	}

	public String getNewMessage()
	{
		Variables vars = new Variables();
		vars.setVariable("%n",getName());
		vars.setVariable("%l",getLocation().getName());
		return vars.parseString(updatemessage);
	}

	public void addMail(Mail message)
	{
		mailbox.addElement(message);
		if (!message.isRead())
		{
			if (zone!=null)
			{
				Iterator loop = getServer().getPlayers();
				while (loop.hasNext())
				{
					Player thisone = (Player)loop.next();
					if ((thisone.isDPower())||(thisone.getLevel()>=level))
					{
						if ((getLocation()!=null)&&(message.getDate().after(thisone.getLogOnDate())))
						{
							thisone.displayText(getNewMessage());
						}
					}
				}
			}
		}
	}

	public int getMailCount()
	{
		return mailbox.size();
	}

	public Mail getMail(int message)
	{
		if (message<mailbox.size())
		{
			return (Mail)mailbox.elementAt(message);
		}
		else
		{
			return null;
		}
	}

	public void removeMail(int message)
	{
		mailbox.remove(message);
	}

	public String getMessages()
	{
		StringBuffer buffer = new StringBuffer("Msg From            Subject                                     Date@/@/");
		for (int loop=0; loop<mailbox.size(); loop++)
		{
			StringBuffer line = new StringBuffer();
			Mail message = (Mail)mailbox.elementAt(loop);
			String item = Integer.toString(loop+1);
			while (item.length()<3)
			{
				item=" "+item;
			}
			line.append(item+" ");
			item=message.getSender();
			while (item.length()<15)
			{
				item=item+" ";
			}
			line.append(item+" "+message.getSubject());
			while (line.length()<63)
			{
				line.append(" ");
			}
			line.setLength(63);
			line.append(" ");
			SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
			line.append(dateformat.format(message.getDate()));
			buffer.append(line);
			buffer.append("@/");
		}
		return buffer.toString();
	}

	public Board asBoard()
	{
		return this;
	}

	public MailHandler getMailHandler()
	{
		return this;
	}
}
