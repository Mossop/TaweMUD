package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.Exit;
import com.esp.tawemud.CodeableObject;
import java.util.LinkedList;
import java.util.Collection;

/**
 * A mapping action. Will return routes out of an area of rooms. All the rooms must have a
 * certain flag set on them.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetRoutes extends TestAction
{
	/**
	 * The starting room.
	 *
	 * @required
	 */
	public String room;
	/**
	 * The flag on all the rooms.
	 *
	 * @default maze
	 */
	public String flag = "maze";
	/**
	 * A variable to hold the routes.
	 *
	 * @required
	 */
	public String dest;

	public boolean mapRoom(TaweServer server, Room room, Collection visited, StringBuffer buffer, StringBuffer route, String flag)
	{
		if (!visited.contains(room))
		{
			visited.add(room);
			if (!room.checkFlag(flag))
			{
				buffer.append(route);
				buffer.append("will take you to @+G");
				buffer.append(room.getName());
				buffer.append("@*@/");
				return true;
			}
			else
			{
				boolean found=false;
				for (int loop=0; loop<10; loop++)
				{
					Exit thisexit = room.getExit(loop);
					if (thisexit!=null)
					{
						Item nextroom = server.getWorld().findItem(thisexit.getDestination());
						if ((nextroom!=null)&&(nextroom.asRoom()!=null))
						{
							found=mapRoom(server,nextroom.asRoom(),visited,buffer,(new StringBuffer()).append(route).append(Room.getDirection(loop)).append(" "),flag)||found;
						}
					}
				}
				return found;
			}
		}
		else
		{
			return false;
		}
	}

	public boolean doTest(TaweServer server, Variables variables)
	{
		boolean found = false;
		Collection visited = new LinkedList();
		StringBuffer buffer = new StringBuffer();
		CodeableObject obj = variables.getObject(room,server);
		if ((obj!=null)&&(obj.asRoom()!=null))
		{
			found=mapRoom(server,obj.asRoom(),visited,buffer,new StringBuffer(),variables.parseString(flag));
			variables.setVariable(dest,buffer.toString());
			return found;
		}
		else
		{
			return false;
		}
	}
}
