package com.esp.tawemud.tawescript;

import java.util.Vector;
import java.io.PrintWriter;
import java.io.StringWriter;
import com.esp.tawemud.TaweServer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import java.lang.reflect.Constructor;
import com.esp.tawemud.items.Mobile;
import java.lang.reflect.Field;

public class GroupAction extends Action
{
	private Vector actions;

	public GroupAction()
	{
		super();
		actions = new Vector(10);
	}

	public Vector getActions()
	{
		return actions;
	}

	public void clear()
	{
		actions = new Vector(10);
	}

	public boolean parseSubElement(Element node, String text)
	{
		try
		{
			Class scriptclass = Class.forName(TaweServer.PACKAGE+".tawescript."+node.getTagName());
			Class actionclass = Class.forName(TaweServer.PACKAGE+".tawescript.Action");
			if (actionclass.isAssignableFrom(scriptclass))
			{
				Action action = (Action)scriptclass.newInstance();
				action.parseElement(node);
				Field[] attrs = scriptclass.getDeclaredFields();
				for (int field = 0; field<attrs.length; field++)
				{
					if (attrs[field].getType().getName().equals("java.lang.String"))
					{
						Object test = attrs[field].get(action);
						if (test==null)
						{
							attrs[field].set(action,"");
						}
					}
				}
				addAction(action);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			return false;
		}
	}

	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		for (int loop=0; loop<actions.size(); loop++)
		{
			node.appendChild(((Action)actions.elementAt(loop)).getElement(builder));
		}
		return node;
	}

	public void addAction(Action action)
	{
		actions.addElement(action);
		action.setParent(this);
	}

	public int getType()
	{
		return Action.AT_GROUPACTION;
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
		boolean answer = false;
		for (int loop=0; loop<actions.size(); loop++)
		{
			answer=((Action)actions.elementAt(loop)).run(server,variables)||answer;
		}
		return answer;
	}
}
