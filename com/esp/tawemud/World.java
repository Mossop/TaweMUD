package com.esp.tawemud;

import java.util.Vector;
import java.util.StringTokenizer;
import com.esp.tawemud.tawescript.Variables;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Hashtable;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.Constructor;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Board;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.esp.tawemud.xml.XmlLoader;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilderFactory;
import com.esp.tawemud.xml.XmlWriter;
import java.net.URL;

public class World extends CodeableObject
{
	private Map zones;
	private EmoteList emotes;
	private StringBuffer loginmessage;
	private StringBuffer connectmessage;
	private TaweServer server;
	private Hashtable infobooks;
	private Map defaultitems;
	private String name;
	private LinkedList levels;
	private HashMap races;

	public World(TaweServer thisserver)
	{
		super(thisserver);
		server=thisserver;
		defaultitems=new HashMap();
		zones = new HashMap();
		infobooks = new Hashtable();
		loginmessage = new StringBuffer();
		connectmessage = new StringBuffer();
		levels = new LinkedList();
		name="TaweMUD";
		races = new HashMap();
	}

	public int getItemCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getItemCount();
		}
		return count;
	}

	public int getDoorCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getDoorCount();
		}
		return count;
	}

	public int getWeaponCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getWeaponCount();
		}
		return count;
	}

	public int getMobileCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getMobileCount();
		}
		return count;
	}

	public int getContainerCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getContainerCount();
		}
		return count;
	}

	public int getRoomCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getRoomCount();
		}
		return count;
	}

	public int getFoodCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getFoodCount();
		}
		return count;
	}

	public int getObjectCount()
	{
		int count=0;
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			count+=((Zone)loop.next()).getObjectCount();
		}
		return count;
	}

	public void setInfoBooks(Hashtable info)
	{
		infobooks=info;
	}

	public Hashtable getInfoBooks()
	{
		return infobooks;
	}

	public void setEmotes(EmoteList emotes)
	{
		this.emotes=emotes;
	}

	public EmoteList getEmotes()
	{
		return emotes;
	}

	public TaweServer getServer()
	{
		return server;
	}

	public boolean loadFrom(String file, PrintWriter out)
	{
		out.println("Parsing "+file+"...");
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+file,out);
			if ((doc!=null)&&(doc.getDocumentElement()!=null))
			{
				parseElement(doc.getDocumentElement(),out);
				return true;
			}
			else
			{
				out.println(file+" is an empty world file");
				return false;
			}
		}
		catch (SAXException ex)
		{
			return false;
		}
		catch (Exception e)
		{
			out.println("There was an error loading the file - "+e.getMessage());
			e.printStackTrace(out);
			return false;
		}
	}

	public RaceInfo getRaceInfo(String race)
	{
		return (RaceInfo)races.get(race.toLowerCase());
	}

	public short getMobileLevel(Mobile mobile)
	{
		short level=0;
		Iterator loop = levels.iterator();
		while (loop.hasNext())
		{
			Level thislevel = (Level)loop.next();
			if ((thislevel.getLevel()>level)&&(thislevel.isLevel(mobile)))
			{
				level=thislevel.getLevel();
			}
		}
		return level;
	}

	public Level getLevel(int level)
	{
		if (level<levels.size())
		{
			return (Level)levels.get(level);
		}
		else
		{
			return null;
		}
	}

	public void loadRaces(PrintWriter out)
	{
		races.clear();
		out.println("Loading race information");
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+"races.xml",out);
			if (doc.getDocumentElement()!=null)
			{
				NodeList nodes = doc.getDocumentElement().getChildNodes();
				for (int loop=0; loop<nodes.getLength(); loop++)
				{
					if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
					{
						Element thisone = (Element)nodes.item(loop);
						if (thisone.getTagName().equals("Race"))
						{
							RaceInfo ri = new RaceInfo();
							ri.setEnergy(Integer.parseInt(thisone.getAttribute("energy")));
							ri.setDexterity(Integer.parseInt(thisone.getAttribute("dexterity")));
							ri.setAgility(Integer.parseInt(thisone.getAttribute("agility")));
							ri.setStrength(Integer.parseInt(thisone.getAttribute("strength")));
							ri.setVitality(Integer.parseInt(thisone.getAttribute("vitality")));
							ri.setMaxHealth(Integer.parseInt(thisone.getAttribute("maxhealth")));
							ri.setMaxMana(Integer.parseInt(thisone.getAttribute("maxmana")));
							ri.setRace(thisone.getAttribute("race"));
							races.put(ri.getRace().toLowerCase(),ri);
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}
	}

	public void loadLevels(PrintWriter out)
	{
		levels.clear();
		out.println("Loading level information");
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+"levels.xml",out);
			if (doc.getDocumentElement()!=null)
			{
				NodeList nodes = doc.getDocumentElement().getChildNodes();
				for (int loop=0; loop<nodes.getLength(); loop++)
				{
					if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
					{
						Element thisone = (Element)nodes.item(loop);
						if (thisone.getTagName().equals("Level"))
						{
							Level newlevel = new Level();
							newlevel.setLevel(Short.parseShort(thisone.getAttribute("level")));
							newlevel.setQPoints(Short.parseShort(thisone.getAttribute("qpoints")));
							newlevel.setExperience(Integer.parseInt(thisone.getAttribute("experience")));
							NodeList guilds = thisone.getChildNodes();
							for (int guildloop=0; guildloop<guilds.getLength(); guildloop++)
							{
								if (guilds.item(guildloop).getNodeType()==Node.ELEMENT_NODE)
								{
									Element thisguild = (Element)guilds.item(guildloop);
									String text;
									if ((thisguild.getFirstChild()!=null)&&(thisguild.getFirstChild().getNodeType()==Node.TEXT_NODE))
									{
										text=thisguild.getFirstChild().getNodeValue();
									}
									else
									{
										text="";
									}
									String guild = thisguild.getAttribute("guild");
									newlevel.setName(guild,text);
									StringTokenizer tokens = new StringTokenizer(thisguild.getAttribute("quests"),",");
									LinkedList quests = new LinkedList();
									while (tokens.hasMoreTokens())
									{
										quests.add(tokens.nextToken());
									}
									newlevel.setGuildQuests(guild,quests);
									levels.add(newlevel.getLevel(),newlevel);
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean loadEmotes(PrintWriter out)
	{
		emotes = new EmoteList(server.getWorldURL());
		return emotes.loadFrom("emotes.xml",out);
	}

	public boolean fireEmote(Mobile caller, String command, String args)
	{
		Emote emote = emotes.findEmote(command);
		boolean worked=false;
		if (emote!=null)
		{
			worked=true;
			int vis = caller.getVisibility();
			Variables variables = new Variables();
			variables.setVariable("$n",caller.getName());
			if (caller.getGender().equals("female"))
			{
				variables.setVariable("$himselfcaller","herself");
				variables.setVariable("$hecaller","she");
				variables.setVariable("$hiscaller","her");
				variables.setVariable("$himcaller","her");
				variables.setVariable("$herscaller","hers");
			}
			else
			{
				variables.setVariable("$himselfcaller","himself");
				variables.setVariable("$hecaller","he");
				variables.setVariable("$hiscaller","his");
				variables.setVariable("$himcaller","him");
				variables.setVariable("$herscaller","his");
			}
			StringTokenizer tokens = new StringTokenizer(args);
			if ((emote.getSingleFlag())&&(tokens.hasMoreTokens()))
			{
				String name = tokens.nextToken();
				args=args.substring(name.length());
				if (caller.getPronoun(name)!=null)
				{
					name=caller.getPronoun(name);
				}
				while (args.startsWith(" "))
				{
					args=args.substring(1);
				}
				variables.setVariable("$s",args);
				Mobile target = caller.getLocation().asRoom().findMobileByName(name);
				if ((target!=null)&&(!caller.canSee(target)))
				{
					target=null;
				}
				if ((target==null)&&(emote.getFarFlag()))
				{
					boolean found=false;
					Iterator loop = server.getPlayers();
					while ((!found)&&(loop.hasNext()))
					{
						Mobile thisone = (Mobile)loop.next();
						if (thisone.hasName(name))
						{
							if (caller.canSee(thisone))
							{
								target=thisone;
								found=true;
							}
						}
					}
				}
				if (target!=null)
				{
					if ((!emote.getViolentFlag())||(!target.checkFlag("peaceful")))
					{
						vis=Math.max(vis,target.getVisibility());
						variables.setVariable("$t",target.getName());
						if (target.getGender().equals("female"))
						{
							variables.setVariable("$himtarget","her");
							variables.setVariable("$histarget","her");
							variables.setVariable("$himselftarget","herself");
							variables.setVariable("$hetarget","she");
							variables.setVariable("$herstarget","hers");
						}
						else
						{
							variables.setVariable("$himtarget","him");
							variables.setVariable("$histarget","his");
							variables.setVariable("$himselftarget","himself");
							variables.setVariable("$hetarget","he");
							variables.setVariable("$herstarget","his");
						}
						if (emote.getWorldFlag())
						{
							Iterator loop = server.getPlayers();
							while (loop.hasNext())
							{
								Mobile thisone = (Mobile)loop.next();
								if (!((thisone.equals(caller))||(thisone.equals(target))))
								{
									thisone.displayText(vis,variables.parseString(emote.getOthers().toString()));
								}
							}
						}
						else
						{
							String noshow=caller.getWorldIdentifier()+","+target.getWorldIdentifier();
							String others = variables.parseString(emote.getOthers().toString());
							if (others.length()>0)
							{
								caller.getLocation().displayText(noshow,vis,others);
							}
						}
						String sender = variables.parseString(emote.getSender().toString());
						if (sender.length()>0)
						{
							caller.displayText(sender);
						}
						String targetmes = variables.parseString(emote.getTarget().toString());
						if (targetmes.length()>0)
						{
							target.displayText(targetmes);
						}
					}
					else
					{
						caller.displayText("They are too peaceful for that.");
					}
				}
				else
				{
					caller.displayText("Who is that meant to be to?");
				}
			}
			else if (emote.getAllFlag()||emote.getWorldFlag())
			{
				variables.setVariable("$s",args);
				String all = variables.parseString(emote.getAll().toString());
				if (all.length()>0)
				{
					if (emote.getWorldFlag())
					{
						Iterator loop = server.getPlayers();
						while (loop.hasNext())
						{
							Mobile thisone = (Mobile)loop.next();
							if (!thisone.equals(caller))
							{
								thisone.displayText(vis,all);
							}
						}
					}
					else
					{
						caller.getLocation().displayText(caller.getWorldIdentifier(),vis,all);
					}
				}
				String me = variables.parseString(emote.getMe().toString());
				if (me.length()>0)
				{
					caller.displayText(variables.parseString(me));
				}
			}
			else
			{
				if (emote.getSingleFlag())
				{
					caller.displayText("Who is that meant to be to?");
				}
			}
		}
		return worked;
	}

	public void saveZones()
	{
		try
		{
			DOMImplementation domimp = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().getImplementation();
			DocumentType dt = domimp.createDocumentType("Zones",null,"dtd/zones.dtd");
			Document doc = domimp.createDocument(null,"Zones",dt);
			Element node=doc.getDocumentElement();
			Iterator loop = zones.values().iterator();
			while (loop.hasNext())
			{
				Zone zone = (Zone)loop.next();
				Element thiszone = doc.createElement("ZoneFile");
				thiszone.setAttribute("zone",zone.getIdentifier());
				node.appendChild(thiszone);
				zone.save();
			}
			FileWriter output = new FileWriter(new File((new URL(server.getWorldURL()+"zones.xml")).getFile()));
			XmlWriter.write(doc,output);
			output.close();
		}
		catch (Exception e)
		{
			server.sendWizMessage("Exception while saving zones","",0,2);
			e.printStackTrace();
		}
	}

	public void save()
	{
		saveZones();
	}

	public void reset()
	{
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			Zone thiszone = (Zone)loop.next();
			if ((!thiszone.getIdentifier().equals("players"))&&(!thiszone.getIdentifier().equals("clone")))
			{
				thiszone.reset();
			}
		}
		super.reset();
	}

	public void addInfoBook(InfoBook infobook)
	{
		infobooks.put(infobook.getName(),infobook);
	}

	public boolean isInfoPage(String book, String page)
	{
		InfoBook infobook = (InfoBook)infobooks.get(book.toLowerCase());
		if (infobook!=null)
		{
			InfoPage infopage = infobook.getPage(page.toLowerCase());
			if (infopage!=null)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}

	public String getInfoPage(String book, String page, Mobile target)
	{
		InfoBook infobook = (InfoBook)infobooks.get(book.toLowerCase());
		if (infobook!=null)
		{
			InfoPage infopage = infobook.getPage(page.toLowerCase());
			if (infopage!=null)
			{
				return infopage.formatPage(target);
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public void addZone(Zone zone)
	{
		zones.put(zone.getIdentifier().toLowerCase(),zone);
	}

	public void removeZone(Zone zone)
	{
		zones.remove(zone.getIdentifier().toLowerCase());
	}

	public void loadInfo(PrintWriter out)
	{
		infobooks = new Hashtable();
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+"info.xml",out);
			if (doc.getDocumentElement()!=null)
			{
				NodeList nodes = doc.getDocumentElement().getChildNodes();
				for (int loop=0; loop<nodes.getLength(); loop++)
				{
					if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
					{
						Element thisone = (Element)nodes.item(loop);
						if (thisone.getTagName().equals("InfoBookFile"))
						{
							out.println("Loading "+thisone.getAttribute("file")+" info book");
							Document infodoc = XmlLoader.parse(server.getWorldURL()+thisone.getAttribute("file"),out);
							if (infodoc.getDocumentElement()!=null)
							{
								InfoBook newbook = new InfoBook();
								newbook.parseElement(infodoc.getDocumentElement(),out);
								addInfoBook(newbook);
							}
							else
							{
								out.println(thisone.getAttribute("file")+" did not contain an info book");
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void loadZones(PrintWriter out)
	{
		zones = new HashMap();
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+"zones.xml",out);
			if (doc.getDocumentElement()!=null)
			{
				NodeList nodes = doc.getDocumentElement().getChildNodes();
				for (int loop=0; loop<nodes.getLength(); loop++)
				{
					if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
					{
						Element thisone = (Element)nodes.item(loop);
						if (thisone.getTagName().equals("ZoneFile"))
						{
							loadZone(thisone.getAttribute("zone"),out);
						}
					}
				}
			}
			place();
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
		}
	}

	public Zone addNewZone(Element node, PrintWriter out)
	{
		Zone newzone = new Zone(this);
		newzone.parseElement(node,out);
		addZone(newzone);
		newzone.place();
		return newzone;
	}

	public boolean loadZone(String zonename, PrintWriter out)
	{
		boolean result = false;
		out.println("Loading "+zonename+" zone...");
		try
		{
			Document doc = XmlLoader.parse(server.getWorldURL()+"zones/"+zonename+".xml",out);
			if ((doc!=null)&&(doc.getDocumentElement()!=null))
			{
				Zone oldzone = findZone(zonename);
				Zone newzone = addNewZone(doc.getDocumentElement(),out);
				if (oldzone!=null)
				{
					Iterator loop = oldzone.getItemIterator();
					while (loop.hasNext())
					{
						Item thisone = (Item)loop.next();
						Item test = newzone.findItem(thisone.getIdentifier());
						updateReferences(thisone,test);
					}
					loop = oldzone.getMobileIterator();
					while (loop.hasNext())
					{
						Item thisone = (Item)loop.next();
						Item test = newzone.findItem(thisone.getIdentifier());
						updateReferences(thisone,test);
					}
					updateReferences(oldzone,newzone);
				}
				return true;
			}
			else
			{
				out.println(zonename+" zone is an empty file");
				return false;
			}
		}
		catch (SAXParseException err)
		{
			return false;
		}
		catch (SAXException ex)
		{
			return false;
		}
		catch (Exception e)
		{
			out.println("There was an error loading the file - "+e.getMessage());
			server.logCrash(e);
			return false;
		}
	}

	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (child.getTagName().equals("LoginMessage"))
		{
			loginmessage = new StringBuffer(text);
			return true;
		}
		else if (child.getTagName().equals("ConnectMessage"))
		{
			connectmessage = new StringBuffer(text);
			return true;
		}
		else if (child.getTagName().equals("IncludeWorld"))
		{
			loadFrom(child.getAttribute("file"),out);
			return true;
		}
		else
		{
			return super.parseSubElement(child,text,out);
		}
	}

	public void setConnectMessage(StringBuffer message)
	{
		connectmessage=message;
	}

	public void setLoginMessage(StringBuffer message)
	{
		loginmessage=message;
	}

	public void setName(String name)
	{
		if ((name!=null)&&(name.length()>0))
		{
			this.name=name;
		}
	}

	public String getName()
	{
		return name;
	}

	public String toString()
	{
		return "";
	}

	public String getConnectMessage()
	{
		return connectmessage.toString();
	}

	public String getLoginMessage()
	{
		return loginmessage.toString();
	}

	public Zone findZone(String name)
	{
		return (Zone)zones.get(name.toLowerCase());
	}

	public Iterator getEmoteIterator()
	{
		return emotes.getIterator();
	}

	public Iterator getZoneIterator()
	{
		return zones.values().iterator();
	}

	public Emote findEmote(String name)
	{
		return emotes.findEmote(name);
	}

	public Item findItem(String name)
	{
		if ((name!=null)&&(name.indexOf(".")>-1))
		{
			String zone = name.substring(0,name.indexOf("."));
			String item = name.substring(name.indexOf(".")+1);
			Zone lookzone = findZone(zone);
			if (lookzone==null)
			{
				return null;
			}
			else
			{
				return lookzone.findItem(item);
			}
		}
		else
		{
			return null;
		}
	}

	public CodeableObject findCodeableObject(String name)
	{
		if (name!=null)
		{
			if (name.length()==0)
			{
				return this;
			}
			else if (name.indexOf(".")==-1)
			{
				return findZone(name);
			}
			else
			{
				String zone = name.substring(0,name.indexOf("."));
				String item = name.substring(name.indexOf(".")+1);
				Zone lookzone = findZone(zone);
				if (lookzone==null)
				{
					return null;
				}
				else
				{
					return lookzone.findItem(item);
				}
			}
		}
		else
		{
			return null;
		}
	}

	private Item createBaseItem(String itemname)
	{
		try
		{
			Class subclass=server.getClassFromType(itemname);
			Class itemclass = Class.forName(TaweServer.PACKAGE+".items.Item");
			if (itemclass.isAssignableFrom(subclass))
			{
				Class[] paramtypes = new Class[1];
				paramtypes[0]=server.getClass();
				Constructor starter = subclass.getConstructor(paramtypes);
				Object[] params = new Object[1];
				params[0]=server;
				Item newitem = (Item)starter.newInstance(params);
				return newitem;
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			server.logCrash(e);
			return null;
		}
	}

	public void setDefaultItem(String itemname, Item item)
	{
		defaultitems.put(itemname.toLowerCase(),item);
	}

	public Item getDefaultItem(String itemname)
	{
		Item result = (Item)defaultitems.get(itemname.toLowerCase());
		if (result==null)
		{
			result=createBaseItem(itemname);
		}
		else
		{
		}
		return result;
	}

	public void place()
	{
		super.place();
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			((Zone)loop.next()).place();
		}
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		super.updateReferences(oldref,newref);
		Iterator loop = zones.values().iterator();
		while (loop.hasNext())
		{
			((Zone)loop.next()).updateReferences(oldref,newref);
		}
	}

	public World asWorld()
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
				Iterator loop = zones.values().iterator();
				while (loop.hasNext())
				{
					((Zone)loop.next()).cycle();
				}
			}
			catch (Throwable e)
			{
				server.logCrash(e);
				setFlag("Stopped");
				server.sendWizMessage("World cycle caused a crash - The world has been stopped.","",0,3,true);
			}
		}
	}
}
