package com.esp.tawemud.items;

import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Iterator;
import java.io.PrintWriter;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Container extends Item
{
	private int maxweight;
	private int maxcapacity;
	protected List itemcontents;

	public Container(TaweServer server)
	{
		super(server);
		maxweight=0;
		maxcapacity=0;
		itemcontents = new LinkedList();
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		if (maxweight!=0)
		{
			node.setAttribute("maxweight",Integer.toString(maxweight));
		}
		if (maxcapacity!=0)
		{
			node.setAttribute("maxcapacity",Integer.toString(maxcapacity));
		}
	}

	public void addItem(Item item)
	{
		itemcontents.add(item);
	}

	public void removeItem(Item item)
	{
		itemcontents.remove(item);
	}

	public Item findItemByName(String name)
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
			Iterator loop = itemcontents.iterator();
			Item result = null;
			int current=0;
			Item thisitem;
			while ((loop.hasNext())&&(result==null))
			{
				thisitem=(Item)loop.next();
				if (thisitem.hasName(name))
				{
					current++;
					if (count==current)
					{
						result=thisitem;
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

	public int getBasicValue()
	{
		return (Math.max(maxcapacity,maxweight)*10)+10;
	}

	public int getMaxWeight()
	{
		return maxweight;
	}

	public int getMaxCapacity()
	{
		return maxcapacity;
	}

	public int getCurrentWeight()
	{
		int count=0;
		Iterator loop = itemcontents.iterator();
		while (loop.hasNext())
		{
			count=count+((Item)loop.next()).getWeight();
		}
		return count;
	}

	public int getCurrentCapacity()
	{
		int count=0;
		Iterator loop = itemcontents.iterator();
		while (loop.hasNext())
		{
			Item thisone = (Item)loop.next();
			if ((!thisone.checkFlag("worn"))&&(!thisone.checkFlag("wielded")))
			{
				count=count+thisone.getSize();
			}
		}
		return count;
	}

	public void setMaxWeight(int val)
	{
		maxweight=val;
	}

	public void setMaxCapacity(int val)
	{
		maxcapacity=val;
	}

	public ListIterator getItemContentsIterator()
	{
		return itemcontents.listIterator();
	}

	public Container asContainer()
	{
		return this;
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (itemcontents.contains(oldref))
		{
			while (itemcontents.contains(oldref))
			{
				itemcontents.remove(oldref);
			}
			newref.asItem().setLocation(this);
		}
		super.updateReferences(oldref,newref);
	}
	
	public void reset()
	{
		Item thisone;
		Room destroyed = zone.findItem("permanent.destroyed").asRoom();
		for (int loop=0; loop<itemcontents.size(); loop++)
		{
			thisone=(Item)itemcontents.get(loop);
			if (!thisone.getZone().equals(zone))
			{
				zone.getWorld().getServer().doMove(thisone,destroyed);
			}
		}
		super.reset();
	}
}
