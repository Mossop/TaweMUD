package com.esp.tawemud.plugins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import java.io.PrintWriter;
import java.util.Map;

public class MudInfo
{
	private String name;
	private int state;
	private String address;
	private int port;
	private String mudlib;
	private String mudtype;
	private int tcpport;
	private int udpport;
	private String baselib;
	private String driver;
	private String openstatus;
	private String email;
	private Map services;
	private Map otherdata;

	public MudInfo()
	{
		name="";
		address="";
		mudlib="";
		mudtype="";
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("Mud");
		node.setAttribute("name",name);
		node.setAttribute("address",address);
		node.setAttribute("mudtype",mudtype);
		node.setAttribute("mudlib",mudlib);
		node.setAttribute("port",Integer.toString(port));
		node.setAttribute("state",Integer.toString(state));
		return node;
	}

	public void parseElement(Element node, PrintWriter out)
	{
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ATTRIBUTE_NODE)
			{
				Attr thisone = (Attr)nodes.item(loop);
				if (thisone.getNodeName().equals("address"))
				{
					address=thisone.getNodeValue();
				}
				else if (thisone.getNodeName().equals("name"))
				{
					name=thisone.getNodeValue();
				}
				else if (thisone.getNodeName().equals("mudtype"))
				{
					mudtype=thisone.getNodeValue();
				}
				else if (thisone.getNodeName().equals("mudlib"))
				{
					mudlib=thisone.getNodeValue();
				}
				else if (thisone.getNodeName().equals("port"))
				{
					port=Integer.parseInt(thisone.getNodeValue());
				}
				else if (thisone.getNodeName().equals("state"))
				{
					state=Integer.parseInt(thisone.getNodeValue());
				}
			}
			else if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
			{
				Element thisone = (Element)nodes.item(loop);
				String text;
				if ((thisone.getFirstChild()!=null)&&(thisone.getFirstChild().getNodeType()==Node.TEXT_NODE))
				{
					text=thisone.getFirstChild().getNodeValue();
				}
				else
				{
					text="";
				}
			}
		}
	}

	public String getName()
	{
		return name;
	}

	public String getAddress()
	{
		return address;
	}

	public String getMudLib()
	{
		return mudlib;
	}

	public String getMudType()
	{
		return mudtype;
	}

	public int getState()
	{
		return state;
	}

	public int getPort()
	{
		return port;
	}

	public void setName(String value)
	{
		name=value;
	}

	public void setAddress(String value)
	{
		address=value;
	}

	public void setMudLib(String value)
	{
		mudlib=value;
	}

	public void setMudType(String value)
	{
		mudtype=value;
	}

	public void setState(int value)
	{
		state=value;
	}

	public void setPort(int value)
	{
		port=value;
	}

	public int getTcpPort()
	{
		return tcpport;
	}

	public int getUdpPort()
	{
		return udpport;
	}

	public String getBaseLib()
	{
		return baselib;
	}

	public String getDriver()
	{
		return driver;
	}

	public String getOpenStatus()
	{
		return openstatus;
	}

	public String getEmail()
	{
		return email;
	}

	public Map getServices()
	{
		return services;
	}

	public Map getOtherData()
	{
		return otherdata;
	}

	public void setTcpPort(int value)
	{
		tcpport=value;
	}

	public void setUdpPort(int value)
	{
		udpport=value;
	}

	public void setBaseLib(String value)
	{
		baselib=value;
	}

	public void setDriver(String value)
	{
		driver=value;
	}

	public void setOpenStatus(String value)
	{
		openstatus=value;
	}

	public void setEmail(String value)
	{
		email=value;
	}

	public void setServices(Map value)
	{
		services=value;
	}

	public void setOtherData(Map value)
	{
		otherdata=value;
	}
}
