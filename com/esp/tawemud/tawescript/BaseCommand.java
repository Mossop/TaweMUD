package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Mobile;
import java.util.StringTokenizer;

public abstract class BaseCommand implements Comparable
{
	private int priority;
	private String name;
	
	public BaseCommand(int priority)
	{
		this.priority=priority;
		name="";
	}
	
	public int compareTo(Object o)
	{
		if (o instanceof BaseCommand)
		{
			BaseCommand target = (BaseCommand)o;
			if (getPriority()==target.getPriority())
			{
				return getName().compareToIgnoreCase(target.getName());
			}
			else
			{
				return getPriority()-target.getPriority();
			}
		}
		else
		{
			throw new ClassCastException("Object given is not a BaseCommand");
		}
	}
	
	public void setName(String newname)
	{
		name=newname;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getPriority()
	{
		return priority;
	}
	
	public void setPriority(int newpriority)
	{
		priority=newpriority;
	}
	
	public abstract String getHelp(Mobile mobile);

	public abstract void parseElement(Element node);

	public abstract Element getElement(Document builder);

	public abstract boolean callCommand(TaweServer server, Mobile caller, String found, String args);
}
