package com.esp.tawemud;

import java.util.Vector;
import java.util.Random;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.StringTokenizer;
import com.esp.tawemud.tawescript.BaseCommand;
import com.esp.tawemud.tawescript.Variables;
import com.esp.tawemud.tawescript.Special;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.text.SimpleDateFormat;
import com.esp.tawemud.items.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.esp.tawemud.xml.XmlLoader;
import com.esp.tawemud.xml.XmlWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;

public class TaweServer implements ServerBase, Runnable
{
	private ServerListener listener;
	private World world;
	private List players;
	private List events;
	private List messagequeue;
	private String worldurl;
	private boolean halted;
	private boolean running;
	private long cyclecount;
	private Collection boards;
	private Collection plugins;
	private FileWriter filelog;
	private Random random;
	private long starttick;
	private Mobile trace;
	private String mudlog;
	public static final String PACKAGE = "com.esp.tawemud";
	private static String BUILD = "";
	private static final String VERSION = "TaweMUD v0.95";
	private static final String BASEVERSION = "TaweMUD v0";

	public TaweServer()
	{
		try
		{
			Properties buildprops = new Properties();
			buildprops.load(getClass().getClassLoader().getResourceAsStream("com/esp/tawemud/build.properties"));
			BUILD=buildprops.getProperty("build.date","00000000")+"."+buildprops.getProperty("build.time","0000");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("Creating TaweServer - "+VERSION+" build "+BUILD);
		random = new Random();
		boards = new LinkedList();
		plugins = new LinkedList();
		events = new LinkedList();
		halted=false;
		running=false;
		players = new LinkedList();
		messagequeue = new LinkedList();
		cyclecount=0;
		starttick=0;
		trace=null;
		mudlog="";
	}

	public static String getBaseVersion()
	{
		return BASEVERSION;
	}
	
	public static String getVersion()
	{
		return VERSION;
	}
	
	public static String getBuild()
	{
		return BUILD;
	}
	
	public void setMudLog(String newlog)
	{
		mudlog=newlog;
	}

	public String getMudLog()
	{
		return mudlog;
	}

	public void startTrace(Mobile mobile)
	{
		trace=mobile;
	}

	public void stopTrace()
	{
		trace=null;
	}

	public Mobile getTracer()
	{
		return trace;
	}

	public void setWorldURL(String dir)
	{
		worldurl=dir;
		if (!worldurl.endsWith("/"))
		{
			worldurl+="/";
		}
	}

	public String getWorldURL()
	{
		try
		{
			return worldurl;
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public Random getRandom()
	{
		return random;
	}

	public long getCycleTime()
	{
		return (System.currentTimeMillis()-starttick)/cyclecount;
	}

	public boolean connectPlayer(String name, IOBase client)
	{
		synchronized (this)
		{
			Item player = world.findItem("players."+name.toLowerCase());
			if ((player!=null)&&(player.asPlayer()!=null))
			{
				client.setServer(this);
				player.asPlayer().connect(client);
				Iterator loop = events.iterator();
				while (loop.hasNext())
				{
					((Event)loop.next()).notify(player.asMobile());
				}
				return true;
			}
			else
			{
				return false;
			}
		}
	}

	private synchronized boolean isHalted()
	{
		return halted;
	}

	private synchronized void setHalted(boolean value)
	{
		halted=value;
	}

	public synchronized boolean isRunning()
	{
		return running;
	}

	private synchronized void setRunning(boolean value)
	{
		running=value;
	}

	public void registerPlayer(Player player)
	{
		if (!players.contains(player))
		{
			players.add(player);
		}
	}

	public void unregisterPlayer(Player player)
	{
		players.remove(player);
	}

	public void registerBoard(Board mh)
	{
		boards.add(mh);
	}

	public Iterator getBoards(int level)
	{
		Collection boardlist = new LinkedList();
		Iterator loop = boards.iterator();
		while (loop.hasNext())
		{
			Board thisone = (Board)loop.next();
			if (thisone.getLevel()<=level)
			{
				boardlist.add(thisone);
			}
		}
		return boardlist.iterator();
	}

	public void startup(String log) throws Exception
	{
		if (!isRunning())
		{
			world.place();
			StringWriter buffer = new StringWriter();
			PrintWriter out = new PrintWriter(buffer);
			out.println(log);
			out.flush();
			setMudLog(PlayerIO.convertText(buffer.toString()));
			(new Thread(this,"TaweServer")).start();
		}
		else
		{
			throw new Exception("Server already running");
		}
	}

	public void addEvent(Event newevent)
	{
		events.add(newevent);
	}
	
	public void addZone(Element node, PrintWriter out)
	{
		world.addNewZone(node,out);
	}

	public boolean loadFullWorld(PrintWriter out)
	{
		if (loadBasicWorld(out))
		{
			world.loadZones(out);
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean loadBasicWorld(PrintWriter out)
	{
		World newworld = new World(this);
		if (newworld.loadFrom("world.xml",out))
		{
			world=newworld;
			world.loadLevels(out);
			world.loadRaces(out);
			world.loadEmotes(out);
			world.loadInfo(out);
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean loadWorld(PrintWriter out)
	{
		World newworld = new World(this);
		if (newworld.loadFrom("world.xml",out))
		{
			world=newworld;
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean callPluginFunction(Mobile user, String pluginname, String function, String options)
	{
		boolean result=false;
		Iterator loop = plugins.iterator();
		while (loop.hasNext())
		{
			Plugin plugin = (Plugin)loop.next();
			if (plugin.getName().equalsIgnoreCase(pluginname))
			{
				result=plugin.callFunction(user,function,options);
			}
		}
		return result;
	}

	public void log(String message)
	{
		SimpleDateFormat dateformat = new SimpleDateFormat("[HH:mm dd/MM/yyyy] ");
		try
		{
			filelog.write(dateformat.format(new Date())+message+"\n");
			filelog.flush();
		}
		catch (Exception e)
		{
		}
	}

	public void pluginMessage(Plugin plugin, String message)
	{
		sendWizMessage(plugin.getName()+": "+message,"",0,2);
	}

	public boolean loadPlugin(String classname)
	{
		try
		{
			Plugin plugin = Plugin.createInstance(classname);
			if (plugin!=null)
			{
				plugin.startup(this);
				plugins.add(plugin);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public void startPlugins(PrintWriter out)
	{
		try
		{
			plugins.clear();
			Document doc = XmlLoader.parse(getWorldURL()+"plugins.xml",out,false);
			if (doc.getDocumentElement()!=null)
			{
				NodeList nodes = doc.getDocumentElement().getChildNodes();
				for (int loop=0; loop<nodes.getLength(); loop++)
				{
					if (nodes.item(loop).getNodeType()==Node.ELEMENT_NODE)
					{
						Element thisone = (Element)nodes.item(loop);
						if (thisone.getNodeName().equals("Plugin"))
						{
							Plugin plugin = Plugin.createInstance(thisone.getAttribute("class"));
							if (plugin!=null)
							{
								plugin.parseElement(thisone,out);
								plugin.startup(this);
								plugins.add(plugin);
								out.println("Loaded plugin: "+plugin.getName());
							}
						}
					}
				}
			}
		}
		catch (Exception e)
		{
			out.println("There was an error loading the file - "+e.getMessage());
			e.printStackTrace(out);
		}
	}

	public void stopPlugins()
	{
		try
		{
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			Element node = doc.createElement("Plugins");
			doc.appendChild(node);
			Iterator loop = (new LinkedList(plugins)).iterator();
			while (loop.hasNext())
			{
				Plugin plugin = (Plugin)loop.next();
				node.appendChild(plugin.getElement(doc));
				plugin.shutdown();
			}
			FileWriter output = new FileWriter(new File((new URL(world.getServer().getWorldURL()+"plugins.xml")).getFile()));
			XmlWriter.write(doc,output);
			output.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void sendWizMessage(String message)
	{
		sendWizMessage(message,"",0,1);
	}

	public void sendWizMessage(String message, String noshow, int vis, int level, boolean dolog)
	{
		sendWizMessage(message,noshow,vis,level);
		if (dolog)
		{
			log(message);
		}
	}

	public void sendWizMessage(String message, String noshow, int vis, int level)
	{
		LinkedList needed = new LinkedList();
		String colour="";
		if (level==1)
		{
			needed.add("immort");
			colour="@+M";
		}
		else if (level==2)
		{
			needed.add("dpower");
			colour="@+G";
		}
		else if (level==3)
		{
			needed.add("power");
			colour="@+R";
		}
		sendAllMessage(vis,"@+W["+colour+message+"@+W]@*",noshow,needed,new LinkedList());
	}

	public void sendAllMessage(int vis, String message)
	{
		sendAllMessage(vis,message,new LinkedList(),new LinkedList());
	}

	public void sendAllMessage(int vis, String message, Collection needflags, Collection badflags)
	{
		sendAllMessage(vis,message,"",needflags,badflags);
	}

	public void sendAllMessage(int vis, String message, String noshow, Collection needflags, Collection badflags)
	{
		Iterator loop = players.iterator();
		while (loop.hasNext())
		{
			Player player = (Player)loop.next();
			if (noshow.indexOf(player.getWorldIdentifier())==-1)
			{
				boolean good=true;
				Iterator subloop = needflags.iterator();
				while (subloop.hasNext())
				{
					if (!player.checkFlag(subloop.next().toString()))
					{
						good=false;
					}
				}
				subloop=badflags.iterator();
				while (subloop.hasNext())
				{
					if (player.checkFlag(subloop.next().toString()))
					{
						good=false;
					}
				}
				if (good)
				{
					player.displayText(noshow,vis,message);
				}
			}
		}
	}

	public Class getClassFromType(String type) throws ClassNotFoundException
	{
		Class result = null;
		try
		{
			TaweLoader loader = (TaweLoader)getClass().getClassLoader();
			result=Class.forName(loader.findClassName(PACKAGE+".items."+type));
		}
		catch (Exception e)
		{
			result=null;
		}
		if (result==null)
		{
			type=type.substring(0,1).toUpperCase()+type.substring(1);
			result=Class.forName(PACKAGE+".items."+type);
		}
		return result;
	}

	public void halt()
	{
		LinkedList list = new LinkedList(players);
		Iterator loop = list.iterator();
		while (loop.hasNext())
		{
			Player thisone = (Player)loop.next();
			thisone.displayText("The mud has had to be shutdown for routine maintenance.");
			parseCommand(thisone,"quit");
		}
		shutdown();
	}

	public void shutdown()
	{
		setHalted(true);
	}

	public String getLoginMessage()
	{
		return world.getLoginMessage();
	}

	public String getConnectMessage()
	{
		return world.getConnectMessage();
	}

	public boolean userExists(String name)
	{
		Item item = world.findItem("players."+name.toLowerCase());
		return ((item!=null)&&(item.asPlayer()!=null));
	}

	public boolean isBanned(String name)
	{
		Item item = world.findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().isBanned();
		}
		else
		{
			return false;
		}
	}

	public boolean authenticate(String name, String password)
	{
		Item item = world.findItem("players."+name.toLowerCase());
		if ((item!=null)&&(item.asPlayer()!=null))
		{
			return item.asPlayer().authenticate(password);
		}
		else
		{
			return false;
		}
	}

	public void doMove(Item item, Container destination)
	{
		Container oldloc = item.getLocation();
		if (oldloc!=null)
		{
			parseSpecial(item,Special.ST_EXIT,oldloc.getWorldIdentifier()+" "+destination.getWorldIdentifier());
		}
		item.setLocation(destination);
		parseSpecial(item,Special.ST_ENTER,oldloc.getWorldIdentifier()+" "+destination.getWorldIdentifier());
	}

	public void sendCommand(Mobile mobile, String command)
	{
		synchronized (messagequeue)
		{
			messagequeue.add(new Message(mobile,command));
		}
	}

	public void setWorld(World newworld)
	{
		world=newworld;
	}

	public World getWorld()
	{
		return world;
	}

	public void parseCommand(Mobile caller, String command)
	{
		if (command.length()>0)
		{
			caller.addCommandCount();
			try
			{
				if (command.startsWith("'"))
				{
					command="say "+command.substring(1);
				}
				else if (command.startsWith(";"))
				{
					command="emote "+command.substring(1);
				}
				String found;
				String args;
				if (command.indexOf(' ')>=0)
				{
					found=command.substring(0,command.indexOf(' ')).toLowerCase();
					args=command.substring(command.indexOf(' ')+1);
				}
				else
				{
					found=command.toLowerCase();
					args="";
				}
				while (args.startsWith(" "))
				{
					args=args.substring(1);
				}
				List itemlist = new LinkedList();
				caller.findCodeableObjects(itemlist);
				List foundcommands = new LinkedList();
				Iterator doloop = itemlist.iterator();
				while (doloop.hasNext())
				{
					((CodeableObject)doloop.next()).addCommands(foundcommands.listIterator(),found);
				}
				boolean worked = false;
				Iterator commands = foundcommands.iterator();
				BaseCommand thisone;
				while ((!worked)&&(commands.hasNext()))
				{
					thisone=(BaseCommand)commands.next();
					worked=thisone.callCommand(this,caller,found,args);
				}
				if ((!worked)&&(!world.fireEmote(caller,found,args)))
				{
					boolean good = false;
					Iterator plloop = players.iterator();
					while ((!good)&&(plloop.hasNext()))
					{
						if (((Player)plloop.next()).hasName(found))
						{
							good=true;
						}
					}
					if (good)
					{
						parseCommand(caller,"tell "+command);
					}
					else
					{
						caller.displayText("Pardon?");
					}
				}
			}
			catch (Throwable e)
			{
				e.printStackTrace();
				sendWizMessage(caller.getName()+" caused exception running \""+command+"\"","",0,2);
				Mail exception = new Mail();
				exception.setSender(caller.getName());
				StringWriter stacktrace = new StringWriter();
				PrintWriter out = new PrintWriter(stacktrace);
				e.printStackTrace(out);
				exception.setSubject(new StringBuffer("Running command \""+command+"\""));
				StringBuffer buffer = new StringBuffer();
				buffer.append(caller.getWorldIdentifier()+" was in "+caller.getLocation().getWorldIdentifier()+"\n\n");
				buffer.append(caller.getWorldIdentifier()+" was carrying:\n");
				Iterator items = caller.getItemContentsIterator();
				while (items.hasNext())
				{
					buffer.append("    "+((Item)items.next()).getWorldIdentifier()+"\n");
				}
				buffer.append("\n"+caller.getLocation().getWorldIdentifier()+" contained:\n");
				items=caller.getLocation().getItemContentsIterator();
				while (items.hasNext())
				{
					buffer.append("    "+((Item)items.next()).getWorldIdentifier()+"\n");
				}
				if (caller.getLocation().asRoom()!=null)
				{
					items=caller.getLocation().asRoom().getMobileContentsIterator();
					while (items.hasNext())
					{
						buffer.append("    "+((Item)items.next()).getWorldIdentifier()+"\n");
					}
				}
				buffer.append("\nThe following exception occurred:\n\n");
				buffer.append(stacktrace);
				exception.getContent().append(PlayerIO.convertText(buffer.toString()));
				Item board = world.findItem("powers.exceptionboard");
				if ((board!=null)&&(board.getMailHandler()!=null))
				{
					board.getMailHandler().addMail(exception);
				}
			}
			caller.deleteCommandCount();
		}
	}

	public void sleep(int nanos)
	{
		try
		{
			Thread.sleep(0,nanos);
		}
		catch (InterruptedException e)
		{
		}
	}

	public void parseSpecial(Item item, int type, String message)
	{
		Variables variables = new Variables();
		variables.setVariable("$0",item);
		int loop=1;
		StringTokenizer tokens = new StringTokenizer(message);
		while (tokens.hasMoreTokens())
		{
			variables.setVariable("$"+Integer.toString(loop),tokens.nextToken());
			loop++;
		}
		List itemlist = new LinkedList();
		item.findCodeableObjects(itemlist);
		ListIterator doloop = itemlist.listIterator();
		while (doloop.hasNext())
		{
			doloop.next();
		}
		boolean worked = false;
		while ((!worked)&&(doloop.hasPrevious()))
		{
			worked=((CodeableObject)doloop.previous()).launchSpecial(type,variables);
		}
	}

	public Iterator getPlayers()
	{
		return players.iterator();
	}

	public void logCrash(Throwable e)
	{
		sendWizMessage("System crash - "+e.getMessage(),"",0,2,true);
		e.printStackTrace();
		Mail exception = new Mail();
		StringWriter stacktrace = new StringWriter();
		PrintWriter out = new PrintWriter(stacktrace);
		e.printStackTrace(out);
		exception.setSubject(new StringBuffer("System Crash - "+e.getMessage()));
		StringBuffer buffer = new StringBuffer();
		buffer.append("\nThe following exception occurred:\n\n");
		buffer.append(stacktrace);
		exception.getContent().append(PlayerIO.convertText(buffer.toString()));
		Item board = world.findItem("powers.exceptionboard");
		if ((board!=null)&&(board.getMailHandler()!=null))
		{
			board.getMailHandler().addMail(exception);
		}
	}

	public void cycle()
	{
		for (int loop=events.size()-1; loop>=0; loop--)
		{
			if (((Event)events.get(loop)).cycle(this))
			{
				events.remove(loop);
			}
		}
		world.cycle();
		cyclecount++;
	}
	
	public void run()
	{
		starttick=System.currentTimeMillis();
		cyclecount=0;
		setRunning(true);
		setHalted(false);
		try
		{
			{
				StringWriter buffer = new StringWriter();
				PrintWriter out = new PrintWriter(buffer);
				Item log = world.findItem("permanant.log");
				if (log!=null)
				{
					out.print(log.getDescription());
				}
				startPlugins(out);
				if (log!=null)
				{
					out.flush();
					log.setDescription(buffer.toString());
				}
			}
			listener = new ServerListener(this);
			filelog=new FileWriter((new URL(getWorldURL()+"serverlog")).getFile(),true);
			sendWizMessage("Server startup - "+VERSION+" build "+BUILD,"",0,2,true);
			boolean saved = true;
			while (!isHalted())
			{
				try
				{
					synchronized (messagequeue)
					{
						if (messagequeue.size()>0)
						{
							Message next = (Message)messagequeue.get(0);
							messagequeue.remove(0);
							parseCommand(next.getItem().asMobile(),next.getMessage());
						}
					}
					cycle();
					sleep(100);
				}
				catch (Throwable e)
				{
					logCrash(e);
				}
			}
			listener.shutdown();
			log("Server shutdown");
			filelog.close();
			stopPlugins();
			System.out.println("Server shutdown");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		setRunning(false);
	}
}
