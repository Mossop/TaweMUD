package com.esp.tawemud;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.DOMImplementation;
import javax.xml.parsers.DocumentBuilderFactory;
import com.esp.tawemud.items.Player;

public class RebootHandler extends Thread
{
	private TaweServer oldserver;

	public RebootHandler(TaweServer server)
	{
		super("RebootHandler");
		oldserver=server;
		start();
	}

	public void run()
	{
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		try
		{
			System.out.println("Loading classes");
			TaweLoader loader = new TaweLoader(oldserver.getWorldURL()+"TaweMUD.jar");
			loader.preload(TaweServer.PACKAGE);
			ServerBase newserver = (ServerBase)loader.loadClass(TaweServer.PACKAGE+".TaweServer").newInstance();
			newserver.setWorldURL(oldserver.getWorldURL());
			System.out.println("Loading world");
			if (newserver.loadBasicWorld(out))
			{
				Zone current;
				oldserver.sendWizMessage("Halting Process","",0,2);
				System.out.println("Stopping old server");
				oldserver.shutdown();
				while (oldserver.isRunning())
				{
					oldserver.sleep(100);
				}
				try
				{
					LinkedList playerlist = new LinkedList();
					Iterator players = oldserver.getPlayers();
					Player thisplayer;
					while (players.hasNext())
					{
						thisplayer=(Player)players.next();
						if (thisplayer.checkFlag("multiline"))
						{
							playerlist.add(thisplayer);
						}
					}
					if (playerlist.size()>0)
					{
						oldserver.sendWizMessage("Waiting for Players","",0,2);
						while (playerlist.size()>0)
						{
							try
							{
								Thread.sleep(100);
							}
							catch (Exception e)
							{
							}
							if (!((Player)playerlist.get(0)).checkFlag("multiline"))
							{
								playerlist.remove(0);
							}
						}
					}
					System.out.println("Copying zones");
					Iterator zones = oldserver.getWorld().getZoneIterator();
					while (zones.hasNext())
					{
						current = (Zone)zones.next();
						DOMImplementation domimp = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument().getImplementation();
						DocumentType dt = domimp.createDocumentType("Zone",null,"../dtd/zone.dtd");
						Document doc = domimp.createDocument(null,"Zone",dt);
						current.storeInElement(doc,doc.getDocumentElement());
						newserver.addZone(doc.getDocumentElement(),out);
					}
					System.out.println("Copying players");
					players = oldserver.getPlayers();
					while (players.hasNext())
					{
						thisplayer=(Player)players.next();
						thisplayer.disconnect();
						if (!newserver.connectPlayer(thisplayer.getIdentifier(),thisplayer.getClient()))
						{
							throw new Exception("Unable to connect player");
						}
					}
					System.out.println("Starting new server");
					out.flush();
					newserver.startup(buffer.toString());
				}
				catch (Throwable e)
				{
					System.out.println("Restarting old server");
					try
					{
						oldserver.startup("");
					}
					catch (Throwable err)
					{
						System.out.println("Error restarting server");
						err.printStackTrace();
					}
					throw e;
				}
			}
			else
			{
				throw new Exception("Failed to load the new world");
			}
		}
		catch (Throwable e)
		{
			oldserver.sendWizMessage("Background reboot failed - "+e.getMessage(),"",0,3);
			out.flush();
			oldserver.setMudLog(PlayerIO.convertText(buffer.toString()));
			oldserver.logCrash(e);
			System.out.println("Reboot failed");
			e.printStackTrace();
		}
	}
}
