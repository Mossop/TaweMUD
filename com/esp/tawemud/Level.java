package com.esp.tawemud;

import com.esp.tawemud.items.Mobile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Stores level information.
 *
 * Each level has a set number of experience points required as well as a number of quests.
 * Also, there are different names for the level depending on what guild you are in.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Level
{
	/**
	 * Experience required to be at this level.
	 */
	private int experience;
	/**
	 * The number of quest points required for this level.
	 */
	private short qpoints;
	/**
	 * Quests required to have been completed by each guild.
	 */
	private HashMap guildquests;
	/**
	 * Level names for each guild.
	 */
	private HashMap guildnames;
	/**
	 * The level number.
	 */
	private short level;

	/**
	 * Creates a new level.
	 */
	public Level()
	{
		level=0;
		experience=-1;
		qpoints=-1;
		guildquests = new HashMap();
		guildnames = new HashMap();
	}

	public int getExperience()
	{
		return experience;
	}

	public void setExperience(int value)
	{
		experience=value;
	}

	public short getLevel()
	{
		return level;
	}

	public void setLevel(short value)
	{
		level=value;
	}

	public short getQPoints()
	{
		return qpoints;
	}

	public void setQPoints(short value)
	{
		qpoints=value;
	}

	/**
	 * Returns the name of this level for the given guild.
	 *
	 * @param guild The guild
	 * @return  The name of the level
	 */
	public String getName(String guild)
	{
		return (String)guildnames.get(guild.toLowerCase());
	}

	/**
	 * Sets the name of this level for the given guild.
	 *
	 * @param guild The guild
	 * @param name  The level name
	 */
	public void setName(String guild, String name)
	{
		guildnames.put(guild.toLowerCase(),name);
	}

	/**
	 * Returns a list of quests necessary for this level for the given guild.
	 *
	 * @param guild The guild
	 * @return  A list of quests
	 */
	public List getGuildQuests(String guild)
	{
		return (List)guildquests.get(guild.toLowerCase());
	}

	/**
	 * Sets the quests necesarry for this level for the given guild.
	 *
	 * @param guild The guild
	 * @param quests  A list of quests
	 */
	public void setGuildQuests(String guild, List quests)
	{
		guildquests.put(guild.toLowerCase(),quests);
	}

	/**
	 * Tests if a mobile has attained this level.
	 *
	 * @param mobile  The mobile
	 * @return  True if the mobile is this level or above, false otherwise
	 */
	public boolean isLevel(Mobile mobile)
	{
		boolean good=true;
		List gq = getGuildQuests(mobile.getGuild());
		if (gq!=null)
		{
			Iterator loop = gq.iterator();
			while ((loop.hasNext())&&(good))
			{
				String quest = loop.next().toString();
				if (!mobile.checkFlag(quest,"quest"))
				{
					good=false;
				}
			}
		}
		if (experience>mobile.getExperience())
		{
			good=false;
		}
		if (qpoints>mobile.getQPoints())
		{
			good=false;
		}
		return good;
	}
}
