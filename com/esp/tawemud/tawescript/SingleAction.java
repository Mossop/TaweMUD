package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.esp.tawemud.items.Mobile;
import java.lang.reflect.Field;

public abstract class SingleAction extends Action
{
	public abstract boolean doAction(TaweServer server, Variables variables);

	public int getType()
	{
		return Action.AT_SINGLEACTION;
	}
	
	public boolean parseSubElement(Element node, String text)
	{
		return false;
	}

	public boolean run(TaweServer server, Variables variables)
	{
		Mobile tracer=server.getTracer();
		if (tracer!=null)
		{
			StringBuffer attribs = new StringBuffer();
			Field[] attrs = getClass().getDeclaredFields();
			for (int field = 0; field<attrs.length; field++)
			{
				if (attrs[field].getType().getName().equals("java.lang.String"))
				{
					try
					{
						String test = (String)attrs[field].get(this);
						if (attribs.length()>0)
						{
							attribs.append(",");
						}
						attribs.append(attrs[field].getName());
						attribs.append("=\"");
						if (test!=null)
						{
							attribs.append(variables.parseString(test));
						}
						attribs.append("\"");
					}
					catch (Exception e)
					{
					}
				}
			}
			tracer.displayText("Running "+getName()+" ("+attribs+")");
		}
		return doAction(server,variables);
	}
}
