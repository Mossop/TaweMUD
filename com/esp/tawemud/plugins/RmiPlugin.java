package com.esp.tawemud.plugins;

import com.esp.tawemud.Plugin;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.RebootHandler;
import com.esp.tawemud.World;
import com.esp.tawemud.Zone;
import com.esp.tawemud.items.Player;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.PlayerIO;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;
import java.util.Iterator;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

public class RmiPlugin extends UnicastRemoteObject implements PluginInterface, RmiPluginInterface
{
	private TaweServer server;
	private Plugin plugin;
	private boolean bound;

	public RmiPlugin() throws RemoteException
	{
		super();
		bound=false;
		plugin=null;
	}

	public boolean authenticate(String name, String password)
	{
		return server.authenticate(name,password);
	}

	public String getName(String name)
	{
		Item item = server.getWorld().findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.getName();
		}
		else
		{
			return "";
		}
	}

	public short getLevel(String name)
	{
		Item item = server.getWorld().findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().getLevel();
		}
		else
		{
			return 0;
		}
	}

	public boolean isWiz(String name)
	{
		Item item = server.getWorld().findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().isWiz();
		}
		else
		{
			return false;
		}
	}

	public boolean isDPower(String name)
	{
		Item item = server.getWorld().findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().isDPower();
		}
		else
		{
			return false;
		}
	}

	public boolean isPower(String name)
	{
		Item item = server.getWorld().findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().isPower();
		}
		else
		{
			return false;
		}
	}

	public void sendMessage(String message)
	{
		server.sendAllMessage(0,"\u0007@+W** SYSTEM: @*"+message);
	}

	public void rebootCode()
	{
		new RebootHandler(server);
	}

	public void rebootWorld()
	{
		server.sendWizMessage("World Reboot called by Remote User","",0,2);
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		try
		{
			World oldworld = server.getWorld();
			if (server.loadWorld(out))
			{
				Iterator loop = oldworld.getZoneIterator();
				Zone thisone;
				while (loop.hasNext())
				{
					thisone=(Zone)loop.next();
					server.getWorld().addZone(thisone);
					thisone.setWorld(server.getWorld());
				}
				server.getWorld().loadLevels(out);
				server.getWorld().setEmotes(oldworld.getEmotes());
				server.getWorld().setInfoBooks(oldworld.getInfoBooks());
				server.sendWizMessage("World Reboot was successfull","",0,2);
			}
			else
			{
				server.sendWizMessage("World Reboot totally failed","",0,2);
			}
		}
		catch (Exception e)
		{
			out.println("Exception rebooting world - "+e.getMessage()+"@/");
			e.printStackTrace(out);
		}
		server.setMudLog(PlayerIO.convertText(buffer.toString()));
	}

	public void rebootEmotes()
	{
		server.sendWizMessage("Emote Reboot called by Remote User","",0,2);
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		server.getWorld().loadEmotes(out);
		server.setMudLog(PlayerIO.convertText(buffer.toString()));
	}

	public void rebootInfo()
	{
		server.sendWizMessage("Info Reboot called by Remote User","",0,2);
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		server.getWorld().loadInfo(out);
		server.setMudLog(PlayerIO.convertText(buffer.toString()));
	}

	public void haltServer()
	{
		server.sendWizMessage("Process is being halted","",0,2);
		server.halt();
	}

	public boolean startup(TaweServer server, Plugin plugin)
	{
		this.server=server;
		this.plugin=plugin;
		try
		{
			Naming.rebind("///TaweMudRmi",this);
			bound=true;
			return true;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void shutdown()
	{
		bound=false;
		try
		{
			unexportObject(this,true);
			Naming.unbind("////TaweMudRmi");
		}
		catch (Exception e)
		{
		}
	}

	public void storeInElement(Document builder, Element node)
	{
	}

	public void parseElement(Element node, PrintWriter out)
	{
		NamedNodeMap attrs = node.getAttributes();
		for (int loop=0; loop<attrs.getLength(); loop++)
		{
			Attr thisone = (Attr)attrs.item(loop);
		}
		NodeList nodes = node.getChildNodes();
		for (int loop=0; loop<nodes.getLength(); loop++)
		{
			if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
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

	public String callFunction(String user, String name, String options)
	{
		Item mobile = server.getWorld().findItem(user);
		return "";
	}
}
