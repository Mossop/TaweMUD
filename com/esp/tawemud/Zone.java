package com.esp.tawemud;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Iterator;
import java.util.ListIterator;
import java.lang.reflect.Constructor;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.tawescript.Subroutine;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileWriter;
import java.io.File;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.items.Player;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.items.Container;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.esp.tawemud.xml.XmlWriter;
import java.net.URL;

public class Zone extends CodeableObject
{
	private String name;
	private Map items;
	private Map mobiles;
	protected World world;
	private int difficulty;
	private long lastcheck;
	private int checkcount;
	private int qpoints;
	private int itemcount;
	private int doorcount;
	private int weaponcount;
	private int mobilecount;
	private int containercount;
	private int roomcount;
	private int foodcount;
	private int objectcount;

	public Zone(World ourworld)
	{
		super(ourworld.getServer());
		world=ourworld;
		name="";
		items = new HashMap();
		mobiles = new HashMap();
		difficulty=0;
		lastcheck=System.currentTimeMillis();
		checkcount=0;
		qpoints=0;
		itemcount=0;
		doorcount=0;
		weaponcount=0;
		mobilecount=0;
		containercount=0;
		roomcount=0;
		foodcount=0;
		objectcount=0;
	}

	public String toString()
	{
		return getIdentifier();
	}

	public boolean save()
	{
		try
		{
			DOMImplementation domimp = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().getImplementation();
			DocumentType dt = domimp.createDocumentType("Zone",null,"../dtd/zone.dtd");
			Document doc = domimp.createDocument(null,"Zone",dt);
			storeInElement(doc,doc.getDocumentElement());
			FileWriter output = new FileWriter(new File((new URL(world.getServer().getWorldURL()+"zones/"+getIdentifier()+".xml")).getFile()));
			XmlWriter.write(doc,output);
			output.close();
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (!super.parseSubElement(child,text,out))
		{
			String classname = child.getAttribute("class");
			Item item = null;
			if ((classname!=null)&&(!classname.equals("")))
			{
				try
				{
					Class thisclass = Class.forName(classname);
					Class[] paramtypes = new Class[1];
					paramtypes[0]=world.getServer().getClass();
					Constructor starter = thisclass.getConstructor(paramtypes);
					Object[] params = new Object[1];
					params[0]=world.getServer();
					item = (Item)starter.newInstance(params);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				item = getDefaultItem(child.getTagName());
			}
			if (item!=null)
			{
				item.setZone(this);
				item.parseElement(child,out);
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return true;
		}
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		node.setAttribute("difficulty",Integer.toString(difficulty));
		node.setAttribute("qpoints",Integer.toString(qpoints));
		node.setAttribute("name",name);
		Iterator loop = getItemIterator();
		while (loop.hasNext())
		{
			node.appendChild(((Item)loop.next()).getElement(builder));
		}
		loop = getMobileIterator();
		while (loop.hasNext())
		{
			node.appendChild(((Item)loop.next()).getElement(builder));
		}
	}

	public int getItemCount()
	{
		return itemcount;
	}

	public int getDoorCount()
	{
		return doorcount;
	}

	public int getWeaponCount()
	{
		return weaponcount;
	}

	public int getMobileCount()
	{
		return mobilecount;
	}

	public int getContainerCount()
	{
		return containercount;
	}

	public int getRoomCount()
	{
		return roomcount;
	}

	public int getFoodCount()
	{
		return foodcount;
	}

	public int getObjectCount()
	{
		return objectcount;
	}

	public boolean canReset()
	{
		boolean good=true;
		Vector zones = new Vector(10);
		Zone thiszone;
		Player thisplayer;
		Iterator loop = world.getServer().getPlayers();
		while ((good)&&(loop.hasNext()))
		{
			thisplayer=(Player)loop.next();
			if (!thisplayer.isWiz())
			{
				thiszone=thisplayer.getRoomLocation().getZone();
				if (thiszone==this)
				{
					good=false;
				}
				if (!zones.contains(thiszone))
				{
					zones.addElement(thiszone);
				}
			}
		}
		loop = items.values().iterator();
		while ((loop.hasNext())&&(good))
		{
			Item thisitem = (Item)loop.next();
			if (thisitem.asRoom()==null)
			{
				if ((thisitem.getLocation()!=null)&&(!thisitem.getLocation().getWorldIdentifier().equals(thisitem.getStartLocation())))
				{
					Room roomloc = thisitem.getRoomLocation();
					if (roomloc!=null)
					{
						thiszone=roomloc.getZone();
						if ((thiszone!=this)&&(zones.contains(thiszone)))
						{
							good=false;
						}
						else
						{
							Container location = thisitem.getLocation();
							while ((location!=null)&&(location.asPlayer()==null))
							{
								location=location.getLocation();
							}
							if (location!=null)
							{
								good=false;
							}
						}
					}
				}
			}
		}
		loop=mobiles.values().iterator();
		while ((loop.hasNext())&&(good))
		{
			Item thisitem=(Item)loop.next();
			if (thisitem.checkFlag("aliased"))
			{
				good=false;
			}
			else
			{
				if (!thisitem.getLocation().getWorldIdentifier().equals(thisitem.getStartLocation()))
				{
					thiszone=thisitem.getRoomLocation().getZone();
					if (zones.contains(thiszone))
					{
						good=false;
					}
				}
			}
		}
		return good;
	}

	public void reset()
	{
		Iterator loop = (new HashMap(items)).values().iterator();
		while (loop.hasNext())
		{
			((Item)loop.next()).reset();
		}
		loop=(new HashMap(mobiles)).values().iterator();
		while (loop.hasNext())
		{
			((Mobile)loop.next()).reset();
		}
		super.reset();
	}

	public Iterator getMobileIterator()
	{
		return mobiles.values().iterator();
	}

	public Iterator getItemIterator()
	{
		return items.values().iterator();
	}

	public void addItem(Item item)
	{
		objectcount++;
		if (item.asMobile()!=null)
		{
			mobiles.put(item.getIdentifier().toLowerCase(),item);
			if (item.asPlayer()==null)
			{
				mobilecount++;
			}
		}
		else
		{
			items.put(item.getIdentifier().toLowerCase(),item);
			if (item.asContainer()!=null)
			{
				if (item.asRoom()!=null)
				{
					roomcount++;
				}
				else
				{
					containercount++;
				}
			}
			else if (item.asWeapon()!=null)
			{
				weaponcount++;
			}
			else if (item.asFood()!=null)
			{
				foodcount++;
			}
			else if (item.asDoor()!=null)
			{
				doorcount++;
			}
			else
			{
				itemcount++;
			}
		}
	}

	public void removeItem(Item item)
	{
		objectcount--;
		if (item.asMobile()!=null)
		{
			mobiles.remove(item.getIdentifier().toLowerCase());
			if (item.asPlayer()==null)
			{
				mobilecount--;
			}
		}
		else
		{
			items.remove(item.getIdentifier().toLowerCase());
			if (item.asContainer()!=null)
			{
				if (item.asRoom()!=null)
				{
					roomcount--;
				}
				else
				{
					containercount--;
				}
			}
			else if (item.asWeapon()!=null)
			{
				weaponcount--;
			}
			else if (item.asFood()!=null)
			{
				foodcount--;
			}
			else if (item.asDoor()!=null)
			{
				doorcount--;
			}
			else
			{
				itemcount--;
			}
		}
	}

	public Item getDefaultItem(String itemname)
	{
		return world.getDefaultItem(itemname);
	}

	public String getName()
	{
		return name;
	}

	public void setName(String newname)
	{
		name=newname;
	}

	public int getDifficulty()
	{
		return difficulty;
	}

	public void setDifficulty(int value)
	{
		difficulty=value;
	}

	public int getQPoints()
	{
		return qpoints;
	}

	public void setQPoints(int value)
	{
		qpoints=value;
	}

	public void setWorld(World newworld)
	{
		world=newworld;
	}

	public World getWorld()
	{
		return world;
	}

	public Subroutine findSubroutine(String name)
	{
		Subroutine result = super.findSubroutine(name);
		if (result==null)
		{
			result=world.findSubroutine(name);
		}
		return result;
	}

	public Item findItem(String name)
	{
		Item result = null;
		if (name.indexOf(".")>-1)
		{
			result=world.findItem(name);
		}
		else
		{
			Map allitems = new HashMap();
			allitems.putAll(items);
			allitems.putAll(mobiles);
			result=(Item)allitems.get(name.toLowerCase());
		}
		return result;
	}

	public Item findItemByName(String name)
	{
		Item result = null;
		Iterator loop = items.values().iterator();
		Item thisitem;
		while ((loop.hasNext())&&(result==null))
		{
			thisitem=(Item)loop.next();
			if (thisitem.hasName(name))
			{
				result=thisitem;
			}
		}
		return result;
	}

	public Mobile findMobileByName(String name)
	{
		Mobile result = null;
		Iterator loop = mobiles.values().iterator();
		Mobile thisitem;
		while ((loop.hasNext())&&(result==null))
		{
			thisitem=(Mobile)loop.next();
			if (thisitem.hasName(name))
			{
				result=thisitem;
			}
		}
		return result;
	}

	public void place()
	{
		super.place();
		Iterator loop = items.values().iterator();
		while (loop.hasNext())
		{
			((Item)loop.next()).place();
		}
		loop=mobiles.values().iterator();
		while (loop.hasNext())
		{
			((Item)loop.next()).place();
		}
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		super.updateReferences(oldref,newref);
		Iterator loop = items.values().iterator();
		while (loop.hasNext())
		{
			((Item)loop.next()).updateReferences(oldref,newref);
		}
		loop=mobiles.values().iterator();
		while (loop.hasNext())
		{
			((Item)loop.next()).updateReferences(oldref,newref);
		}
	}

	public Zone asZone()
	{
		return this;
	}

	public void cycle()
	{
		if (!checkFlag("Stopped"))
		{
			try
			{
				super.cycle();
				Iterator loop = items.values().iterator();
				while (loop.hasNext())
				{
					((Item)loop.next()).cycle();
				}
				loop=mobiles.values().iterator();
				while (loop.hasNext())
				{
					((Item)loop.next()).cycle();
				}
				if (!getIdentifier().equals("players"))
				{
					if ((System.currentTimeMillis()-lastcheck)>60000)
					{
						lastcheck=System.currentTimeMillis();
						if (canReset())
						{
							if (checkcount<6)
							{
								checkcount++;
							}
						}
						else
						{
							checkcount=0;
						}
					}
					if (checkcount==5)
					{
						reset();
						checkcount=6;
					}
				}
			}
			catch (Throwable e)
			{
				world.getServer().logCrash(e);
				setFlag("Stopped");
				world.getServer().sendWizMessage(getIdentifier()+" zone cycle caused a crash - The zone has been stopped.","",0,3,true);
			}
		}
	}
}
