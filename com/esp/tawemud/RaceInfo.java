package com.esp.tawemud;

import org.w3c.dom.Element;
import java.io.PrintWriter;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

public class RaceInfo
{
	private int energy;
	private int vitality;
	private int strength;
	private int dexterity;
	private int agility;
	private int maxhealth;
	private int maxmana;
	private String race;
	
	public RaceInfo()
	{
		energy=0;
		vitality=0;
		strength=0;
		dexterity=0;
		agility=0;
		maxhealth=0;
		maxmana=0;
		race="";
	}

	public String getRace()
	{
		return race;
	}
	
	public int getEnergy()
	{
		return energy;
	}
	
	public int getVitality()
	{
		return vitality;
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
	
	public int getMaxHealth()
	{
		return maxhealth;
	}
	
	public int getMaxMana()
	{
		return maxmana;
	}
	
	public void setRace(String value)
	{
		race=value;
	}
	
	public void setEnergy(int value)
	{
		energy=value;
	}

	public void setVitality(int value)
	{
		vitality=value;
	}

	public void setStrength(int value)
	{
		strength=value;
	}

	public void setDexterity(int value)
	{
		dexterity=value;
	}

	public void setAgility(int value)
	{
		agility=value;
	}

	public void setMaxHealth(int value)
	{
		maxhealth=value;
	}

	public void setMaxMana(int value)
	{
		maxmana=value;
	}
}
