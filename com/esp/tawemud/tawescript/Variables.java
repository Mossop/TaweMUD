package com.esp.tawemud.tawescript;

import java.util.HashMap;
import java.util.Iterator;
import com.esp.tawemud.World;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

public class Variables
{
	private HashMap variables;

	public Variables()
	{
		variables = new HashMap(10);
	}

	public Variables(Variables global)
	{
		this();
		variables = new HashMap(global.getVariables());
	}

	public HashMap getVariables()
	{
		return variables;
	}

	public void setVariable(String name, String value)
	{
		if (name.length()>0)
		{
			variables.put(name,value);
		}
	}

	public void setVariable(String name, CodeableObject value)
	{
		if ((value!=null)&&(name.length()>0))
		{
			variables.put(name,value);
		}
	}
	
	public CodeableObject getObject(String name, TaweServer server)
	{
		return getObject(name,server.getWorld());
	}
	
	public CodeableObject getObject(String name, World world)
	{
		CodeableObject obj;
		try
		{
			obj = (CodeableObject)variables.get(name);
			if (obj==null)
			{
				obj=world.findCodeableObject(parseString(name));
			}
			return obj;
		}
		catch (Exception e)
		{
			obj=world.findCodeableObject(parseString(name));
			if (obj!=null)
			{
				variables.put(name,obj);
			}
			return obj;
		}
	}
	
	public String toString()
	{
		return variables.toString();
	}

	public String parseString(String string)
	{
		if (string==null)
		{
			return "";
		}
		else
		{
			if (variables.get(string)!=null)
			{
				return variables.get(string).toString();
			}
			else
			{
				StringBuffer text = new StringBuffer(string);
				Iterator loop = variables.keySet().iterator();
				while (loop.hasNext())
				{
					String varname=loop.next().toString();
					String value;
					if (variables.get(varname)!=null)
					{
						value=variables.get(varname).toString();
					}
					else
					{
						value="";
					}
					if (!varname.equals(value))
					{
						int index=0;
						while (text.toString().indexOf(varname,index)>=0)
						{
							index=text.toString().indexOf(varname,index);
							text.delete(index,index+varname.length());
							text.insert(index,value);
							index=index+value.length();
						}
					}
				}
				return text.toString();
			}
		}
	}
}
