package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;
import com.esp.tawemud.World;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.RebootHandler;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.FileInputStream;
import java.util.Iterator;

/**
 * Reboots the mud.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Reboot extends SingleAction
{
	/**
	 * The type of reboot. Can be CODE, EMOTES, INFO or WORLD
	 *
	 * @required
	 */
	public String type;

	public boolean doAction(TaweServer server, Variables variables)
	{
		String rltype = variables.parseString(type);
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		if (rltype.equals("CODE"))
		{
			new RebootHandler(server);
		}
		else if (rltype.equals("INFO"))
		{
			server.getWorld().loadInfo(out);
		}
		else if (rltype.equals("WORLD"))
		{
			try
			{
				World oldworld = server.getWorld();
				if (server.loadBasicWorld(out))
				{
					Iterator loop = oldworld.getZoneIterator();
					Zone thisone;
					while (loop.hasNext())
					{
						thisone=(Zone)loop.next();
						server.getWorld().addZone(thisone);
						thisone.setWorld(server.getWorld());
					}
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
		}
		server.setMudLog(PlayerIO.convertText(buffer.toString()));
		return false;
	}
}
