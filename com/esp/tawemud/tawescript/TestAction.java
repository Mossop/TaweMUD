package com.esp.tawemud.tawescript;

import java.io.PrintWriter;
import java.io.StringWriter;
import com.esp.tawemud.TaweServer;
import java.util.Vector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.esp.tawemud.items.Mobile;
import java.lang.reflect.Field;

public abstract class TestAction extends Action
{
	private GroupAction passactions;
	private GroupAction failactions;

	public TestAction()
	{
		super();
		passactions = new GroupAction();
		passactions.setName("OnPass");
		failactions = new GroupAction();
		failactions.setName("OnFail");
		passactions.setParent(this);
		failactions.setParent(this);
	}

	public boolean parseSubElement(Element node, String text)
	{
		if (node.getTagName().equals("OnPass"))
		{
			passactions.parseElement(node);
			return true;
		}
		if (node.getTagName().equals("OnFail"))
		{
			failactions.parseElement(node);
			return true;
		}
		return false;
	}

	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		if (passactions.getActions().size()>0)
		{
			node.appendChild(passactions.getElement(builder));
		}
		if (failactions.getActions().size()>0)
		{
			node.appendChild(failactions.getElement(builder));
		}
		return node;
	}

	public GroupAction getPassAction()
	{
		return passactions;
	}

	public GroupAction getFailAction()
	{
		return failactions;
	}

	public void setPassAction(GroupAction newactions)
	{
		passactions=newactions;
	}

	public void setFailAction(GroupAction newactions)
	{
		failactions=newactions;
	}

	public abstract boolean doTest(TaweServer server, Variables variables);

	public int getType()
	{
		return Action.AT_TESTACTION;
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
		if (doTest(server,variables))
		{
			return passactions.run(server,variables);
		}
		else
		{
			return failactions.run(server,variables);
		}
	}
}
