package com.esp.tawemud;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.PrintWriter;
import java.util.HashMap;
import java.lang.reflect.Method;
import com.esp.tawemud.plugins.PluginInterface;
import com.esp.tawemud.items.Mobile;

public class Plugin
{
	private PluginInterface plugin;
	private HashMap functions;
	
	public Plugin(PluginInterface plugin)
	{
		if (plugin!=null)
		{
			this.plugin=plugin;
			functions = new HashMap();
			try
			{
				Class[] wantparams = new Class[2];
				wantparams[0]=Class.forName(TaweServer.PACKAGE+".items.Mobile");
				wantparams[1]=Class.forName("java.lang.String");
				Method[] methods = plugin.getClass().getMethods();
				for (int loop=0; loop<methods.length; loop++)
				{
					if ((methods[loop].getName().startsWith("do"))&&methods[loop].getReturnType().getName().equals("boolean"))
					{
						Class[] params = methods[loop].getParameterTypes();
						boolean result=true;
						for (int subloop=0; subloop<params.length; subloop++)
						{
							result=result&&(params[subloop].equals(wantparams[subloop]));
						}
						if (result)
						{
							String name = methods[loop].getName().substring(2).toLowerCase();
							functions.put(name,methods[loop]);
						}
					}
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			throw new NullPointerException();
		}
	}
	
	public boolean startup(TaweServer server)
	{
		return plugin.startup(server,this);
	}

	public void shutdown()
	{
		plugin.shutdown();
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("Plugin");
		node.setAttribute("class",plugin.getClass().getName());
		plugin.storeInElement(builder,node);
		return node;
	}

	public void parseElement(Element node, PrintWriter out)
	{
		plugin.parseElement(node,out);
	}

	public String getName()
	{
		return plugin.getClass().getName().substring(plugin.getClass().getName().lastIndexOf(".")+1);
	}

	public static Plugin createInstance(String classname)
	{
		try
		{
			Class thisclass = Class.forName(classname);
			if (Class.forName(TaweServer.PACKAGE+".plugins.PluginInterface").isAssignableFrom(thisclass))
			{
				Plugin newplugin = new Plugin((PluginInterface)thisclass.newInstance());
				return newplugin;
			}
			else
			{
				return null;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}

	public boolean callFunction(Mobile user, String name, String options)
	{
		Method function = (Method)functions.get(name.toLowerCase());
		if (function!=null)
		{
			Object[] params = new Object[] {user,options};
			try
			{
				return ((Boolean)function.invoke(plugin,params)).booleanValue();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}
