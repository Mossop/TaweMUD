package com.esp.tawemud.items;

import java.io.PrintWriter;
import com.esp.tawemud.TravelMessages;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;
import com.esp.tawemud.Level;
import com.esp.tawemud.Exit;
import com.esp.tawemud.RaceInfo;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.tawescript.Special;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Mobile extends Container
{
	private int health;
	private int kills;
	private int deaths;
	private int mana;
	private int skill;
	private int wimpy;
	private int eatlevel;
	private int experience;
	private int energy;
	private int strength;
	private int dexterity;
	private int agility;
	private int vitality;
	private int maxhealth;
	private int maxmana;
	private int speed;
	private long nextpunch;
	private long nextheal;
	private long nextmana;
	private int hands;
	private Mobile opponent;
	private Mobile executioner;
	private Mobile aliassource;
	protected int commandcount;
	/**
	 * A collection of skills on the object.
	 */
	private List skills;
	/**
	 * What the skills should revert to when the object is reset.
	 */
	private List startskills;
	/**
	 * A collection of spells on the object.
	 */
	private List spells;
	/**
	 * What the spells should revert to when the object is reset.
	 */
	private List startspells;
	private List questflags;
	private TravelMessages travelmessages;
	private String gender;
	private String race;
	private String guild;
	private HashMap pronouns;
	private int qpoints;
	private short level;
	private long nextmove;

	public Mobile(TaweServer server)
	{
		super(server);
		guild="adventurer";
		race="human";
		travelmessages = new TravelMessages();
		health=0;
		kills=0;
		mana=0;
		skill=0;
		wimpy=0;
		eatlevel=0;
		experience=0;
		speed=0;
		opponent=null;
		executioner=null;
		aliassource=null;
		energy=0;
		questflags = new LinkedList();
		skills = new LinkedList();
		startskills = new LinkedList();
		spells = new LinkedList();
		startspells = new LinkedList();
		strength=0;
		dexterity=0;
		agility=0;
		vitality=0;
		maxhealth=0;
		maxmana=0;
		hands=2;
		commandcount=0;
		qpoints=0;
		nextheal=System.currentTimeMillis();
		nextpunch=0;
		nextmana=nextheal;
		nextmove=nextheal;
		gender="male";
		setState("stand");
		setStartState("stand");
		addState("stand",new StringBuffer("%n is standing here."));
		addState("sat",new StringBuffer("%n is sitting here."));
		addState("dead",new StringBuffer("The corpse of %n is rotting away on the floor."));
		addState("fight",new StringBuffer("%n here fighting"));
		pronouns = new HashMap();
		level=0;
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		if ((aliassource!=null)&&(aliassource!=this))
		{
			node.setAttribute("alias",aliassource.getWorldIdentifier());
		}
		if (kills!=0)
		{
			node.setAttribute("kills",Integer.toString(kills));
		}
		if (deaths!=0)
		{
			node.setAttribute("deaths",Integer.toString(deaths));
		}
		if (health!=0)
		{
			node.setAttribute("health",Integer.toString(health));
		}
		if (mana!=0)
		{
			node.setAttribute("mana",Integer.toString(mana));
		}
		if (skill!=0)
		{
			node.setAttribute("skill",Integer.toString(skill));
		}
		if (qpoints!=0)
		{
			node.setAttribute("qpoints",Integer.toString(qpoints));
		}
		if (wimpy!=0)
		{
			node.setAttribute("wimpy",Integer.toString(eatlevel));
		}
		if (experience!=0)
		{
			node.setAttribute("experience",Integer.toString(experience));
		}
		if (speed!=0)
		{
			node.setAttribute("speed",Integer.toString(speed));
		}
		if (eatlevel!=0)
		{
			node.setAttribute("eatlevel",Integer.toString(eatlevel));
		}
		if (!gender.equals("male"))
		{
			node.setAttribute("gender",gender);
		}
		if (!guild.equals("none"))
		{
			node.setAttribute("guild",guild);
		}
		if (!race.equals("human"))
		{
			node.setAttribute("race",race);
		}
		if (hands!=2)
		{
			node.setAttribute("hands",Integer.toString(hands));
		}
		if (opponent!=null)
		{
			node.setAttribute("opponent",opponent.toString());
		}
		String flags = getFlagList(spells);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("Spells");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		flags = getFlagList(skills);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("Skills");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		flags = getFlagList(startspells);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("StartSpells");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		flags = getFlagList(startskills);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("StartSkills");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		flags = getFlagList(questflags);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("QuestFlags");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		node.appendChild(travelmessages.getElement(builder));
	}

	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (child.getTagName().equals("QuestFlags"))
		{
			setFlags(text,questflags);
			return true;
		}
		else if (child.getTagName().equals("Skills"))
		{
			setFlags(text,skills);
			return true;
		}
		else if (child.getTagName().equals("StartSkills"))
		{
			setFlags(text,startskills);
			return true;
		}
		else if (child.getTagName().equals("Spells"))
		{
			setFlags(text,spells);
			return true;
		}
		else if (child.getTagName().equals("StartSpells"))
		{
			setFlags(text,startspells);
			return true;
		}
		else if (child.getTagName().equals("TravelMessages"))
		{
			travelmessages.parseElement(child,out);
			return true;
		}
		else
		{
			return super.parseSubElement(child,text,out);
		}
	}

	public boolean canEnterZone(Zone zone)
	{
		if (zone.getIdentifier().equals("players"))
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	public void kill()
	{
		quit();
		setState("dead");
	}
	
	public void reset()
	{
		super.reset();
		spells = new LinkedList(startspells);
		skills = new LinkedList(startskills);
	}
	
	public void quit()
	{
	}
	
	public void setLocation(Container location)
	{
		if (location!=null)
		{
			super.setLocation(location.getRoomLocation());
		}
		else
		{
			super.setLocation(location);
		}
	}

	public void setName(String name)
	{
		super.setName(name);
		pronouns.put("me",getName());
	}

	public void addName(StringBuffer name)
	{
		super.addName(name);
		pronouns.put("me",getName());
	}

	public void setPronoun(String noun, String data)
	{
		pronouns.put(noun.toLowerCase(),data);
	}

	public String getPronoun(String noun)
	{
		return (String)pronouns.get(noun.toLowerCase());
	}

	public ListIterator getQuestFlagIterator()
	{
		return questflags.listIterator();
	}

	public void setLevel()
	{
		short oldlevel=level;
		level=zone.getWorld().getMobileLevel(this);
		RaceInfo guildi = zone.getWorld().getRaceInfo(guild);
		RaceInfo racei = zone.getWorld().getRaceInfo(race);
		if ((racei!=null)&&(guildi!=null))
		{
			energy=Math.max(0,(racei.getEnergy()+guildi.getEnergy())*level);
			vitality=Math.max(0,(racei.getVitality()+guildi.getVitality())*level);
			strength=Math.max(0,(racei.getStrength()+guildi.getStrength())*level);
			dexterity=Math.max(0,(racei.getDexterity()+guildi.getDexterity())*level);
			agility=Math.max(0,(racei.getAgility()+guildi.getAgility())*level);
			maxhealth=Math.max(0,(racei.getMaxHealth()+guildi.getMaxHealth())*level);
			maxmana=Math.max(0,(racei.getMaxMana()+guildi.getMaxMana())*level);
		}
		else
		{
			energy=0;
			vitality=0;
			strength=0;
			dexterity=0;
			agility=0;
			maxhealth=0;
			maxmana=0;
		}
		if ((oldlevel!=level)&&(!inInit()))
		{
			zone.getWorld().getServer().parseSpecial(this,Special.ST_LEVEL,oldlevel+" "+level);
		}
	}

	protected List getFlags(String type)
	{
		if (type.equals("quest"))
		{
			return questflags;
		}
		else if (type.equals("skill"))
		{
			return skills;
		}
		else if (type.equals("startskill"))
		{
			return startskills;
		}
		else if (type.equals("spell"))
		{
			return spells;
		}
		else if (type.equals("startspell"))
		{
			return startspells;
		}
		else
		{
			return super.getFlags(type);
		}
	}
	
	public TravelMessages getTravelMessages()
	{
		return travelmessages;
	}

	public void setTravelMessages(TravelMessages newtrav)
	{
		travelmessages=newtrav;
	}

	public Mobile getOpponent()
	{
		return opponent;
	}

	public void setOpponent(Mobile target)
	{
		opponent=target;
	}

	public int getCommandCount()
	{
		return commandcount;
	}

	public void addCommandCount()
	{
		commandcount++;
	}

	public int getQPoints()
	{
		return qpoints;
	}

	public void setQPoints(int value)
	{
		if (qpoints!=value)
		{
			qpoints=value;
			setLevel();
		}
	}

	public int getSpeed()
	{
		return speed;
	}

	public void setSpeed(int value)
	{
		speed=value;
	}

	public void deleteCommandCount()
	{
		commandcount--;
	}

	public int getHands()
	{
		return hands;
	}

	public void setHands(int val)
	{
		hands=val;
	}

	public int getKills()
	{
		return kills;
	}

	public void setKills(int val)
	{
		kills=val;
	}

	public int getDeaths()
	{
		return deaths;
	}

	public void setDeaths(int val)
	{
		deaths=val;
	}

	public int getMaxHealth()
	{
		return (maxhealth*10)+50;
	}

	public int getMaxSkill()
	{
		return (getLevel()*10)+50;
	}

	public int getMaxMana()
	{
		return (maxmana*10)+50;
	}

	public void setHealth(int level)
	{
		if ((!checkFlag("immort"))||(level>health))
		{
			if (level>getMaxHealth())
			{
				level=getMaxHealth();
			}
			health=level;
		}
	}

	public int getHealth()
	{
		return health;
	}

	public void setMana(int level)
	{
		if ((!checkFlag("immort"))||(level>mana))
		{
			if (level>getMaxMana())
			{
				level=getMaxMana();
			}
			mana=level;
		}
	}

	public int getMana()
	{
		return mana;
	}

	public boolean isPower()
	{
		return false;
	}

	public boolean isDPower()
	{
		return false;
	}

	public boolean isWiz()
	{
		return false;
	}

	public Iterator getWorn(String location)
	{
		LinkedList list = new LinkedList();
		Iterator loop = getItemContentsIterator();
		while (loop.hasNext())
		{
			Item thisone = (Item)loop.next();
			if (thisone.checkFlag("worn"))
			{
				if (thisone.getWearPlaces().contains(location))
				{
					list.add(thisone);
				}
			}
		}
		return list.iterator();
	}

	public void setSkill(int level)
	{
		if (!checkFlag("immort"))
		{
			skill=level;
		}
	}

	public int getSkill()
	{
		return skill;
	}

	public void setWimpy(int level)
	{
		wimpy=level;
	}

	public int getWimpy()
	{
		return wimpy;
	}

	public void setExperience(int value)
	{
		if (experience!=value)
		{
			experience=value;
			setLevel();
		}
	}

	public int getExperience()
	{
		return experience;
	}

	public void setEatLevel(int level)
	{
		eatlevel=level;
	}

	public int getEatLevel()
	{
		return eatlevel;
	}

	public Mobile getExecutioner()
	{
		return executioner;
	}

	public void setExecutioner(Mobile killer)
	{
		executioner=killer;
	}

	public String getGender()
	{
		return gender;
	}

	public void setGender(String newgender)
	{
		gender=newgender;
	}

	public String getRace()
	{
		return race;
	}

	public void setRace(String newrace)
	{
		if (!race.equals(newrace))
		{
			race=newrace;
			setLevel();
		}
	}

	public String getGuild()
	{
		return guild;
	}

	public void setGuild(String newguild)
	{
		if (!guild.equals(newguild))
		{
			guild=newguild;
			setLevel();
		}
	}

	public int getEnergy()
	{
		return energy;
	}

	public int getStrength()
	{
		return strength;
	}

	public int getDexterity()
	{
		return dexterity;
	}

	public int getAgility()
	{
		return agility;
	}

	public int getVitality()
	{
		return vitality;
	}

	public short getLevel()
	{
		return level;
	}

	public void setAlias(Mobile alias)
	{
		aliassource=alias;
	}

	public Mobile getAlias()
	{
		return aliassource;
	}

	public String getLevelName()
	{
		Level level = zone.getWorld().getLevel(getLevel());
		if (level!=null)
		{
			if (level.getName(guild)!=null)
			{
				return level.getName(guild);
			}
			else
			{
				return "";
			}
		}
		else
		{
			return "";
		}
	}

	public boolean canSee(Item item)
	{
		return (getLevel()>=item.getVisibility());
	}

	public boolean hasName(String name)
	{
		boolean result = false;
		for (int loop=0; loop<names.size(); loop++)
		{
			result=result||PlayerIO.stripColour(((StringBuffer)names.get(loop)).toString()).toLowerCase().startsWith(name.toLowerCase());
		}
		return result;
	}

	public void displayText(String noshow, int vis, String text)
	{
		if (noshow.indexOf(getWorldIdentifier())==-1)
		{
			if (getLevel()>=vis)
			{
				displayText(text);
			}
		}
	}

	public void displayText(String text)
	{
		if ((aliassource!=null)&&(aliassource!=this))
		{
			aliassource.displayText(text);
		}
		if (commandcount==0)
		{
			displayPrompt();
		}
	}

	public void displayPrompt()
	{
		String text = "@+W[Aliased]@*>";
		displayPrompt(text);
	}

	public void displayPrompt(String text)
	{
		if ((aliassource!=null)&&(aliassource!=this))
		{
			aliassource.displayPrompt(text);
		}
	}

	public Mobile asMobile()
	{
		return this;
	}

	private void hit(int weapondamage, String name)
	{
		double damage=((strength+100)*weapondamage)/100.0;
		damage=damage*(100-(opponent.getAgility()/10.0))/100;
		damage=damage*((server.getRandom().nextFloat()/2)+0.5);
		int rldamage = (int)Math.round(damage);
		if ((((dexterity/20)+50)>(server.getRandom().nextInt(100)))&&(rldamage>0))
		{
			opponent.setHealth(opponent.getHealth()-rldamage);
			setExperience(getExperience()+rldamage);
			if (opponent.getHealth()<0)
			{
				displayText("You have killed "+opponent.getName());
				setExperience(getExperience()+opponent.getValue());
				opponent.displayText(getName()+" killed you");
				setState("stand");
				getServer().parseCommand(opponent,"drop all");
				opponent.kill();
				kills++;
				opponent.setDeaths(opponent.getDeaths()+1);
				getLocation().displayText(getWorldIdentifier()+","+opponent.getWorldIdentifier(),Math.max(getVisibility(),opponent.getVisibility()),getName()+" killed "+opponent.getName());
			}
			else
			{
				displayText("You hit "+opponent.getName()+" with your "+name);
				if (getGender().equals("male"))
				{
					opponent.displayText(getName()+" hit you with his "+name);
					getLocation().displayText(getWorldIdentifier()+","+opponent.getWorldIdentifier(),Math.max(getVisibility(),opponent.getVisibility()),getName()+" hit "+opponent.getName()+" with his "+name);
				}
				else
				{
					opponent.displayText(getName()+" hit you with her "+name);
					getLocation().displayText(getWorldIdentifier()+","+opponent.getWorldIdentifier(),Math.max(getVisibility(),opponent.getVisibility()),getName()+" hit "+opponent.getName()+" with her "+name);
				}
			}
		}
		else
		{
			displayText("You totally missed "+opponent.getName()+"!");
			opponent.displayText(getName()+" totally missed you!");
			getLocation().displayText(getWorldIdentifier()+","+opponent.getWorldIdentifier(),Math.max(getVisibility(),opponent.getVisibility()),getName()+" completely misses "+opponent.getName());
		}
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (opponent==oldref)
		{
			opponent=(Mobile)newref;
		}
		if (executioner==oldref)
		{
			executioner=(Mobile)newref;
		}
		if (aliassource==oldref)
		{
			aliassource=(Mobile)newref;
		}
		super.updateReferences(oldref,newref);
	}
	
	public void cycle()
	{
		super.cycle();
		long ticks = System.currentTimeMillis();
		if (!getState().equals("dead"))
		{
			if (getState().equals("fight"))
			{
				if ((opponent!=null)&&(opponent.getLocation().equals(getLocation())))
				{
					opponent.setOpponent(this);
					opponent.setState("fight");
					Iterator loop = getItemContentsIterator();
					int count=0;
					while ((loop.hasNext())&&(getState().equals("fight")))
					{
						Item thisone = (Item)loop.next();
						if ((thisone.asWeapon()!=null)&&(thisone.checkFlag("wielded")))
						{
							count+=thisone.asWeapon().getHands();
							if (ticks>=thisone.asWeapon().getNextHit())
							{
								hit(thisone.asWeapon().getDamage(),thisone.getName());
								double delay = ((double)thisone.getWeight()/100);
								delay=(delay+((500.0-getDexterity())/250.0)+1.0)*1000;
								thisone.asWeapon().setNextHit((long)(ticks+delay));
							}
						}
					}
					if ((count==0)&&(getState().equals("fight")))
					{
						if (ticks>=nextpunch)
						{
							hit(5,"fist");
							double delay=(((500.0-getDexterity())/250.0)+1.0)*1000;
							nextpunch=(long)(ticks+delay);
						}
					}
				}
				else
				{
					opponent=null;
					setState("stand");
				}
			}
			else
			{
				if (ticks>=nextheal)
				{
					setHealth(health+1);
					nextheal=ticks;
				}
				if (ticks>=nextmana)
				{
					setMana(mana+1);
					nextmana=ticks+4000;
				}
				if ((speed>0)&&(ticks>=nextmove))
				{
					List dirs = new ArrayList();
					Exit thisexit;
					for (int loop=0; loop<10; loop++)
					{
						thisexit=getLocation().asRoom().getExit(loop);
						if (thisexit!=null)
						{
							Item dest = zone.findItem(thisexit.getDestination());
							if ((dest!=null)&&(!dest.checkFlag("nomobiles"))&&(!dest.checkFlag("deathroom")))
							{
								dirs.add(Room.getDirection(loop));
							}
						}
					}
					if (dirs.size()>0)
					{
						server.parseCommand(this,"go "+dirs.get(server.getRandom().nextInt(dirs.size())));
					}
					double rnd=server.getRandom().nextGaussian();
					nextmove=200-Math.round((rnd*(speed/5.0))+speed);
					nextmove*=100;
					nextmove+=ticks;
				}
			}
		}
	}
}
