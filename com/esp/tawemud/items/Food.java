package com.esp.tawemud.items;

import java.io.PrintWriter;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Food extends Item
{
	private int healthgain;
	private int managain;
	private int skillgain;

	public Food(TaweServer server)
	{
		super(server);
		healthgain=0;
		managain=0;
		skillgain=0;
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		if (healthgain!=0)
		{
			node.setAttribute("healthgain",Integer.toString(healthgain));
		}
		if (managain!=0)
		{
			node.setAttribute("managain",Integer.toString(managain));
		}
		if (skillgain!=0)
		{
			node.setAttribute("skillgain",Integer.toString(skillgain));
		}
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

	public int getBasicValue()
	{
		return ((healthgain+managain+skillgain)*10)+10;
	}

	public int getHealthGain()
	{
		return healthgain;
	}

	public void setHealthGain(int gain)
	{
		healthgain=gain;
	}

	public int getManaGain()
	{
		return managain;
	}

	public void setManaGain(int gain)
	{
		managain=gain;
	}

	public int getSkillGain()
	{
		return skillgain;
	}

	public void setSkillGain(int gain)
	{
		skillgain=gain;
	}

	public Food asFood()
	{
		return this;
	}
}
