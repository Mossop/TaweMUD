package com.esp.tawemud.items;

import java.io.PrintWriter;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.Exit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Door extends Item
{
	private Door linkdoor;
	private int direction;
	private String keytype;

	public Door(TaweServer server)
	{
		super(server);
		doSetState("closed");
		doSetStartState("closed");
		linkdoor=null;
		direction=0;
		keytype="key";
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		node.setAttribute("direction",Room.getDirection(direction));
		if (linkdoor!=null)
		{
			node.setAttribute("linkdoor",linkdoor.getWorldIdentifier());
		}
		if (!keytype.equals("key"))
		{
			node.setAttribute("keytype",keytype);
		}
	}

	public String getKeyType()
	{
		return keytype;
	}

	public void setKeyType(String type)
	{
		keytype=type;
	}

	public Door getLinkDoor()
	{
		return linkdoor;
	}

	public void setLinkDoor(Door link)
	{
		linkdoor=link;
	}

	public String getDirection()
	{
		return Room.getDirection(direction);
	}

	public void setDirection(String dir)
	{
		direction=Room.getDirection(dir);
	}

	public void doSetStartState(String newstate)
	{
		super.setStartState(newstate);
	}

	public void setStartState(String newstate)
	{
		if (linkdoor!=null)
		{
			linkdoor.doSetStartState(newstate);
		}
		doSetStartState(newstate);
	}

	public void doSetState(String newstate)
	{
		super.setState(newstate);
	}

	public void setState(String newstate)
	{
		if (linkdoor!=null)
		{
			linkdoor.doSetState(newstate);
		}
		doSetState(newstate);
	}

	public void doOpen(Room dest)
	{
		Exit newexit = location.asRoom().getExit(direction);
		if (newexit==null)
		{
			newexit = new Exit(location.asRoom(),dest.getWorldIdentifier());
			location.asRoom().addExit(newexit,direction);
		}
		else
		{
			zone.getWorld().getServer().sendWizMessage("Could not create an exit for "+getWorldIdentifier()+", one already exists","",0,2);
		}
	}

	public void open()
	{
		if (linkdoor!=null)
		{
			linkdoor.doOpen(location.asRoom());
			doOpen(linkdoor.getLocation().asRoom());
		}
		else
		{
			zone.getWorld().getServer().sendWizMessage("Couldnt open "+getWorldIdentifier()+" - link does not exist.","",0,2);
		}
	}

	public void doClose()
	{
		location.asRoom().removeExit(direction);
	}

	public void close()
	{
		if (linkdoor!=null)
		{
			linkdoor.doClose();
			doClose();
		}
		else
		{
			zone.getWorld().getServer().sendWizMessage("Couldnt close "+getWorldIdentifier()+" - link does not exist.","",0,2);
		}
	}

	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
		if (linkdoor==oldref)
		{
			linkdoor=(Door)newref;
		}
		super.updateReferences(oldref,newref);
	}
	
	public Door asDoor()
	{
		return this;
	}
}
