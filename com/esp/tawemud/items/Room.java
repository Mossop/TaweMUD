package com.esp.tawemud.items;

import java.util.Vector;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import com.esp.tawemud.Exit;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class Room extends Container
{
	private Exit[] exits;
	private Exit[] startexits;

	private List mobilecontents;

	public static String getDirection(int dir)
	{
		switch(dir)
		{
			case 0:		return "n";
			case 1:		return "e";
			case 2:		return "u";
			case 3:		return "ne";
			case 4:		return "nw";
			case 5:		return "s";
			case 6:		return "w";
			case 7:		return "d";
			case 8:		return "sw";
			case 9:		return "se";
			default:	return "";
		}
	}

	public static int getDirection(String dir)
	{
		String real=dir.toLowerCase();
		if (real.equals("n"))
		{
			return 0;
		}
		else if (real.equals("e"))
		{
			return 1;
		}
		else if (real.equals("u"))
		{
			return 2;
		}
		else if (real.equals("ne"))
		{
			return 3;
		}
		else if (real.equals("nw"))
		{
			return 4;
		}
		else if (real.equals("s"))
		{
			return 5;
		}
		else if (real.equals("w"))
		{
			return 6;
		}
		else if (real.equals("d"))
		{
			return 7;
		}
		else if (real.equals("sw"))
		{
			return 8;
		}
		else if (real.equals("se"))
		{
			return 9;
		}
		else
		{
			return -1;
		}
	}

	public Room(TaweServer server)
	{
		super(server);
		startexits = new Exit[10];
		exits = new Exit[10];
		for (int loop=0; loop<10; loop++)
		{
			exits[loop]=null;
			startexits[loop]=null;
		}
		mobilecontents = new LinkedList();
		setMaxCapacity(10000);
		setMaxWeight(10000);
	}

	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (child.getTagName().equals("StartExits"))
		{
			NodeList subnodes = child.getChildNodes();
			Element thisexit;
			for (int subloop=0; subloop<subnodes.getLength(); subloop++)
			{
				if (subnodes.item(subloop).getNodeType()==Node.ELEMENT_NODE)
				{
					thisexit = (Element)subnodes.item(subloop);
					if (thisexit.getTagName().equals("Exit"))
					{
						Exit exit = new Exit(this);
						exit.parseElement(thisexit);
						startexits[getDirection(thisexit.getAttribute("direction"))]=exit;
					}
				}
			}
			return true;
		}
		else if (child.getTagName().equals("Exits"))
		{
			NodeList subnodes = child.getChildNodes();
			Element thisexit;
			for (int subloop=0; subloop<subnodes.getLength(); subloop++)
			{
				if (subnodes.item(subloop).getNodeType()==Node.ELEMENT_NODE)
				{
					thisexit = (Element)subnodes.item(subloop);
					if (thisexit.getTagName().equals("Exit"))
					{
						Exit exit = new Exit(this);
						exit.parseElement(thisexit);
						exits[getDirection(thisexit.getAttribute("direction"))]=exit;
					}
				}
			}
			return true;
		}
		else
		{
			return super.parseSubElement(child,text,out);
		}
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		Element exitnode = builder.createElement("StartExits");
		for (int loop=0; loop<10; loop++)
		{
			if (startexits[loop]!=null)
			{
				Element exit=startexits[loop].getElement(builder);
				exit.setAttribute("direction",getDirection(loop));
				exitnode.appendChild(exit);
			}
		}
		node.appendChild(exitnode);
		exitnode = builder.createElement("Exits");
		for (int loop=0; loop<10; loop++)
		{
			if (exits[loop]!=null)
			{
				Element exit=exits[loop].getElement(builder);
				exit.setAttribute("direction",getDirection(loop));
				exitnode.appendChild(exit);
			}
		}
		node.appendChild(exitnode);
	}

	public void setLocation(Container loc)
	{
	}

	public void setStartLocation(String loc)
	{
	}

	public Room getRoomLocation()
	{
		return this;
	}

	public void addStartExit(Exit exit, int direction)
	{
		if ((direction>=0)&&(direction<10))
		{
			startexits[direction]=exit;
		}
	}

	public void addExit(Exit exit, int direction)
	{
		if ((direction>=0)&&(direction<10))
		{
			exits[direction]=exit;
		}
	}

	public void removeExit(int direction)
	{
		if ((direction>=0)&&(direction<10))
		{
			exits[direction]=null;
		}
	}

	public void removeStartExit(int direction)
	{
		if ((direction>=0)&&(direction<10))
		{
			startexits[direction]=null;
		}
	}

	public Exit getExit(int direction)
	{
		return exits[direction];
	}

	public Mobile findMobileByName(String name)
	{
		String val = "";
		while ((name.length()>0)&&(Character.isDigit(name.charAt(name.length()-1))))
		{
			val=name.substring(name.length()-1)+val;
			name=name.substring(0,name.length()-1);
		}
		if (name.length()>0)
		{
			int count;
			if (val.length()>0)
			{
				count=Integer.parseInt(val);
			}
			else
			{
				count=1;
			}
			Iterator loop = mobilecontents.iterator();
			Mobile result = null;
			int current=0;
			Mobile thisone;
			while ((loop.hasNext())&&(result==null))
			{
				thisone=(Mobile)loop.next();
				if (thisone.hasName(name))
				{
					current++;
					if (count==current)
					{
						result=thisone;
					}
				}
			}
			return result;
		}
		else
		{
			return null;
		}
	}

	public ListIterator getMobileContentsIterator()
	{
		return mobilecontents.listIterator();
	}

	public void addItem(Item item)
	{
		if (item.asMobile()!=null)
		{
			addMobile(item.asMobile());
		}
		else
		{
			super.addItem(item);
		}
	}

	public void removeItem(Item item)
	{
		if (item.asMobile()!=null)
		{
			removeMobile(item.asMobile());
		}
		else
		{
			super.removeItem(item);
		}
	}

	private void addMobile(Mobile item)
	{
		mobilecontents.add(item);
	}

	private void removeMobile(Mobile item)
	{
		mobilecontents.remove(item);
	}

	public void displayText(String noshow, int vis, String text)
	{
		Iterator loop = mobilecontents.iterator();
		while (loop.hasNext())
		{
			((Mobile)loop.next()).displayText(noshow,vis,text);
		}
	}

	public Room asRoom()
	{
		return this;
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (mobilecontents.contains(oldref))
		{
			while (mobilecontents.contains(oldref))
			{
				mobilecontents.remove(oldref);
			}
			if (newref!=null)
			{
				newref.asItem().setLocation(this);
			}
		}
		super.updateReferences(oldref,newref);
	}
	
	public void reset()
	{
		for (int loop=0; loop<10; loop++)
		{
			exits[loop]=startexits[loop];
		}
		super.reset();
	}
}
