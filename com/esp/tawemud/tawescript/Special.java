package com.esp.tawemud.tawescript;

import com.esp.tawemud.Message;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.CodeableObject;

public class Special extends GroupAction
{
	public final static int ST_UNKNOWN = 0;
	public final static int ST_AUTORUN = 1;
	public final static int ST_ENTER = 2;
	public final static int ST_EXIT = 3;
	public final static int ST_RESET = 4;
	public final static int ST_STATE = 5;
	public final static int ST_LEVEL = 6;

	public String type;
	public String identifier;
	public String nexttime;
	private boolean isdefault;
	private CodeableObject owner;
	public String version;

	public static int typeFromString(String type)
	{
		if (type.equals("autorun"))
		{
			return ST_AUTORUN;
		}
		else if (type.equals("exit"))
		{
			return ST_EXIT;
		}
		else if (type.equals("enter"))
		{
			return ST_ENTER;
		}
		else if (type.equals("reset"))
		{
			return ST_RESET;
		}
		else if (type.equals("state"))
		{
			return ST_STATE;
		}
		else if (type.equals("level"))
		{
			return ST_LEVEL;
		}
		else
		{
			return ST_UNKNOWN;
		}
	}

	public Special(CodeableObject owner)
	{
		super();
		type="";
		identifier="";
		nexttime="";
		isdefault=false;
		this.owner=owner;
		version="0.00";
	}

	public boolean isDefault()
	{
		return isdefault;
	}

	public void setDefault(boolean newval)
	{
		isdefault=newval;
	}

	public void setNextTime(long newtime)
	{
		nexttime=String.valueOf(newtime);
	}
	
	public long getNextTime()
	{
		try
		{
			if (nexttime.length()>0)
			{
				return Long.parseLong(nexttime);
			}
		}
		catch (Exception e)
		{
		}
		return -1;
	}

	public CodeableObject getOwner()
	{
		return owner;
	}

	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		node.setAttribute("identifier",identifier);
		node.setAttribute("type",type);
		node.setAttribute("version",version);
		node.setAttribute("nexttime",nexttime);
		return node;
	}

	public String getName()
	{
		return identifier;
	}

	public int getType()
	{
		return typeFromString(type);
	}

	public boolean run(TaweServer server, Variables variables)
	{
		nexttime="";
		return super.run(server,variables);
	}
}
