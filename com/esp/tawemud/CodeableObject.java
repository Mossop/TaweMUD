package com.esp.tawemud;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.HashMap;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import com.esp.tawemud.tawescript.Command;
import com.esp.tawemud.tawescript.BaseCommand;
import com.esp.tawemud.tawescript.NLCommand;
import com.esp.tawemud.tawescript.Special;
import com.esp.tawemud.tawescript.Variables;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.Text;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import java.util.StringTokenizer;
import com.esp.tawemud.tawescript.Subroutine;
import com.esp.tawemud.items.*;

/**
 * The most basic object in the mud.
 *
 * All other objects are all specialisations of this class.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public abstract class CodeableObject implements About
{
	/**
	 * Holds the description for the object.
	 */
	private String description = "";
	/**
	 * Holds the identifier for the object.
	 */
	private String identifier = "";
	/**
	 * A collection of commands that may be run from the object.
	 */
	private List commands;
	/**
	 * A collection of specials that may be run from the object.
	 */
	private List specials;
	/**
	 * The current server.
	 */
	protected TaweServer server;
	/**
	 * A collection of flags on the object.
	 */
	private List flags;
	/**
	 * What the flags should revert to when the object is reset.
	 */
	private List startflags;
	/**
	 * Subroutines that can be called from specials or commands.
	 */
	private List subroutines;
	/**
	 * Variables for dynamic attributes on the object.
	 */
	private HashMap variables;
	/**
	 * A helper list of attributes that havent been set properly yet.
	 */
	private HashMap unset;

	/**
	 * Initialises the object to default values.
	 *
	 * @param thisserver  The current server.
	 */
	public CodeableObject(TaweServer thisserver)
	{
		server=thisserver;
		commands = new LinkedList();
		subroutines = new LinkedList();
		specials = new ArrayList();
		flags = new LinkedList();
		startflags = new LinkedList();
		variables = new HashMap();
		unset = new HashMap();
	}

	/**
	 * Sets an attribute on the object using the specified method.
	 *
	 * This method looks at the parameter type of the given method, then converts the given value
	 * into a form that can be used with the method.
	 *
	 * @param method  The method to use.
	 * @param value The value to set.
	 * @return  True if the set was succesfull.
	 */
	private boolean doSet(Method method, String value)
	{
		boolean found=true;
		try
		{
			Class paramtype=method.getParameterTypes()[0];
			if (paramtype.getName().equals("java.lang.String"))
			{
				method.invoke(this,new Object[] {value});
			}
			else if (paramtype.getName().equals("int"))
			{
				Integer intattr = new Integer(value);
				method.invoke(this,new Object[] {intattr});
			}
			else if (paramtype.getName().equals("short"))
			{
				Short intattr = new Short(value);
				method.invoke(this,new Object[] {intattr});
			}
			else if (paramtype.getName().equals("long"))
			{
				Long intattr = new Long(value);
				method.invoke(this,new Object[] {intattr});
			}
			else if (Class.forName(TaweServer.PACKAGE+".items.Item").isAssignableFrom(paramtype))
			{
				Item objattr = null;
				if (asWorld()!=null)
				{
					objattr=asWorld().findItem(value);
				}
				else if (asZone()!=null)
				{
					objattr=asZone().findItem(value);
				}
				else if (asItem()!=null)
				{
					objattr = asItem().getZone().findItem(value);
				}
				if (objattr==null)
				{
					found=false;
				}
				else
				{
					method.invoke(this,new Object[] {objattr});
				}
			}
			else if (paramtype.getName().equals(TaweServer.PACKAGE+".Zone"))
			{
				Zone newzone=null;
				newzone=server.getWorld().findZone(value);
				if (newzone!=null)
				{
					method.invoke(this,new Object[] {newzone});
				}
				else
				{
					found=false;
				}
			}
			else
			{
				found=false;
			}
		}
		catch (Exception e)
		{
			found=false;
		}
		return found;
	}

	/**
	 * Searches for a method to set the given attribute with the given value.
	 *
	 * Simply iterates this classes methods for one that has the name &quot;set&quot; followed
	 * by the attribute name. Then calls doSet with it.
	 *
	 * @see #doSet(Method, String)
	 */
	public boolean setValue(String attr, String value)
	{
		boolean found = false;
		Class thisclass = getClass();
		Method[] methods = thisclass.getMethods();
		attr="set"+attr.toLowerCase();
		int loop=0;
		while ((!found)&&(loop<methods.length))
		{
			if ((methods[loop].getParameterTypes().length==1)&&(methods[loop].getName().toLowerCase().equals(attr)))
			{
				found=doSet(methods[loop],value);
			}
			loop++;
		}
		return found;
	}

	/**
	 * Attempts to set attributes that for some reason failed to get set earlier.
	 *
	 * An object generally will reference othe object (location for example). During a load of the
	 * object, objects it references may not yet exist so setting that attribute will fail. When
	 * this happens the attribute and aimed value is stored. After the load is complete this method
	 * is called to try to set those failed attributes again.
	 */
	public void place()
	{
		Iterator loop = (new HashMap(unset)).keySet().iterator();
		while (loop.hasNext())
		{
			Method setter = (Method)loop.next();
			if (doSet(setter,(String)unset.get(setter)))
			{
				unset.remove(setter);
			}
		}
	}

	/**
	 * Updates any references to a given object with new references.
	 * During the life of a mud occasionally an object will be replaced with a new
	 * copy. Commonly this occurs while loading a zone, overwriting the zone in
	 * memory. In this case any references to objects in the old zone must be replaced with
	 * references to the new objects.
	 */
	public void updateReferences(CodeableObject oldref, CodeableObject newref)
	{
	}
	
	/**
	 * Returns a string name for what type this object is.
	 *
	 * This iterates through the class hierachy till it finds a class that is in the com.esp.tawemud
	 * packages. It then returns its name.
	 *
	 * @return  The type of the object as a String
	 */
	public String getType()
	{
		Class type = getClass();
		while (type.getName().lastIndexOf(".")>TaweServer.PACKAGE.length())
		{
			type=type.getSuperclass();
		}
		String typename=type.getName().substring(TaweServer.PACKAGE.length()+1);
		return typename;
	}

	/**
	 * Returns the value of a dynamic variable.
	 *
	 * @param varname The variable required.
	 * @return  A blank string if the variable does not exist, or the value of the variable if it does.
	 */
	public String getVariable(String varname)
	{
		String result=(String)variables.get(varname);
		if (result==null)
		{
			result="";
		}
		return result;
	}

	/**
	 * Sets the value of a dynamic variable.
	 *
	 * If the variable does not exist then it is created, otherwise it is overwritten.
	 *
	 * @param varname The variable to be set.
	 * @param value The value to set to the variable.
	 */
	public void setVariable(String varname, String value)
	{
		variables.put(varname,value);
	}

	/**
	 * Returns a set of flags of a specified type.
	 *
	 * @param	type	The type of flags.
	 * @return	A list containing the flags.
	 */
	protected List getFlags(String type)
	{
		if (type.equals("normal"))
		{
			return flags;
		}
		else if (type.equals("start"))
		{
			return startflags;
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Sets a flag on this object.
	 *
	 * If the flag is already set then a second copy of it will be set.
	 *
	 * @param flag  The flag to be set.
	 * @param	flags	The flags to be used.
	 */
	protected void setFlag(String flag, List flags)
	{
		if (flag!=null)
		{
			flags.add(flag);
		}
	}

	/**
	 * Removes a flag from this object.
	 *
	 * If the flag is not set then no action will be taken.
	 *
	 * @param flag  The flag to be removed.
	 * @param flags	The flags to be used.
	 */
	protected void removeFlag(String flag, List flags)
	{
		flags.remove(flag);
	}

	/**
	 * Sets a number of flags
	 *
	 * A helper method that takes a comma seperated list of flags and sets them.
	 *
	 * @param newflags The flags to be set.
	 * @param flags	The flags to e used.
	 */
	protected void setFlags(String newflags, List flags)
	{
		flags.clear();
		StringTokenizer tokens = new StringTokenizer(newflags,",");
		while (tokens.hasMoreTokens())
		{
			flags.add(tokens.nextToken());
		}
	}

	/**
	 * Checks if a flag is set on this object.
	 *
	 * @param flag  The flag to be checked.
	 * @param flags	The flags to be used.
	 * @return  True if the flag is set on this object, False otherwise.
	 */
	protected boolean checkFlag(String flag, List flags)
	{
		return flags.contains(flag);
	}

	/**
	 * Returns an iterator to the flags.
	 *
	 * @param		flags	The flags to work from.
	 * @return  An iterator to access each of the flags.
	 */
	protected ListIterator getFlagIterator(List flags)
	{
		return flags.listIterator();
	}

	/**
	 * Returns a comma separated list of the flags.
	 *
	 * @param	flags	The flags to list.
	 * @return	The list of flags.
	 */
	protected String getFlagList(List flags)
	{
		Iterator loop = getFlagIterator(flags);
		String flaglist="";
		if (loop.hasNext())
		{
			while (loop.hasNext())
			{
				flaglist=flaglist+loop.next().toString();
				if (loop.hasNext())
				{
					flaglist=flaglist+",";
				}
			}
		}
		return flaglist;
	}
	
	/**
	 * Sets a flag on this object.
	 *
	 * If the flag is already set then a second copy of it will be set.
	 *
	 * @param flag  The flag to be set.
	 * @param	type	The type of flags to be used.
	 */
	public void setFlag(String flag, String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			setFlag(flag,flags);
		}
	}

	/**
	 * Removes a flag from this object.
	 *
	 * If the flag is not set then no action will be taken.
	 *
	 * @param flag  The flag to be removed.
	 * @param type	The type of flags to be used.
	 */
	public void removeFlag(String flag, String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			removeFlag(flag,flags);
		}
	}

	/**
	 * Sets a number of flags
	 *
	 * A helper method that takes a comma seperated list of flags and sets them.
	 *
	 * @param newflags The flags to be set.
	 * @param type	The type of flags to e used.
	 */
	public void setFlags(String newflags, String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			setFlags(newflags,flags);
		}
	}

	/**
	 * Checks if a flag is set on this object.
	 *
	 * @param flag  The flag to be checked.
	 * @param type	The type of flags to be used.
	 * @return  True if the flag is set on this object, False otherwise.
	 */
	public boolean checkFlag(String flag, String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			return checkFlag(flag,flags);
		}
		else
		{
			return false;
		}
	}

	/**
	 * Returns an iterator to the flags.
	 *
	 * @param		type	The type of flags to work from.
	 * @return  An iterator to access each of the flags.
	 */
	public ListIterator getFlagIterator(String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			return getFlagIterator(flags);
		}
		else
		{
			return null;
		}
	}

	/**
	 * Returns a comma separated list of the flags.
	 *
	 * @param	type	The type of flags to list.
	 * @return	The list of flags.
	 */
	public String getFlagList(String type)
	{
		List flags = getFlags(type);
		if (flags!=null)
		{
			return getFlagList(flags);
		}
		else
		{
			return "";
		}
	}

	/**
	 * Sets a flag on this object.
	 *
	 * If the flag is already set then a second copy of it will be set.
	 *
	 * @param flag  The flag to be set.
	 */
	public void setFlag(String flag)
	{
		setFlag(flag,flags);
	}

	/**
	 * Removes a flag from this object.
	 *
	 * If the flag is not set then no action will be taken.
	 *
	 * @param flag  The flag to be removed.
	 */
	public void removeFlag(String flag)
	{
		removeFlag(flag,flags);
	}

	/**
	 * Sets a number of flags
	 *
	 * A helper method that takes a comma seperated list of flags and sets them.
	 *
	 * @param newflags The flags to be set.
	 */
	public void setFlags(String newflags)
	{
		setFlags(newflags,flags);
	}

	/**
	 * Checks if a flag is set on this object.
	 *
	 * @param flag  The flag to be checked.
	 * @return  True if the flag is set on this object, False otherwise.
	 */
	public boolean checkFlag(String flag)
	{
		return checkFlag(flag,flags);
	}

	/**
	 * Returns an iterator to the flags.
	 *
	 * @return  An iterator to access each of the flags.
	 */
	public ListIterator getFlagIterator()
	{
		return getFlagIterator(flags);
	}

	/**
	 * Returns a comma separated list of the flags.
	 *
	 * @return	The list of flags.
	 */
	public String getFlagList()
	{
		return getFlagList(flags);
	}

	/**
	 * Returns the description that is to be stored on disk.
	 *
	 * Sometimes objects will provide their own description dynamically generated. This method
	 * stops those descriptions being stored on disk.
	 *
	 * @return  The description to be stored as a String
	 */
	public String getStoreableDescription()
	{
		return description;
	}

	/**
	 * Returns the description that can be viewed on the mud.
	 *
	 * This method can be overwritten to provide a dynamically made description.
	 *
	 * @return  The description as a String
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets a new description for this object.
	 *
	 * @param description The new description
	 */
	public void setDescription(String description)
	{
		this.description=description;
	}

	/**
	 * Returns the identifier for this object.
	 *
	 * The identifier returned here is only unique to the current scope of the object.
	 *
	 * @return  The identifier for this object
	 * @see #getWorldIdentifier()
	 */
	public String getIdentifier()
	{
		return identifier;
	}

	/**
	 * Sets a new identifier for this object.
	 *
	 * @param newid The new identifier for the object
	 */
	public void setIdentifier(String newid)
	{
		identifier=newid.toLowerCase();
	}

	/**
	 * Returns an identifier that is guaranteed to be unique over the whole mud.
	 *
	 * @return  The unique identifier.
	 */
	public String getWorldIdentifier()
	{
		return identifier;
	}

	/**
	 * Returns a list of objects that may affect this one.
	 *
	 * Used for specials and commands. This will return all the objects that should be
	 * checked for matching commands and specials.
	 *
	 * @param objects The list that will be filled with objects.
	 */
	public void findCodeableObjects(List objects)
	{
		objects.add(this);
	}

	/**
	 * Adds a new command to this object.
	 *
	 * Inserts the given command into the ordered list of commands on this object.
	 *
	 * @param newcommand  The command to be added.
	 */
	public void addCommand(BaseCommand newcommand)
	{
		ListIterator loop = commands.listIterator();
		BaseCommand next;
		if (loop.hasNext())
		{
			next=(BaseCommand)loop.next();
			loop.previous();
		}
		else
		{
			next=null;
		}
		while ((next!=null)&&(next.getName().compareToIgnoreCase(newcommand.getName())<0))
		{
			loop.next();
			if (loop.hasNext())
			{
				next=(BaseCommand)loop.next();
				loop.previous();
			}
			else
			{
				next=null;
			}
		}
		loop.add(newcommand);
	}

	/**
	 * Adds a new subroutine to the object.
	 *
	 * Inserts the new subroutine into the ordered list.
	 *
	 * @param newsub  The new subroutine.
	 */
	public void addSubroutine(Subroutine newsub)
	{
		ListIterator loop = subroutines.listIterator();
		Subroutine next;
		if (loop.hasNext())
		{
			next=(Subroutine)loop.next();
			loop.previous();
		}
		else
		{
			next=null;
		}
		while ((next!=null)&&(next.getName().compareToIgnoreCase(newsub.getName())<0))
		{
			loop.next();
			if (loop.hasNext())
			{
				next=(Subroutine)loop.next();
				loop.previous();
			}
			else
			{
				next=null;
			}
		}
		loop.add(newsub);
	}

	/**
	 * Adds a new special to the object.
	 *
	 * @param  newspecial  The new special to be added.
	 */
	public void addSpecial(Special newspecial)
	{
		specials.add(newspecial);
	}

	/**
	 * Runs specials of a particular type on this object.
	 *
	 * Will run all specials until one of them was succesfull.
	 *
	 * @param type  The type of special to be run.
	 * @param variables The variables to be passed to the special.
	 * @return  Whether any of the specials were succesfull or not.
	 * @see com.esp.tawemud.tawescript.Special
	 */
	public boolean launchSpecial(int type, Variables variables)
	{
		boolean result=false;
		Iterator loop = specials.iterator();
		while ((!result)&&(loop.hasNext()))
		{
			Special thisone = (Special)loop.next();
			if (thisone.getType()==type)
			{
				variables.setVariable("$owner",getWorldIdentifier());
				result=thisone.run(server,variables);
			}
		}
		return result;
	}

	/**
	 * Adds all commands on this object that match the given name.
	 *
	 * Searches through the commands on this object for ones with a name
	 * beginning with the given text. These are then added to the list in the correct position.
	 * Also adds any commands that have the name &quot;*&quot;
	 *
	 * @param newlist The list of commands to add the new commands to.
	 * @param command The text used to run the command.
	 */
	public void addCommands(ListIterator newlist, String command)
	{
		ListIterator ourlist = commands.listIterator();
		BaseCommand thisone,next;
		if (newlist.hasNext())
		{
			next=(BaseCommand)newlist.next();
			newlist.previous();
		}
		else
		{
			next=null;
		}
		while (ourlist.hasNext())
		{
			thisone = (BaseCommand)ourlist.next();
			if ((thisone.getName().startsWith(command))||(thisone.getName().equals("*")))
			{
				if (next==null)
				{
					newlist.add(thisone);
				}
				else
				{
					while ((next!=null)&&(next.getName().compareToIgnoreCase(thisone.getName())<=0))
					{
						newlist.next();
						if (newlist.hasNext())
						{
							next=(BaseCommand)newlist.next();
							newlist.previous();
						}
						else
						{
							next=null;
						}
					}
					newlist.add(thisone);
				}
			}
		}
	}

	/**
	 * Returns an iterator to the specials on the object.
	 *
	 * @return  An iterator to the specials.
	 */
	public Iterator getSpecialsIterator()
	{
		return specials.iterator();
	}

	/**
	 * Returns an iterator to the commands on the object.
	 *
	 * @return  An iterator to the commands.
	 */
	public ListIterator getCommandsIterator()
	{
		return commands.listIterator();
	}

	/**
	 * Returns an iterator to the subroutines on the object.
	 *
	 * @return  An iterator to the subroutines.
	 */
	public ListIterator getSubroutinesIterator()
	{
		return subroutines.listIterator();
	}

	/**
	 * Finds a subroutine on this object matching the given name.
	 *
	 * The match is not case sensitive.
	 *
	 * @param name  The name to find.
	 * @return  A subroutine with the given name or null is none were found.
	 */
	public Subroutine findSubroutine(String name)
	{
		Iterator loop = getSubroutinesIterator();
		Subroutine result = null;
		while (loop.hasNext())
		{
			Subroutine thissub = (Subroutine)loop.next();
			if (thissub.getName().equalsIgnoreCase(name))
			{
				result=thissub;
			}
		}
		return result;
	}

	/**
	 * Resets the object to an initial state.
	 *
	 * Will also launch any reset specials.
	 */
	public void reset()
	{
		flags = new LinkedList(startflags);
		variables.clear();
		launchSpecial(Special.ST_RESET,new Variables());
	}

	/**
	 * Parses an xml element to attempt to initialise the object with the information within it.
	 *
	 * @param child The element containing the information
	 * @param text  The text within the element
	 * @param out A PrintWriter for outputing to a log.
	 * @return  True if the elements information was used, False otherwise.
	 */
	public boolean parseSubElement(Element child, String text, PrintWriter out)
	{
		if (child.getTagName().equals("Flags"))
		{
			setFlags(text);
			return true;
		}
		else if (child.getTagName().equals("StartFlags"))
		{
			setFlags(text,startflags);
			return true;
		}
		else if (child.getTagName().equals("Variable"))
		{
			setVariable(child.getAttribute("name"),text);
			return true;
		}
		else if (child.getTagName().equals("Description"))
		{
			description=text;
			return true;
		}
		else if (child.getTagName().equals("Command"))
		{
			BaseCommand newcommand;
			String classname = child.getAttribute("class");
			if ((classname!=null)&&(classname.length()>0))
			{
				try
				{
					Class commandclass = Class.forName(TaweServer.PACKAGE+".tawescript.BaseCommand");
					Class thisclass = Class.forName(classname);
					if (commandclass.isAssignableFrom(thisclass))
					{
						Constructor maker = thisclass.getConstructor(new Class[] {Class.forName(TaweServer.PACKAGE+".CodeableObject")});
						newcommand = (BaseCommand)maker.newInstance(new Object[] {this});
					}
					else
					{
						newcommand=null;
						out.println("Error creating command with class"+classname+" - wrong type");
					}
				}
				catch (Exception e)
				{
					out.println("Error creating command with class"+classname);
					e.printStackTrace(out);
					newcommand=null;
				}
			}
			else
			{
				newcommand = new Command(this);
			}
			if (newcommand!=null)
			{
				newcommand.parseElement(child);
				addCommand(newcommand);
				return true;
			}
			else
			{
				return false;
			}
		}
		else if (child.getTagName().equals("NLCommand"))
		{
			BaseCommand newcommand = new NLCommand(this);
			newcommand.parseElement(child);
			addCommand(newcommand);
			return true;
		}
		else if (child.getTagName().equals("Special"))
		{
			Special newspecial = new Special(this);
			newspecial.parseElement(child);
			addSpecial(newspecial);
			return true;
		}
		else if (child.getTagName().equals("Subroutine"))
		{
			Subroutine newsub = new Subroutine(this);
			newsub.parseElement(child);
			addSubroutine(newsub);
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Initialises the object from the given xml element.
	 *
	 * @param node  The element to load information from.
	 * @param out A PrintWriter for outputting log information.
	 */
	public void parseElement(Element node, PrintWriter out)
	{
		Class thisclass = getClass();
		Method[] methods = thisclass.getMethods();
		int loop=0;
		String attr;
		while (loop<methods.length)
		{
			if ((methods[loop].getParameterTypes().length==1)&&(methods[loop].getName().startsWith("set")))
			{
				String attrname = methods[loop].getName().substring(3).toLowerCase();
				if (node.hasAttribute(attrname))
				{
					if (!doSet(methods[loop],node.getAttribute(attrname)))
					{
						unset.put(methods[loop],node.getAttribute(attrname));
					}
				}
			}
			loop++;
		}
		Node child = node.getFirstChild();
		String text;
		Element thisone;
		while (child!=null)
		{
			if (child.getNodeType()==Node.ELEMENT_NODE)
			{
				if ((child.getFirstChild()!=null)&&(child.getFirstChild().getNodeType()==Node.TEXT_NODE))
				{
					text=child.getFirstChild().getNodeValue();
				}
				else
				{
					text="";
				}
				parseSubElement((Element)child,text,out);
			}
			child=child.getNextSibling();
		}
	}

	/**
	 * Returns an xml element to fully represent this object.
	 *
	 * @param builder An xml Document for building the element
	 * @return  The object serialized as an xml element
	 */
	public final Element getElement(Document builder)
	{
		Element node = builder.createElement(getType());
		storeInElement(builder,node);
		return node;
	}

	/**
	 * Stores information about this object into an element.
	 *
	 * @param builder An xml Document for generating the xml structures.
	 * @param node  The element to store information into.
	 */
	public void storeInElement(Document builder, Element node)
	{
		node.setAttribute("identifier",identifier);
		String flags = getFlagList(this.flags);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("Flags");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		flags = getFlagList(startflags);
		if (flags.length()>0)
		{
			Element flag = builder.createElement("StartFlags");
			flag.appendChild(builder.createTextNode(flags));
			node.appendChild(flag);
		}
		Iterator loop=variables.keySet().iterator();
		while (loop.hasNext())
		{
			String name=loop.next().toString();
			Element variable = builder.createElement("Variable");
			variable.setAttribute("name",name);
			variable.appendChild(builder.createTextNode(variables.get(name).toString()));
			node.appendChild(variable);
		}
		if (description.length()>0)
		{
			Element desc = builder.createElement("Description");
			desc.appendChild(builder.createTextNode(getStoreableDescription()));
			node.appendChild(desc);
		}
		loop = getSubroutinesIterator();
		while (loop.hasNext())
		{
			node.appendChild(((Subroutine)loop.next()).getElement(builder));
		}
		loop = getCommandsIterator();
		while (loop.hasNext())
		{
			node.appendChild(((BaseCommand)loop.next()).getElement(builder));
		}
		loop = getSpecialsIterator();
		while (loop.hasNext())
		{
			node.appendChild(((Special)loop.next()).getElement(builder));
		}
	}

	/**
	 * Performs operations that must happem continuosley on the object.
	 */
	public void cycle()
	{
		Special thisone;
		for (int loop=0; loop<specials.size(); loop++)
		{
			thisone=(Special)specials.get(loop);
			if (thisone.getType()==Special.ST_AUTORUN)
			{
				if ((thisone.getNextTime()>=0)&&(thisone.getNextTime()<=System.currentTimeMillis()))
				{
					Variables variables = new Variables();
					variables.setVariable("$owner",getWorldIdentifier());
					thisone.run(server,variables);
				}
			}
		}
	}

	/**
	 * Returns this object if it is a World, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public World asWorld()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Zone, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Zone asZone()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Item, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Item asItem()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Board, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Board asBoard()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Food, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Food asFood()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Drink, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Drink asDrink()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Door, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Door asDoor()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Container, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Container asContainer()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Mobile, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Mobile asMobile()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Player, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Player asPlayer()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Room, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Room asRoom()
	{
		return null;
	}

	/**
	 * Returns this object if it is a CloseableContainer, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public CloseableContainer asCloseableContainer()
	{
		return null;
	}

	/**
	 * Returns this object if it is a Weapon, or null if it is not.
	 *
	 * @return  This object or null
	 */
	public Weapon asWeapon()
	{
		return null;
	}
}
