package com.esp.tawemud.items;

import java.util.Iterator;
import java.util.StringTokenizer;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.CodeableObject;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.ListIterator;
import java.lang.reflect.Method;
import com.esp.tawemud.Zone;
import com.esp.tawemud.World;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import com.esp.tawemud.tawescript.Subroutine;
import com.esp.tawemud.tawescript.Special;
import javax.xml.parsers.DocumentBuilderFactory;

public class Item extends CodeableObject
{
	protected Zone zone=null;
	protected List names = new ArrayList();
	private short size=0;
	private short weight=0;
	private short visibility=0;
	protected Container startlocation = null;
	protected Container location = null;
	private String state="default";
	private String startstate="default";
	private HashMap statedescs = new HashMap();
	private List wearplaces = new ArrayList();
	private List wearflags = new ArrayList();
	private int bonusvalue=0;
	private short armour=0;
	private Item clonedfrom;
	private boolean init=true;

	public Item(TaweServer server)
	{
		super(server);
		if (server.getWorld()!=null)
		{
			location=(Container)server.getWorld().findItem("permanent.bin");
			startlocation=location;
		}
		clonedfrom=this;
	}

	public boolean inInit()
	{
		return init;
	}

	public String getType()
	{
		Class type = getClass();
		while (type.getName().lastIndexOf(".")>(TaweServer.PACKAGE.length()+6))
		{
			type=type.getSuperclass();
		}
		String typename=type.getName().substring(TaweServer.PACKAGE.length()+7);
		return typename;
	}

	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (child.getTagName().equals("Name"))
		{
			addName(new StringBuffer(text));
			return true;
		}
		else if (child.getTagName().equals("State"))
		{
			addState(child.getAttribute("name"),new StringBuffer(text));
			return true;
		}
		else if (child.getTagName().equals("WearPlaces"))
		{
			setWearPlaces(text);
			return true;
		}
		else if (child.getTagName().equals("WearFlags"))
		{
			setFlags(text,wearflags);
			return true;
		}
		else if (child.getTagName().equals("Mailbox"))
		{
			NodeList mails = child.getChildNodes();
			for (int mailloop=0; mailloop<mails.getLength(); mailloop++)
			{
				if (mails.item(mailloop).getNodeType()==Node.ELEMENT_NODE)
				{
					Element thismail = (Element)mails.item(mailloop);
					if (thismail.getTagName().equals("Mail"))
					{
						Mail newmail = new Mail();
						newmail.parseElement(thismail,out);
						getMailHandler().addMail(newmail);
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
		if (clonedfrom!=this)
		{
			node.setAttribute("clonedfrom",clonedfrom.getWorldIdentifier());
		}
		if (bonusvalue!=0)
		{
			node.setAttribute("bonusvalue",Integer.toString(bonusvalue));
		}
		if (armour!=0)
		{
			node.setAttribute("armour",Integer.toString(armour));
		}
		if (size!=0)
		{
			node.setAttribute("size",Integer.toString(size));
		}
		if (weight!=0)
		{
			node.setAttribute("weight",Integer.toString(weight));
		}
		if (visibility!=0)
		{
			node.setAttribute("visibility",Integer.toString(visibility));
		}
		if (asRoom()==null)
		{
			if (startlocation!=null)
			{
				node.setAttribute("startlocation",startlocation.getWorldIdentifier());
			}
			if (location!=null)
			{
				if (location.getZone().equals(zone))
				{
					node.setAttribute("location",location.getIdentifier());
				}
				else
				{
					node.setAttribute("location",location.toString());
				}
			}
		}
		if (!startstate.equals("default"))
		{
			node.setAttribute("startstate",startstate);
		}
		if (!state.equals("default"))
		{
			node.setAttribute("state",state);
		}
		Iterator loop = names.iterator();
		Element name;
		while (loop.hasNext())
		{
			name = builder.createElement("Name");
			name.appendChild(builder.createTextNode(loop.next().toString()));
			node.appendChild(name);
		}
		Iterator wear = wearplaces.iterator();
		if (wear.hasNext())
		{
			Element places = builder.createElement("WearPlaces");
			StringBuffer contents = new StringBuffer();
			while (wear.hasNext())
			{
				contents.append(wear.next().toString());
				if (wear.hasNext())
				{
					contents.append(",");
				}
			}
			places.appendChild(builder.createTextNode(contents.toString()));
			node.appendChild(places);
		}
		String flags = getFlagList(wearflags);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("WearFlags");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		Iterator keys = statedescs.keySet().iterator();
		while (keys.hasNext())
		{
			String key = (String)keys.next();
			Element statedesc = builder.createElement("State");
			statedesc.setAttribute("name",key);
			statedesc.appendChild(builder.createTextNode(statedescs.get(key).toString()));
			node.appendChild(statedesc);
		}
		if (getMailHandler()!=null)
		{
			MailHandler mh = getMailHandler();
			Element mailbox = builder.createElement("Mailbox");
			for (int mailloop=0; mailloop<mh.getMailCount(); mailloop++)
			{
				mailbox.appendChild(mh.getMail(mailloop).getElement(builder));
			}
			node.appendChild(mailbox);
		}
	}

	protected List getFlags(String type)
	{
		if (type.equals("wear"))
		{
			return wearflags;
		}
		else
		{
			return super.getFlags(type);
		}
	}
	
	public Item clone(String newid)
	{
		try
		{
			StringWriter buffer = new StringWriter();
			PrintWriter out = new PrintWriter(buffer);
			Item newitem = zone.getDefaultItem(getType());
			newitem.parseElement(getElement(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()),out);
			newitem.setIdentifier(newid);
			newitem.setZone(zone);
			newitem.place();
			return newitem;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public String toString()
	{
		return getWorldIdentifier();
	}

	public boolean checkFlag(String flag)
	{
		return ((super.checkFlag(flag))||((super.checkFlag("worn"))&&(checkFlag(flag,wearflags))));
	}

	public Subroutine findSubroutine(String name)
	{
		Subroutine result = super.findSubroutine(name);
		if (result==null)
		{
			result=zone.findSubroutine(name);
		}
		return result;
	}

	public void findCodeableObjects(List objects)
	{
		objects.add(this);
		if (asContainer()!=null)
		{
			Iterator loop = asContainer().getItemContentsIterator();
			while (loop.hasNext())
			{
				objects.add(loop.next());
			}
			if (asRoom()!=null)
			{
				loop=asRoom().getMobileContentsIterator();
				while (loop.hasNext())
				{
					objects.add(loop.next());
				}
			}
		}
		Zone loczone = null;
		if (asRoom()==null)
		{
			objects.add(getLocation());
			Iterator loop = getLocation().getItemContentsIterator();
			while (loop.hasNext())
			{
				Object thisone = loop.next();
				if (thisone!=this)
				{
					objects.add(thisone);
				}
			}
			if (getLocation().asRoom()!=null)
			{
				loop=getLocation().asRoom().getMobileContentsIterator();
				while (loop.hasNext())
				{
					Object thisone = loop.next();
					if (thisone!=this)
					{
						objects.add(thisone);
					}
				}
			}
			loczone=getLocation().getZone();
		}
		if (!getRoomLocation().getZone().equals(loczone))
		{
			objects.add(getRoomLocation().getZone());
		}
		if (loczone!=null)
		{
			objects.add(loczone);
		}
		if ((!getRoomLocation().getZone().equals(zone))&&(!zone.equals(loczone)))
		{
			objects.add(zone);
		}
		objects.add(zone.getWorld());
	}

	public void delete()
	{
		if ((this.asRoom()==null)&&(location!=null))
		{
			location.removeItem(this);
		}
		zone.removeItem(this);
		zone.getWorld().updateReferences(this,null);
	}
	
	public void reset()
	{
		if (checkFlag("clone"))
		{
			delete();
		}
		else
		{
			state=startstate;
			if (this.asRoom()==null)
			{
				if (startlocation==null)
				{
					setLocation(zone.findItem("permanent.bin").asContainer());
				}
				else
				{
					setLocation(startlocation);
				}
			}
			super.reset();
		}
	}

	public String getWorldIdentifier()
	{
		return zone.getIdentifier()+"."+getIdentifier();
	}

	public boolean hasName(String name)
	{
		boolean result = false;
		for (int loop=0; loop<names.size(); loop++)
		{
			result=result||name.toLowerCase().equals(PlayerIO.stripColour(((StringBuffer)names.get(loop)).toString()).toLowerCase());
		}
		return result;
	}

	public short getSize()
	{
		return size;
	}

	public short getArmour()
	{
		return armour;
	}

	public void setArmour(short value)
	{
		armour=value;
	}

	public int getBonusValue()
	{
		return bonusvalue;
	}

	public short getWeight()
	{
		return weight;
	}

	public Zone getZone()
	{
		return zone;
	}

	public void setZone(Zone newzone)
	{
		if (zone!=null)
		{
			zone.removeItem(this);
		}
		zone=newzone;
		if (zone!=null)
		{
			zone.addItem(this);
		}
	}

	public TaweServer getServer()
	{
		return getWorld().getServer();
	}

	public World getWorld()
	{
		return zone.getWorld();
	}

	public short getVisibility()
	{
		return visibility;
	}

	public void setVisibility(short newvis)
	{
		visibility=newvis;
	}

	public Container getStartLocation()
	{
		return startlocation;
	}

	public Collection getWearPlaces()
	{
		return wearplaces;
	}

	public List getNames()
	{
		return names;
	}

	public String getName()
	{
		if (names.size()>=1)
		{
			return ((StringBuffer)names.get(0)).toString();
		}
		else
		{
			return "";
		}
	}

	public void addName(StringBuffer name)
	{
		delName(name);
		names.add(name);
	}

	public void delName(StringBuffer name)
	{
		for (int loop=names.size()-1; loop>=0; loop--)
		{
			if (name.toString().toLowerCase().equals(names.get(loop).toString().toLowerCase()))
			{
				names.remove(loop);
			}
		}
	}

	public void setNames(String newnames)
	{
		names.clear();
		StringTokenizer tokens = new StringTokenizer(newnames,",");
		while (tokens.hasMoreTokens())
		{
			names.add(tokens.nextToken());
		}
	}

	public void setName(String name)
	{
		StringBuffer newname = new StringBuffer(name);
		delName(newname);
		names.add(0,newname);
	}

	public Container getLocation()
	{
		return location;
	}

	public void setSize(short newsize)
	{
		size=newsize;
	}

	public void setBonusValue(int value)
	{
		bonusvalue=value;
	}

	public int getBasicValue()
	{
		return (armour*10)+10;
	}

	public int getValue()
	{
		return getBasicValue()+getBonusValue();
	}

	public void setWearPlaces(String flags)
	{
		wearplaces.clear();
		StringTokenizer tokens = new StringTokenizer(flags,",");
		while (tokens.hasMoreTokens())
		{
			wearplaces.add(tokens.nextToken());
		}
	}

	public void setWearPlace(String place)
	{
		wearplaces.add(place);
	}

	public void setWeight(short newweight)
	{
		weight=newweight;
	}

	public void setStartLocation(Container location)
	{
		startlocation=location;
	}

	public void setIdentifier(String newid)
	{
		if (newid.indexOf(".")==-1)
		{
			if (zone!=null)
			{
				zone.removeItem(this);
			}
			super.setIdentifier(newid);
			if (zone!=null)
			{
				zone.addItem(this);
			}
		}
		else
		{
			throw new IllegalArgumentException("Identifiers should not contain a period.");
		}
	}

	public Item getClonedFrom()
	{
		return clonedfrom;
	}

	public void setClonedFrom(Item item)
	{
		clonedfrom=item;
	}

	public void setLocation(Container place)
	{
		if (location!=null)
		{
			location.removeItem(this);
		}
		location=place;
		if (location!=null)
		{
			location.addItem(this);
		}
	}

	public Room getRoomLocation()
	{
		if (location!=null)
		{
			return location.getRoomLocation();
		}
		else
		{
			return null;
		}
	}

	public String getState()
	{
		return state;
	}

	public Iterator getStateIterator()
	{
		return statedescs.keySet().iterator();
	}

	public String getStartState()
	{
		return startstate;
	}

	public void setState(String newstate)
	{
		String oldstate=state;
		state=newstate;
		if (!inInit())
		{
			zone.getWorld().getServer().parseSpecial(this,Special.ST_STATE,oldstate+" "+state);
		}
	}

	public void setStartState(String newstate)
	{
		startstate=newstate;
	}

	public void addState(String state, StringBuffer description)
	{
		statedescs.put(state,description);
	}

	public void removeState(String state)
	{
		statedescs.remove(state);
	}

	public String getLocalDescription()
	{
		String ld=getLocalDescription(state);
		Variables vars = new Variables();
		vars.setVariable("%n",getName());
		return vars.parseString(ld);
	}

	public String getLocalDescription(String thestate)
	{
		String ld;
		if (statedescs.containsKey(thestate))
		{
			ld=((StringBuffer)statedescs.get(thestate)).toString();
		}
		else
		{
			ld="A %n is lying here";
		}
		return ld;
	}

	public void setLocalDescription(String desc)
	{
		addState(state,new StringBuffer(desc));
	}

	public void displayText(int vis, String text)
	{
		displayText("",vis,text);
	}

	public void displayText(String noshow, int vis, String text)
	{
	}

	public Item asItem()
	{
		return this;
	}

	public MailHandler getMailHandler()
	{
		return null;
	}

	public void place()
	{
		super.place();
		init=false;
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (startlocation==oldref)
		{
			if (newref!=null)
			{
				startlocation=newref.asContainer();
			}
			else
			{
				startlocation=zone.findItem("permanent.bin").asContainer();
			}
		}
		if (location==oldref)
		{
			if (newref!=null)
			{
				setLocation(newref.asContainer());
			}
			else
			{
				setLocation(zone.findItem("permanent.bin").asContainer());
			}
		}
		if (clonedfrom==oldref)
		{
			if (newref!=null)
			{
				clonedfrom=newref.asItem();
			}
			else
			{
				clonedfrom=this;
			}
		}
		super.updateReferences(oldref,newref);
	}
	
	public void cycle()
	{
		super.cycle();
	}
}
