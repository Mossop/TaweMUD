package com.esp.tawemud.specialized;

import com.esp.tawemud.tawescript.Command;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.items.Player;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Zone;
import java.util.Iterator;
import java.util.ArrayList;

public class PuntCommand extends Command
{
	public PuntCommand(CodeableObject owner)
	{
		super(owner);
	}

	public boolean run(TaweServer server, Variables vars)
	{
		CodeableObject caller = vars.getObject("$0",server);
		if ((caller.asPlayer()!=null)&&(caller.asPlayer().isWiz()))
		{
			CodeableObject target = vars.getObject("$2",server);
			if ((target==null)||(target.asMobile()==null)||(!caller.asMobile().canSee(target.asItem())))
			{
				target=null;
				Iterator loop = server.getPlayers();
				boolean result = false;
				String realname = vars.parseString("$2");
				while ((!result)&&(loop.hasNext()))
				{
					Player thisone = (Player)loop.next();
					if ((caller.asPlayer().canSee(thisone))&&(thisone.hasName(realname)))
					{
						result=true;
						target=thisone;
					}
				}
				if (!result)
				{
					caller.asPlayer().displayText("You must say who you want to punt.");
				}
			}
			if (target!=null)
			{
				if (target.asMobile()==null)
				{
					caller.asPlayer().displayText("You can only punt mobiles.");
				}
				else
				{
					if ((target.asPlayer()!=null)&&(target.asPlayer().isPower())&&(!caller.asPlayer().isPower()))
					{
						caller.asMobile().displayText("You cant punt them.");
					}
					else if ((target.asPlayer()!=null)&&(target.asPlayer().isDPower())&&(!caller.asPlayer().isDPower()))
					{
						caller.asMobile().displayText("You cant punt then.");
					}
					else
					{
						CodeableObject location = vars.getObject("$3",server);
						if ((location==null)||(location.asItem()==null))
						{
							ArrayList rooms = new ArrayList();
							Iterator zoneloop = server.getWorld().getZoneIterator();
							Iterator roomloop;
							while (zoneloop.hasNext())
							{
								Zone thisone = (Zone)zoneloop.next();
								if (target.asMobile().canEnterZone(thisone))
								{
									roomloop = thisone.getItemIterator();
									while (roomloop.hasNext())
									{
										location = (CodeableObject)roomloop.next();
										if (location.asRoom()!=null)
										{
											rooms.add(location);
										}
									}
								}
							}
							location = (CodeableObject)rooms.get(server.getRandom().nextInt(rooms.size()));
						}
						if (location.asRoom()==null)
						{
							location=location.asItem().getRoomLocation();
						}
						if (!target.asMobile().canEnterZone(location.asItem().getZone()))
						{
							caller.asPlayer().displayText("You cant punt them there.");
						}
						else
						{
							int vis = Math.max(caller.asPlayer().getVisibility(),target.asMobile().getVisibility());
							server.sendWizMessage(caller.asItem().getName()+" punted "+target.asItem().getName()+" to "+location.asRoom().getName()+" ("+location.toString()+")");
							if (target.asMobile().getGender().equals("female"))
							{
								caller.asMobile().displayText("You deliver a well aimed kick to her backside sending her flying across the realm.");
								server.sendAllMessage(vis,caller.asItem().getName()+" delivers a well aimed kick to "+target.asItem().getName()+"'s backside and sends her flying across the realm.",caller.toString()+","+target.toString(),new ArrayList(),new ArrayList());
							}
							else
							{
								caller.asMobile().displayText("You deliver a well aimed kick to his backside sending him flying across the realm.");
								server.sendAllMessage(vis,caller.asItem().getName()+" delivers a well aimed kick to "+target.asItem().getName()+"'s backside and sends him flying across the realm.",caller.toString()+","+target.toString(),new ArrayList(),new ArrayList());
							}
							target.asMobile().displayText(caller.asMobile().getName()+" delivers a well aimed kick to your backside sending you soaring high into the air.");
							location.asRoom().displayText(target.toString(),vis,target.asMobile().getName()+" falls out of the sky.");
							server.doMove(target.asItem(),location.asRoom());
						}
					}
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
}
