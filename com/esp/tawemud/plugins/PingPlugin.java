package com.esp.tawemud.plugins;

import org.w3c.dom.Element;
import org.w3c.dom.Document;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Iterator;
import com.esp.tawemud.items.Player;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Plugin;

public class PingPlugin implements Runnable, PluginInterface
{
	private TaweServer server;
	private Plugin plugin;
	private boolean halted = true;
	private Thread thread;
	
	public PingPlugin()
	{
		server=null;
		plugin=null;
		halted=false;
	}

	public boolean startup(TaweServer server, Plugin plugin)
	{
		this.server=server;
		this.plugin=plugin;
		Thread thread = new Thread(this,"PingPlugin");
		thread.start();
		return true;
	}

	public void shutdown()
	{
		setHalted(true);
		while (thread.isAlive())
		{
			try
			{
				Thread.sleep(100);
			}
			catch(Exception e)
			{
			}
		}
	}

	public void storeInElement(Document builder, Element node)
	{
	}

	public void parseElement(Element node, PrintWriter out)
	{
	}

	private synchronized void setHalted(boolean value)
	{
		halted=value;
	}

	private synchronized boolean isHalted()
	{
		return halted;
	}

	public void run()
	{
		try
		{
			String params="";
			if (!(System.getProperty("os.name").toLowerCase().indexOf("windows")>=0))
			{
				params="-c 4 ";
			}
			Player thisone;
			InetAddress host;
			BufferedReader in;
			Process ping;
			String line;
			float total;
			int pos;
			int lastpos;
			int count;
			while (!isHalted())
			{
				Iterator loop = server.getPlayers();
				while (loop.hasNext())
				{
					thisone = (Player)loop.next();
					if (thisone.isConnected())
					{
						host=thisone.getClient().getSocket().getInetAddress();
						ping = Runtime.getRuntime().exec("ping "+params+host.getHostAddress());
						in = new BufferedReader(new InputStreamReader(ping.getInputStream()));
						line = in.readLine();
						count=0;
						total=0;
						while (line!=null)
						{
							pos=line.indexOf("time")+5;
							if (pos>=5)
							{
								lastpos=pos;
								while ((lastpos<line.length())&&((Character.isDigit(line.charAt(lastpos)))||(line.charAt(lastpos)=='.')))
								{
									lastpos++;
								}
								line=line.substring(pos,lastpos);
								try
								{
									total+=Float.parseFloat(line);
									count++;
								}
								catch (Exception e)
								{
								}
							}
							line=in.readLine();
						}
						thisone.setVariable("pingtime",String.valueOf((int)(total/count)));
					}
				}
				try
				{
					Thread.sleep(300000);
				}
				catch(Exception e)
				{
				}
			}
		}
		catch (Exception e)
		{
			server.pluginMessage(plugin,"Crash during run");
			server.logCrash(e);
		}
	}
}
