package com.esp.tawemud.plugins;

import java.net.Socket;
import java.net.InetAddress;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.ListIterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Vector;
import java.util.List;
import java.util.LinkedList;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.Plugin;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Player;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.tawescript.Variables;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Attr;

public class Intermud implements Runnable, PluginInterface
{
	private Socket router;
	String routername;
	String routeraddress;
	int routerport;
	private String mudname;
	private int password;
	private DataOutputStream output;
	private DataInputStream input;
	private boolean connected;
	private TaweServer server;
	private boolean halted;
	private int mudlistid;
	private int chanlistid;
	private HashMap muds;
	private boolean fullconnect;
	private Plugin plugin;

	public Intermud()
	{
		halted=false;
		fullconnect=false;
		connected=false;
		routername="*gjs";
		routeraddress="198.144.203.194";
		routerport=9000;
		mudname="TaweMUD";
		password=0;
		muds = new HashMap();
		plugin=null;
	}

	public boolean startup(TaweServer server, Plugin plugin)
	{
		this.server=server;
		this.plugin=plugin;
		mudname=server.getWorld().getName();
		(new Thread(this,"Intermud")).start();
		return true;
	}

	public void shutdown()
	{
		if (isConnected())
		{
			setHalted(true);
			while (isConnected())
			{
				try
				{
					Thread.sleep(10);
				}
				catch (Exception e)
				{
				}
			}
		}
	}

	public void storeInElement(Document builder, Element node)
	{
		node.setAttribute("routername",routername);
		node.setAttribute("routeraddress",routeraddress);
		node.setAttribute("routerport",Integer.toString(routerport));
		node.setAttribute("password",Integer.toString(password));
		//node.setAttribute("chanlistid",Integer.toString(chanlistid));
		//node.setAttribute("mudlistid",Integer.toString(mudlistid));
		//Element mudnode = builder.createElement("Muds");
		//node.appendChild(mudnode);
		//Iterator loop = muds.values().iterator();
		//while (loop.hasNext())
		//{
		//	mudnode.appendChild(((MudInfo)loop.next()).getElement(builder));
		//}
	}

	public void parseElement(Element node, PrintWriter out)
	{
		NamedNodeMap attrs = node.getAttributes();
		for (int loop=0; loop<attrs.getLength(); loop++)
		{
			Attr thisone = (Attr)attrs.item(loop);
			if (thisone.getName().equals("routeraddress"))
			{
				routeraddress=thisone.getValue();
			}
			else if (thisone.getName().equals("routername"))
			{
				routername=thisone.getValue();
			}
			else if (thisone.getName().equals("routerport"))
			{
				routerport=Integer.parseInt(thisone.getValue());
			}
			else if (thisone.getName().equals("password"))
			{
				password=Integer.parseInt(thisone.getValue());
			}
			else if (thisone.getName().equals("mudlistid"))
			{
				mudlistid=Integer.parseInt(thisone.getValue());
			}
			else if (thisone.getName().equals("chanlistid"))
			{
				chanlistid=Integer.parseInt(thisone.getValue());
			}
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
				if (thisone.getTagName().equals("Muds"))
				{
					NodeList mudnodes = thisone.getChildNodes();
				}
				for (int mudloop=0; mudloop<nodes.getLength(); mudloop++)
				{
					if (nodes.item(mudloop).getNodeType()==Node.ELEMENT_NODE)
					{
						MudInfo newmud = new MudInfo();
						newmud.parseElement((Element)nodes.item(mudloop),out);
						if (newmud.getName().length()>0)
						{
							synchronized(muds)
							{
								muds.put(newmud.getName(),newmud);
							}
						}
					}
				}
			}
		}
	}

	private String makeColour(String line)
	{
		Variables vars = new Variables();
		vars.setVariable("&+","@+");
		vars.setVariable("&-","@-");
		vars.setVariable("&=","@=");
		vars.setVariable("&*","@*");
		vars.setVariable("%^RESET%^","@*");
		vars.setVariable("%^BLACK%^","@+l");
		vars.setVariable("%^WHITE%^","@+w");
		vars.setVariable("%^BLUE%^","@+b");
		vars.setVariable("%^CYAN%^","@+c");
		vars.setVariable("%^MAGENTA%^","@+m");
		vars.setVariable("%^RED%^","@+r");
		vars.setVariable("%^GREEN%^","@+g");
		vars.setVariable("%^YELLOW%^","@+y");
		vars.setVariable("%^ORANGE%^","@+y");
		vars.setVariable("%^B_BLACK%^","@-l");
		vars.setVariable("%^B_WHITE%^","@-w");
		vars.setVariable("%^B_BLUE%^","@-b");
		vars.setVariable("%^B_CYAN%^","@-c");
		vars.setVariable("%^B_MAGENTA%^","@-m");
		vars.setVariable("%^B_RED%^","@-r");
		vars.setVariable("%^B_GREEN%^","@-g");
		vars.setVariable("%^B_YELLOW%^","@-y");
		vars.setVariable("%^B_ORANGE%^","@-y");
		return vars.parseString(line)+"@*";
	}

	private String findMud(String mudname)
	{
		String result=null;
		synchronized(muds)
		{
			Iterator loop = muds.keySet().iterator();
			boolean found=false;
			while ((!found)&&(loop.hasNext()))
			{
				String thisone=loop.next().toString();
				if ((mudname.length()>=thisone.length())&&(((MudInfo)muds.get(thisone)).getState()==-1))
				{
					if (thisone.equalsIgnoreCase(mudname.substring(0,thisone.length())))
					{
						result=thisone;
					}
				}
			}
		}
		return result;
	}

	public boolean doTell(Mobile mobile, String options)
	{
		if (options.indexOf("@")<0)
		{
			mobile.displayText("You must specify who to talk to as Player@@Mud");
		}
		else
		{
			String player=options.substring(0,options.indexOf("@"));
			options=options.substring(options.indexOf("@")+1,options.length());
			String mud=findMud(options);
			if (mud==null)
			{
				mobile.displayText("No such mud connected");
			}
			else
			{
				String message=PlayerIO.stripColour(options.substring(mud.length()));
				while (message.startsWith(" "))
				{
					message=message.substring(1);
				}
				IntermudPacket ip = new IntermudPacket("tell",mudname,mobile.getName(),mud,player);
				ip.getOptions().add(mobile.getName());
				ip.getOptions().add(message);
				sendString(ip.toString());
				mobile.displayText("@+yYou tell @+G"+player+"@*@@@+G"+mud+" : @+W'@*"+message+"@+W'");
			}
		}
		return true;
	}
	
	public boolean doReply(Mobile mobile, String options)
	{
		String mud=mobile.getVariable("intermudlastmud");
		String player=mobile.getVariable("intermudlastplayer");
		if ((mud.length()>0)&&(player.length()>0))
		{
			String message=PlayerIO.stripColour(options);
			while (message.startsWith(" "))
			{
				message=message.substring(1);
			}
			IntermudPacket ip = new IntermudPacket("tell",mudname,mobile.getName(),mud,player);
			ip.getOptions().add(mobile.getName());
			ip.getOptions().add(message);
			sendString(ip.toString());
			mobile.displayText("@+yYou tell @+G"+player+"@*@@@+G"+mud+" : @+W'@*"+message+"@+W'");
		}
		else
		{
			mobile.displayText("Noone has spoke to you across intermud recently.");
		}
		return true;
	}
	
	public boolean doIGossip(Mobile mobile, String options)
	{
		IntermudPacket ip;
		if (options.startsWith(":"))
		{
			ip = new IntermudPacket("channel-e",mudname,mobile.getName(),"0","0");
			options="$N "+options.substring(1);
		}
		else
		{
			ip = new IntermudPacket("channel-m",mudname,mobile.getName(),"0","0");
		}
		List optionlist = ip.getOptions();
		optionlist.add("imud_gossip");
		optionlist.add(mobile.getName());
		optionlist.add(PlayerIO.stripColour(options));
		sendString(ip.toString());
		return true;
	}
	
	public boolean doICode(Mobile mobile, String options)
	{
		IntermudPacket ip;
		if (options.startsWith(":"))
		{
			ip = new IntermudPacket("channel-e",mudname,mobile.getName(),"0","0");
			options="$N "+options.substring(1);
		}
		else
		{
			ip = new IntermudPacket("channel-m",mudname,mobile.getName(),"0","0");
		}
		List optionlist = ip.getOptions();
		optionlist.add("imud_code");
		optionlist.add(mobile.getName());
		optionlist.add(PlayerIO.stripColour(options));
		sendString(ip.toString());
		return true;
	}
	
	public boolean doEmoteTo(Mobile mobile, String options)
	{
		if (options.indexOf("@")<0)
		{
			mobile.displayText("You must specify who to emote to as Player@@Mud");
		}
		else
		{
			String player=options.substring(0,options.indexOf("@"));
			options=options.substring(options.indexOf("@")+1,options.length());
			String mud=findMud(options);
			if (mud==null)
			{
				mobile.displayText("No such mud connected");
			}
			else
			{
				String message=PlayerIO.stripColour(options.substring(mud.length()));
				while (message.startsWith(" "))
				{
					message=message.substring(1);
				}
				IntermudPacket ip = new IntermudPacket("emoteto",mudname,mobile.getName(),mud,player);
				ip.getOptions().add(mobile.getName());
				ip.getOptions().add("$N "+message);
				sendString(ip.toString());
				mobile.displayText("Emote sent to "+player+" on "+mud);
			}
		}
		return true;
	}
	
	public boolean doFinger(Mobile mobile, String options)
	{
		if (options.indexOf("@")<0)
		{
			mobile.displayText("You must specify who to finger as Player@@Mud");
		}
		else
		{
			String player=options.substring(0,options.indexOf("@"));
			options=options.substring(options.indexOf("@")+1,options.length());
			String mud=findMud(options);
			if (mud==null)
			{
				mobile.displayText("No such mud connected");
			}
			else
			{
				IntermudPacket ip = new IntermudPacket("finger-req",mudname,mobile.getName(),mud,"0");
				ip.getOptions().add(player);
				sendString(ip.toString());
				mobile.displayText("Fingering "+player+" on "+mud);
			}
		}
		return true;
	}
	
	public boolean doLocate(Mobile mobile, String options)
	{
		IntermudPacket ip = new IntermudPacket("locate-req",mudname,mobile.getName(),"0","0");
		ip.getOptions().add(options);
		sendString(ip.toString());
		mobile.displayText("Trying to locate "+options+" over intermud.");
		return true;
	}
	
	public boolean doWho(Mobile mobile, String options)
	{
		String rlname=findMud(options);
		if (rlname!=null)
		{
			IntermudPacket ip = new IntermudPacket("who-req",mudname,mobile.getName(),rlname,"0");
			sendString(ip.toString());
			mobile.displayText("Fetching who listing for "+rlname);
		}
		else
		{
			mobile.displayText("No such mud connected");
		}
		return true;
	}
	
	public boolean doInfo(Mobile mobile, String options)
	{
		String rlname=findMud(options);
		if (rlname!=null)
		{
			MudInfo mudinfo=(MudInfo)muds.get(rlname);
			mobile.displayText("@+YMud@*         : "+rlname+"@/");
			mobile.displayText("@+YAddress@*     : "+mudinfo.getAddress());
			mobile.displayText("@+YPort@*        : "+mudinfo.getPort());
			mobile.displayText("@+YOOB Port@*    : "+mudinfo.getTcpPort());
			mobile.displayText("@+YMudlib@*      : "+mudinfo.getMudLib());
			mobile.displayText("@+YBase Mudlib@* : "+mudinfo.getBaseLib());
			mobile.displayText("@+YDriver@*      : "+mudinfo.getDriver());
			mobile.displayText("@+YMud Type@*    : "+mudinfo.getMudType());
			mobile.displayText("@+YStatus@*      : "+mudinfo.getOpenStatus());
			mobile.displayText("@+YAdmin Email@* : "+mudinfo.getEmail());
			String services="";
			Iterator loop = mudinfo.getServices().keySet().iterator();
			while (loop.hasNext())
			{
				String thisservice=loop.next().toString();
				if (mudinfo.getServices().get(thisservice).equals("1"))
				{
					services+=thisservice+",";
				}
			}
			if (services.endsWith(","))
			{
				services=services.substring(0,services.length()-1);
			}
			mobile.displayText("@+YServices@*    : "+services);
		}
		else
		{
			mobile.displayText("No such mud connected");
		}
		return true;
	}
	
	public boolean doMudList(Mobile mobile, String options)
	{
		synchronized(muds)
		{
			LinkedList mudorder = new LinkedList();
			Iterator loop = muds.keySet().iterator();
			while (loop.hasNext())
			{
				String thisname=loop.next().toString();
				int count=0;
				while ((count<mudorder.size())&&(mudorder.get(count).toString().compareTo(thisname)<0))
				{
					count++;
				}
				mudorder.add(count,thisname);
			}
			mobile.displayText("Mud                                    MudLib       Type  IP Address       Port");
			mobile.displayText("-------------------------------------------------------------------------------");
			loop = mudorder.iterator();
			while (loop.hasNext())
			{
				MudInfo thisone = (MudInfo)muds.get(loop.next());
				if (thisone.getState()==-1)
				{
					String thisname=thisone.getName();
					while(thisname.length()<24)
					{
						thisname+=" ";
					}
					if (thisname.length()>24)
					{
						thisname=thisname.substring(0,24);
					}
					String mudtype=thisone.getMudType();
					while(mudtype.length()<10)
					{
						mudtype=" "+mudtype;
					}
					if (mudtype.length()>10)
					{
						mudtype=mudtype.substring(0,10);
					}
					String mudlib=thisone.getMudLib();
					while(mudlib.length()<20)
					{
						mudlib=" "+mudlib;
					}
					if (mudlib.length()>20)
					{
						mudlib=mudlib.substring(0,20);
					}
					String ip=thisone.getAddress();
					while(ip.length()<15)
					{
						ip+=" ";
					}
					String port=Integer.toString(thisone.getPort());
					while(port.length()<6)
					{
						port=" "+port;
					}
					mobile.displayText(thisname+" "+mudlib+" "+mudtype+"  "+ip+port);
				}
			}
		}
		return true;
	}
	
	private String stripQuotes(String text)
	{
		String result=text;
		if (result.startsWith("\""))
		{
			result=result.substring(1);
		}
		if (result.endsWith("\""))
		{
			result=result.substring(0,result.length()-1);
		}
		return result;
	}

	private void sendString(String array)
	{
		try
		{
			//System.out.println(array);
			output.writeInt(array.length());
			output.writeBytes(array);
			output.flush();
		}
		catch (Exception e)
		{
		}
	}

	private void connect()
	{
		try
		{
			router = new Socket(routeraddress,routerport);
			output = new DataOutputStream(router.getOutputStream());
			input = new DataInputStream(router.getInputStream());
			IntermudPacket packet = new IntermudPacket("startup-req-3",mudname,"0",routername,"0");
			List options=packet.getOptions();
			options.add(new Integer(password));
			options.add(new Integer(mudlistid));
			options.add(new Integer(chanlistid));
			options.add(new Integer(0));
			options.add(new Integer(0));
			options.add(new Integer(0));
			options.add(TaweServer.getVersion());
			options.add(TaweServer.getBaseVersion());
			options.add("TaweMUD Intermud plugin 1.05");
			options.add("TaweMUD");
			options.add("beta testing");
			options.add("mossop@bigfoot.com");
			Map services = new HashMap();
			services.put("who",new Integer(1));
			services.put("tell",new Integer(1));
			services.put("emoteto",new Integer(1));
			services.put("locate",new Integer(1));
			services.put("finger",new Integer(1));
			services.put("channel",new Integer(1));
			options.add(services);
			options.add(new Integer(0));
			sendString(packet.toString());
			setConnected(true);
			fullconnect=false;
		}
		catch (Exception e)
		{
		}
	}

	private void disconnect()
	{
		IntermudPacket packet = new IntermudPacket("shutdown",mudname,"0","*gjs","0");
		packet.getOptions().add(new Integer(0));
		sendString(packet.toString());
		setConnected(false);
		fullconnect=false;
		try
		{
			router.close();
		}
		catch (Exception e)
		{
		}
	}

	private synchronized void setConnected(boolean value)
	{
		connected=value;
	}

	private synchronized boolean isConnected()
	{
		return connected;
	}

	private synchronized void setHalted(boolean value)
	{
		halted=value;
	}

	private synchronized boolean isHalted()
	{
		return halted;
	}

	private void processPacket(IntermudPacket packet)
	{
		if (packet.getType().equals("startup-reply"))
		{
			ListIterator loop = packet.getOptionList();
			List routers = (List)loop.next();
			List router = (List)routers.get(0);
			String newname = router.get(0).toString();
			String addresses = router.get(1).toString();
			StringTokenizer tokens = new StringTokenizer(addresses," ");
			String newaddress = tokens.nextToken().toString();
			int newport = Integer.parseInt(tokens.nextToken().toString());
			password = Integer.parseInt(loop.next().toString());
			if (!((newname.equals(routername))&&(newaddress.equals(routeraddress))&&(newport==routerport)))
			{
				server.pluginMessage(plugin,"Changing router");
				routername=newname;
				routeraddress=newaddress;
				routerport=newport;
				disconnect();
				connect();
			}
		}
		else if (packet.getType().equals("who-req"))
		{
			server.pluginMessage(plugin,"Who request from "+packet.getOriginUser()+"@"+packet.getOrigin());
			IntermudPacket who = new IntermudPacket("who-reply",mudname,"0",packet.getOrigin(),packet.getOriginUser());
			List playarray = new LinkedList();
			Iterator loop = server.getPlayers();
			while (loop.hasNext())
			{
				List playinfo = new LinkedList();
				Player player = (Player)loop.next();
				if (player.getVisibility()==0)
				{
					playinfo.add(PlayerIO.stripColour(player.getName()));
					playinfo.add(new Integer(player.getIdleTimeSecs()));
					String extra;
					if (player.isPower())
					{
						extra=" (God)";
					}
					else if (player.isDPower())
					{
						extra=" (DemiGod)";
					}
					else
					{
						extra=" ("+player.getLevelName()+")";
					}
					playinfo.add(PlayerIO.stripColour(player.getTitle())+extra);
					playarray.add(playinfo);
				}
			}
			who.getOptions().add(playarray);
			sendString(who.toString());
		}
		else if (packet.getType().equals("who-reply"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if (found)
			{
				ListIterator options = packet.getOptionList();
				List playerlist = (List)options.next();
				Iterator playerloop = playerlist.iterator();
				String list;
				if (playerloop.hasNext())
				{
					list = "Player                 Idle  Misc@/\n";
					list+="-------------------------------------------------------------------------------@/\n";
					while (playerloop.hasNext())
					{
						List info = (List)playerloop.next();
						String name=info.get(0).toString();
						while (name.length()<19)
						{
							name+=" ";
						}
						int idle=Integer.parseInt(info.get(1).toString());
						String idlestr=Integer.toString(idle%60);
						if (idlestr.length()<2)
						{
							idlestr="0"+idlestr;
						}
						idlestr=Integer.toString((idle/60)%60)+":"+idlestr;
						if (idlestr.length()<5)
						{
							idlestr="0"+idlestr;
						}
						idlestr=Integer.toString(idle/3600)+":"+idlestr;
						if (idlestr.length()<8)
						{
							idlestr="0"+idlestr;
						}
						list+=name+idlestr+"  "+makeColour((String)info.get(2))+"@/\n";
					}
				}
				else
				{
					list="There are no visible players on "+packet.getOrigin();
				}
				target.displayText(list);
			}
		}
		else if (packet.getType().equals("locate-req"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			String username=packet.getOptionList().next().toString();
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(username))
				{
					target=thisone;
					found=true;
				}
			}
			if ((found)&&(target.getVisibility()==0))
			{
				target.displayText("@+W[@+BIntermud: @*"+packet.getOriginUser()+"@@"+packet.getOrigin()+" has just located you over Intermud@+W]");
				IntermudPacket ip = new IntermudPacket("locate-reply",mudname,"0",packet.getOrigin(),packet.getOriginUser());
				List options = ip.getOptions();
				options.add(mudname);
				options.add(PlayerIO.stripColour(target.getName()));
				options.add(new Integer(target.getIdleTimeSecs()));
				if (target.checkFlag("linkdead"))
				{
					options.add("link-dead");
				}
				else if (target.checkFlag("away"))
				{
					options.add("afk: "+target.getAwayMessage());
				}
				else
				{
					options.add("inactive");
				}
				sendString(ip.toString());
			}
		}
		else if (packet.getType().equals("locate-reply"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if (found)
			{
				ListIterator options = packet.getOptionList();
				String mudname=options.next().toString();
				String visname=options.next().toString();
				target.displayText(visname+" was located on "+mudname);
			}
		}
		else if (packet.getType().equals("finger-req"))
		{
			String username=packet.getOptionList().next().toString();
			Item target=server.getWorld().findItem("players."+username.toLowerCase());
			if ((target!=null)&&(target.asPlayer()!=null)&&(target.getVisibility()==0))
			{
				target.asPlayer().displayText("@+W[@+BIntermud: @*"+packet.getOriginUser()+"@@"+packet.getOrigin()+" has just fingered you over Intermud@+W]");
				IntermudPacket ip = new IntermudPacket("finger-reply",mudname,"0",packet.getOrigin(),packet.getOriginUser());
				List options = ip.getOptions();
				options.add(PlayerIO.stripColour(target.asPlayer().getName()));
				options.add(PlayerIO.stripColour(target.asPlayer().getTitle()));
				options.add(new Integer(0));
				options.add(new Integer(0));
				if (target.asPlayer().isConnected())
				{
					options.add(target.asPlayer().getLogOn());
					options.add(new Integer(target.asPlayer().getIdleTimeSecs()));
				}
				else
				{
					options.add(target.asPlayer().getLastOn());
					options.add(new Integer(-1));
				}
				options.add(new Integer(0));
				String extra;
				if (target.asPlayer().isPower())
				{
					extra="God";
				}
				else if (target.asPlayer().isDPower())
				{
					extra="DemiGod";
				}
				else
				{
					extra=Integer.toString(target.asPlayer().getLevel())+" ("+target.asMobile().getLevelName()+")";
				}
				options.add(extra);
				options.add(new Integer(0));
				sendString(ip.toString());
			}
			else
			{
				IntermudPacket ip = new IntermudPacket("error",mudname,"0",packet.getOrigin(),packet.getOriginUser());
				List options = ip.getOptions();
				options.add("unk-user");
				options.add("No such user in database.");
				String packetdata = packet.toString();
				packetdata=packetdata.substring(2,packetdata.length()-2);
				options.add(ip.parseList(new StringTokenizer(packetdata,",",true)));
				sendString(ip.toString());
			}
		}
		else if (packet.getType().equals("finger-reply"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if (found)
			{
				ListIterator options = packet.getOptionList();
				String username=options.next().toString()+"@"+packet.getOrigin();
				String title=options.next().toString();
				String rlname=options.next().toString();
				String email=options.next().toString();
				String logtime=options.next().toString();
				String idle=options.next().toString();
				String ip=options.next().toString();
				String level=options.next().toString();
				String list="";
				list+="User      : "+username+"@/\n";
				if (!level.equals("0"))
				{
					list+="Level     : "+level+"@/\n";
				}
				if (!title.equals("0"))
				{
					list+="Title     : "+makeColour(title)+"@/\n";
				}
				if (!rlname.equals("0"))
				{
					list+="Real Name : "+rlname+"@/\n";
				}
				if (!email.equals("0"))
				{
					list+="Email     : "+email+"@/\n";
				}
				if (idle.equals("-1"))
				{
					list+="Last On   : "+logtime+"@/\n";
				}
				else
				{
					list+="Logged On : "+logtime+"@/\n";
				}
				target.displayText(list);
			}
		}
		else if (packet.getType().equals("tell"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if ((found)&&(target.getVisibility()==0))
			{
				ListIterator options = packet.getOptionList();
				String lastplayer=options.next().toString();
				target.setVariable("intermudlastmud",packet.getOrigin());
				target.setVariable("intermudlastplayer",lastplayer);
				target.displayText("@+G"+lastplayer+"@*@@@+G"+packet.getOrigin()+"@+y tells you : @+W'@*"+makeColour(options.next().toString())+"@+W'");
			}
			else
			{
				IntermudPacket ip = new IntermudPacket("error",mudname,"0",packet.getOrigin(),packet.getOriginUser());
				List options = ip.getOptions();
				options.add("unk-user");
				options.add("User is not online.");
				String packetdata = packet.toString();
				packetdata=packetdata.substring(2,packetdata.length()-2);
				options.add(ip.parseList(new StringTokenizer(packetdata,",",true)));
				sendString(ip.toString());
			}
		}
		else if (packet.getType().equals("emoteto"))
		{
			Iterator loop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((loop.hasNext())&&(!found))
			{
				Player thisone = (Player)loop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if ((found)&&(target.getVisibility()==0))
			{
				ListIterator options = packet.getOptionList();
				String camefrom=options.next().toString()+"@@"+packet.getOrigin();
				Variables vars = new Variables();
				vars.setVariable("$N",camefrom);
				target.displayText(vars.parseString(makeColour(options.next().toString())));
			}
			else
			{
				IntermudPacket ip = new IntermudPacket("error",mudname,"0",packet.getOrigin(),packet.getOriginUser());
				List options = ip.getOptions();
				options.add("unk-user");
				options.add("User is not online.");
				String packetdata = packet.toString();
				packetdata=packetdata.substring(2,packetdata.length()-2);
				options.add(ip.parseList(new StringTokenizer(packetdata,",",true)));
				sendString(ip.toString());
			}
		}
		else if (packet.getType().equals("channel-m"))
		{
			Iterator loop=packet.getOptionList();
			String channel=loop.next().toString();
			if ((channel.equals("imud_gossip"))||(channel.equals("imud_code")))
			{
				LinkedList needflags = new LinkedList();
				LinkedList badflags = new LinkedList();
				if (channel.equals("imud_gossip"))
				{
					channel="IGossip";
				}
				else if (channel.equals("imud_code"))
				{
					needflags.add("dpower");
					needflags.add("immort");
					channel="ICode";
				}
				badflags.add("No"+channel);
				String user="@+Y"+loop.next().toString()+"@*@@@+Y"+packet.getOrigin();
				String message=makeColour(loop.next().toString());
				server.sendAllMessage(0,"@+W["+channel+"] "+user+":@* "+message,needflags,badflags);
			}
		}
		else if (packet.getType().equals("channel-e"))
		{
			Iterator loop=packet.getOptionList();
			String channel=loop.next().toString();
			if ((channel.equals("imud_gossip"))||(channel.equals("imud_code")))
			{
				LinkedList needflags = new LinkedList();
				LinkedList badflags = new LinkedList();
				if (channel.equals("imud_gossip"))
				{
					channel="IGossip";
				}
				else if (channel.equals("imud_code"))
				{
					needflags.add("dpower");
					needflags.add("immort");
					channel="ICode";
				}
				badflags.add("No"+channel);
				String user=loop.next().toString()+"@@"+packet.getOrigin();
				String message=makeColour(loop.next().toString());
				Variables vars = new Variables();
				vars.setVariable("$N",user);
				server.sendAllMessage(0,"@+W["+channel+"]@* "+vars.parseString(message),needflags,badflags);
			}
		}
		else if (packet.getType().equals("channel-t"))
		{
			Iterator loop=packet.getOptionList();
			String channel=loop.next().toString();
			if ((channel.equals("imud_gossip"))||(channel.equals("imud_code")))
			{
				LinkedList needflags = new LinkedList();
				LinkedList badflags = new LinkedList();
				if (channel.equals("imud_gossip"))
				{
					channel="IGossip";
				}
				else if (channel.equals("imud_code"))
				{
					needflags.add("dpower");
					needflags.add("immort");
					channel="ICode";
				}
				badflags.add("No"+channel);
				String targmud=loop.next().toString();
				loop.next();
				String message=makeColour(loop.next().toString());
				String targetmessage=makeColour(loop.next().toString());
				String user = loop.next().toString()+"@@"+packet.getOrigin();
				String target = loop.next().toString();
				Variables vars = new Variables();
				vars.setVariable("$N",user);
				vars.setVariable("$O",target+"@@"+targmud);
				if (targmud.equalsIgnoreCase(mudname))
				{
					server.sendAllMessage(0,"@+W["+channel+"]@* "+vars.parseString(message),"players."+target.toLowerCase(),needflags,badflags);
					Player pltarget=null;
					boolean found=false;
					while ((loop.hasNext())&&(!found))
					{
						Player thisone = (Player)loop.next();
						if (thisone.getName().equalsIgnoreCase(target))
						{
							pltarget=thisone;
							found=true;
						}
					}
					if (found)
					{
						pltarget.displayText("@+W["+channel+"]@* "+vars.parseString(targetmessage));
					}
				}
				else
				{
					server.sendAllMessage(0,"@+W["+channel+"]@* "+vars.parseString(message),needflags,badflags);
				}
			}
		}
		else if (packet.getType().equals("chanlist-reply"))
		{
			Iterator options = packet.getOptionList();
			chanlistid=Integer.parseInt(options.next().toString());
			Map channels = (Map)options.next();
			fullconnect=true;
		}
		else if (packet.getType().equals("error"))
		{
			System.out.print("Error for "+packet.getDestinationUser()+" - ");
			ListIterator loop = packet.getOptionList();
			String code=loop.next().toString();
			String message=loop.next().toString();
			System.out.println(code+": "+message);
			Iterator playerloop = server.getPlayers();
			boolean found=false;
			Player target=null;
			while ((playerloop.hasNext())&&(!found))
			{
				Player thisone = (Player)playerloop.next();
				if (thisone.getName().equalsIgnoreCase(packet.getDestinationUser()))
				{
					target=thisone;
					found=true;
				}
			}
			if (found)
			{
				target.displayText("@+W[@+BIntermud: @*"+message+"@+W]");
			}
		}
		else if (packet.getType().equals("mudlist"))
		{
			ListIterator loop = packet.getOptionList();
			mudlistid=Integer.parseInt(loop.next().toString());
			Map mapping = (Map)loop.next();
			Iterator maploop = mapping.keySet().iterator();
			synchronized(muds)
			{
				while (maploop.hasNext())
				{
					String mudname=maploop.next().toString();
					if ((mudname!=null)&&(mudname.length()>0))
					{
						MudInfo mudinfo=(MudInfo)muds.get(mudname);
						if (mapping.get(mudname).toString().equals("0"))
						{
							if (mudinfo!=null)
							{
								if (fullconnect)
								{
									LinkedList badflags = new LinkedList();
									badflags.add("NoIInfo");
									server.sendAllMessage(0,"@+W[@+BIntermud: @*"+mudname+" disconnected from Intermud@+W]",new LinkedList(),badflags);
								}
								muds.remove(mudname);
							}
						}
						else
						{
							List muddata = (List)mapping.get(mudname);
							int newstate=Integer.parseInt(muddata.get(0).toString());
							if (mudinfo==null)
							{
								if ((fullconnect)&&(newstate==-1))
								{
									LinkedList badflags = new LinkedList();
									badflags.add("NoIInfo");
									server.sendAllMessage(0,"@+W[@+BIntermud: @*"+mudname+" connected to Intermud@+W]",new LinkedList(),badflags);
								}
								mudinfo = new MudInfo();
								mudinfo.setName(mudname);
								muds.put(mudname,mudinfo);
							}
							else if (fullconnect)
							{
								LinkedList badflags = new LinkedList();
								badflags.add("NoIInfo");
								if ((mudinfo.getState()>=0)&&(newstate==-1))
								{
									server.sendAllMessage(0,"@+W[@+BIntermud: @*"+mudname+" connected to Intermud@+W]",new LinkedList(),badflags);
								}
								else if ((mudinfo.getState()==-1)&&(newstate>=0))
								{
									server.sendAllMessage(0,"@+W[@+BIntermud: @*"+mudname+" disconnected from Intermud@+W]",new LinkedList(),badflags);
								}
							}
							mudinfo.setState(newstate);
							mudinfo.setAddress(muddata.get(1).toString());
							mudinfo.setPort(Integer.parseInt(muddata.get(2).toString()));
							mudinfo.setTcpPort(Integer.parseInt(muddata.get(3).toString()));
							mudinfo.setUdpPort(Integer.parseInt(muddata.get(4).toString()));
							mudinfo.setMudLib(muddata.get(5).toString());
							mudinfo.setBaseLib(muddata.get(6).toString());
							mudinfo.setDriver(muddata.get(7).toString());
							mudinfo.setMudType(muddata.get(8).toString());
							mudinfo.setOpenStatus(muddata.get(9).toString());
							mudinfo.setEmail(muddata.get(10).toString());
							if (muddata.get(11).toString().equals("0"))
							{
								mudinfo.setServices(new HashMap());
							}
							else
							{
								mudinfo.setServices((Map)muddata.get(11));
							}
							if (muddata.get(12).toString().equals("0"))
							{
								mudinfo.setOtherData(new HashMap());
							}
							else
							{
								mudinfo.setOtherData((Map)muddata.get(12));
							}
						}
					}
				}
			}
		}
		else
		{
			System.out.println(packet.getType()+" from "+packet.getOrigin());
			IntermudPacket ip = new IntermudPacket("error",mudname,"0",packet.getOrigin(),packet.getOriginUser());
			List options = ip.getOptions();
			options.add("unk-type");
			options.add("Feature not implemented.");
			String packetdata = packet.toString();
			packetdata=packetdata.substring(2,packetdata.length()-2);
			options.add(ip.parseList(new StringTokenizer(packetdata,",",true)));
			sendString(ip.toString());
		}
	}

	public void run()
	{
		try
		{
			while (!isHalted())
			{
				connect();
				if (!isConnected())
				{
					try
					{
						Thread.sleep(10000);
					}
					catch (Exception e)
					{
					}
				}
				else
				{
					server.pluginMessage(plugin,"Connection established");
				}
				while (isConnected())
				{
					while ((!isHalted())&&(input.available()<4))
					{
						Thread.sleep(10);
					}
					if (!isHalted())
					{
						int length=input.readInt();
						if (length==-1)
						{
							server.pluginMessage(plugin,"Connection lost");
							setConnected(false);
						}
						else
						{
							byte[] data = new byte[length];
							int got=0;
							while (got<length)
							{
								got+=input.read(data,got,length-got);
							}
							if (got==length)
							{
								String contents = new String(data);
								while (contents.endsWith("\u0000"))
								{
									contents=contents.substring(0,contents.length()-1);
								}
								if ((contents.startsWith("({"))&&(contents.endsWith("})")))
								{
									contents=contents.substring(2,contents.length()-2);
									try
									{
										IntermudPacket packet = new IntermudPacket(contents);
										processPacket(packet);
									}
									catch (Exception e)
									{
										server.pluginMessage(plugin,"Bad packet recieved");
										System.out.println("Bad packet - "+contents);
										server.logCrash(e);
									}
								}
							}
						}
					}
					if (isHalted())
					{
						disconnect();
					}
				}
			}
		}
		catch (Exception e)
		{
			server.pluginMessage(plugin,"Crash during run");
			server.logCrash(e);
			setConnected(false);
		}
	}
}