package com.esp.tawemud.items;

import java.util.Vector;
import java.io.PrintWriter;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Weapon extends Item
{
	private int damage;
	private int hands;
	private long nexthit;

	public Weapon(TaweServer server)
	{
		super(server);
		damage=0;
		hands=1;
		nexthit=System.currentTimeMillis();
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		if (damage!=0)
		{
			node.setAttribute("damage",Integer.toString(damage));
		}
		if (hands!=1)
		{
			node.setAttribute("hands",Integer.toString(hands));
		}
	}

	public boolean hasName(String name)
	{
		if (name.toLowerCase().equals("weapon"))
		{
			return true;
		}
		else
		{
			return super.hasName(name);
		}
	}

	public int getBasicValue()
	{
		return ((damage/hands)*10)+10;
	}

	public long getNextHit()
	{
		return nexthit;
	}

	public void setNextHit(long val)
	{
		nexthit=val;
	}

	public int getDamage()
	{
		return damage;
	}

	public void setDamage(int newval)
	{
		damage=newval;
	}

	public int getHands()
	{
		return hands;
	}

	public void setHands(int val)
	{
		hands=val;
	}

	public Weapon asWeapon()
	{
		return this;
	}
}
