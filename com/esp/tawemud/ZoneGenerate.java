package com.esp.tawemud;

import java.util.HashMap;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import com.esp.tawemud.items.Room;

/**
 * Creates a new zone from a map entered via standard input.
 *
 * Creates a zone of rooms from input from keyboard or a piped file. In the file
 * a * is a room, D is a deathroom and W is a water room. Any rooms that are adjacent
 * to each other will have exits created linking them together.
 *
 * @author  Dave Townsend
 * @version 1.5
 */
public class ZoneGenerate
{
	/**
	 * Creates the new zone.
	 *
	 * If was an argument entered on the command line then this will be the name of the zone,
	 * otherwise the zone will be called &quot;generated&quot;
	 *
	 * @param args  The command line arguments
	 */
	public static void main(String[] args) throws Exception
	{
		TaweServer server = new TaweServer();
		server.setWorldURL((new File(".")).toURL().toString());
		Zone newzone = new Zone(new World(server));
		String zonename;
		if (args.length>0)
		{
			zonename=args[0];
		}
		else
		{
			zonename="generated";
		}
		newzone.setIdentifier(zonename);
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		HashMap rows = new HashMap();
		String line=in.readLine();
		int row=0;
		int roomcount=1;
		while (line!=null)
		{
			String rooms=""+roomcount;
			while (rooms.length()<4)
			{
				rooms+=" ";
			}
			System.out.println(rooms+line);
			HashMap cols = new HashMap();
			rows.put(new Integer(row),cols);
			for (int loop=0; loop<line.length(); loop++)
			{
				if (line.charAt(loop)!=' ')
				{
					Room room = (Room)newzone.getDefaultItem("Room");
					switch (line.charAt(loop))
					{
						case 'W':	room.setFlag("water","start");
											break;
						case 'D':	room.setFlag("deathroom","start");
											break;
						case 'U':	room.setFlag("underwater","start");
											break;
					}
					room.setZone(newzone);
					room.setIdentifier(zonename+roomcount);
					cols.put(new Integer(loop),room);
					if (loop>0)
					{
						Room otherroom = (Room)cols.get(new Integer(loop-1));
						if (otherroom!=null)
						{
							Exit exit = new Exit(room,otherroom.toString());
							room.addStartExit(exit,Room.getDirection("w"));
							exit = new Exit(otherroom,room.toString());
							otherroom.addStartExit(exit,Room.getDirection("e"));
						}
					}
					if (row>0)
					{
						HashMap lastrow = (HashMap)rows.get(new Integer(row-1));
						if (lastrow!=null)
						{
							Room otherroom = (Room)lastrow.get(new Integer(loop));
							if (otherroom!=null)
							{
								Exit exit = new Exit(room,otherroom.toString());
								room.addStartExit(exit,Room.getDirection("n"));
								exit = new Exit(otherroom,room.toString());
								otherroom.addStartExit(exit,Room.getDirection("s"));
							}
						}
					}
					roomcount++;
				}
			}
			row++;
			line=in.readLine();
		}
		newzone.save();
	}
}
