package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import java.util.Iterator;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.items.Item;
import com.esp.tawemud.PlayerIO;

public class Spec
{
	private String name;
	private int level;
	private String type;
	private String spectext;

	public Spec()
	{
		name="";
		level=0;
		type="mortal";
		spectext="";
	}

	public void setName(String value)
	{
		if (value!=null)
		{
			name=value;
		}
	}

	public void setLevel(String value)
	{
		if (value!=null)
		{
			try
			{
				level=Integer.parseInt(value);
			}
			catch (Exception e)
			{
			}
		}
	}

	public void setType(String value)
	{
		if (value!=null)
		{
			type=value;
		}
	}

	public void setSpecText(String value)
	{
		if (value!=null)
		{
			spectext=value;
		}
	}

	public Element getElement(Document builder)
	{
		Element node = builder.createElement("Spec");
		node.setAttribute("name",name);
		node.setAttribute("level",Integer.toString(level));
		node.setAttribute("type",type);
		node.appendChild(builder.createTextNode(spectext));
		return node;
	}

	public String getName()
	{
		return name;
	}

	private boolean findContainedItem(Mobile caller, String args, String spec, Variables vars, String varname, int pos)
	{
		//caller.displayText("findContainedItem");
		boolean found=false;
		String lcargs = args.toLowerCase();
		Iterator loop = caller.getItemContentsIterator();
		while ((!found)&&(loop.hasNext()))
		{
			Item thisone = (Item)loop.next();
			if ((caller.canSee(thisone))&&(thisone.asContainer()!=null))
			{
				Iterator nameloop = thisone.getNames().iterator();
				while ((!found)&&(nameloop.hasNext()))
				{
					String thisname = PlayerIO.stripColour(nameloop.next().toString()).toLowerCase();
					int index = lcargs.indexOf(thisname);
					if ((index>0)&&(lcargs.charAt(index-1)==' ')&&((lcargs.length()==(index+thisname.length()))||(lcargs.charAt(index+thisname.length())==' ')))
					{
						String newargs;
						String start=args.substring(0,index).toLowerCase();
						while (start.endsWith(" "))
						{
							start=start.substring(0,start.length()-1);
						}
						if (args.length()==(index+thisname.length()))
						{
							newargs="";
						}
						else
						{
							newargs=args.substring(index+thisname.length()+1);
						}
						while (newargs.startsWith(" "))
						{
							newargs=newargs.substring(1);
						}
						if (start.endsWith(" the"))
						{
							start=start.substring(0,start.length()-4);
						}
						if (start.endsWith(" from"))
						{
							start=start.substring(0,start.length()-5);
						}
						else if (start.endsWith(" inside"))
						{
							start=start.substring(0,start.length()-7);
						}
						else if (start.endsWith(" in"))
						{
							start=start.substring(0,start.length()-3);
						}
						while (start.endsWith(" "))
						{
							start=start.substring(0,start.length()-1);
						}
						Iterator subloop = thisone.asContainer().getItemContentsIterator();
						while ((!found)&&(subloop.hasNext()))
						{
							Item thissub = (Item)subloop.next();
							if ((thissub.hasName(start))||((start.startsWith("the "))&&(thissub.hasName(start.substring(4)))))
							{
								caller.setPronoun("it",thissub.getName());
								vars.setVariable("$"+varname,thissub);
								found=findNext(caller,newargs,spec,vars,pos+1);
							}
						}
					}
				}
			}
		}
		loop = caller.getLocation().getItemContentsIterator();
		while ((!found)&&(loop.hasNext()))
		{
			Item thisone = (Item)loop.next();
			if ((caller.canSee(thisone))&&(thisone.asContainer()!=null))
			{
				Iterator nameloop = thisone.getNames().iterator();
				while ((!found)&&(nameloop.hasNext()))
				{
					String thisname = PlayerIO.stripColour(nameloop.next().toString()).toLowerCase();
					int index = lcargs.indexOf(thisname);
					if ((index>0)&&(lcargs.charAt(index-1)==' ')&&((lcargs.length()==(index+thisname.length()))||(lcargs.charAt(index+thisname.length())==' ')))
					{
						String newargs;
						String start=args.substring(0,index).toLowerCase();
						while (start.endsWith(" "))
						{
							start=start.substring(0,start.length()-1);
						}
						if (args.length()==(index+thisname.length()))
						{
							newargs="";
						}
						else
						{
							newargs=args.substring(index+thisname.length()+1);
						}
						while (newargs.startsWith(" "))
						{
							newargs=newargs.substring(1);
						}
						if (start.endsWith(" the"))
						{
							start=start.substring(0,start.length()-4);
						}
						if (start.endsWith(" from"))
						{
							start=start.substring(0,start.length()-5);
						}
						else if (start.endsWith(" inside"))
						{
							start=start.substring(0,start.length()-7);
						}
						else if (start.endsWith(" in"))
						{
							start=start.substring(0,start.length()-3);
						}
						while (start.endsWith(" "))
						{
							start=start.substring(0,start.length()-1);
						}
						Iterator subloop = thisone.asContainer().getItemContentsIterator();
						while ((!found)&&(subloop.hasNext()))
						{
							Item thissub = (Item)subloop.next();
							if ((thissub.hasName(start))||((start.startsWith("the "))&&(thissub.hasName(start.substring(4)))))
							{
								caller.setPronoun("it",thissub.getName());
								vars.setVariable("$"+varname,thissub);
								found=findNext(caller,newargs,spec,vars,pos+1);
							}
						}
					}
				}
			}
		}
		if (!found)
		{
			return findItem(caller,args,spec,vars,varname,pos);
		}
		else
		{
			return found;
		}
	}

	private boolean findItem(Mobile caller, String args, String spec, Variables vars, String varname, int pos)
	{
		//caller.displayText("findItem");
		boolean found=false;
		String lcargs = args.toLowerCase();
		if ((lcargs.startsWith("it "))||(lcargs.equals("it")))
		{
			args=caller.getPronoun("it")+args.substring(2);
		}
		lcargs=args.toLowerCase();
		Iterator loop = caller.getItemContentsIterator();
		while ((!found)&&(loop.hasNext()))
		{
			Item thisone = (Item)loop.next();
			if (caller.canSee(thisone))
			{
				Iterator nameloop = thisone.getNames().iterator();
				while ((!found)&&(nameloop.hasNext()))
				{
					String thisname = PlayerIO.stripColour(nameloop.next().toString()).toLowerCase();
					if ((lcargs.startsWith(thisname+" "))||(lcargs.equals(thisname)))
					{
						String newargs;
						if (args.length()==thisname.length())
						{
							newargs="";
						}
						else
						{
							newargs=args.substring(thisname.length()+1);
						}
						while (newargs.startsWith(" "))
						{
							newargs=newargs.substring(1);
						}
						caller.setPronoun("it",thisone.getName());
						vars.setVariable("$"+varname,thisone);
						found=findNext(caller,newargs,spec,vars,pos+1);
					}
				}
			}
		}
		loop = caller.getLocation().getItemContentsIterator();
		while ((!found)&&(loop.hasNext()))
		{
			Item thisone = (Item)loop.next();
			if (caller.canSee(thisone))
			{
				Iterator nameloop = thisone.getNames().iterator();
				while ((!found)&&(nameloop.hasNext()))
				{
					String thisname = PlayerIO.stripColour(nameloop.next().toString()).toLowerCase();
					if ((lcargs.startsWith(thisname+" "))||(lcargs.equals(thisname)))
					{
						String newargs;
						if (args.length()==thisname.length())
						{
							newargs="";
						}
						else
						{
							newargs = args.substring(thisname.length()+1);
						}
						while (newargs.startsWith(" "))
						{
							newargs=newargs.substring(1);
						}
						caller.setPronoun("it",thisone.getName());
						vars.setVariable("$"+varname,thisone);
						found=findNext(caller,newargs,spec,vars,pos+1);
					}
				}
			}
		}
		return found;
	}

	private boolean findMobile(Mobile caller, String args, String spec, Variables vars, String varname, int pos)
	{
		//caller.displayText("findMobile");
		boolean found=false;
		String lcargs = args.toLowerCase();
		if ((lcargs.startsWith("me "))||(lcargs.equals("me")))
		{
			args=caller.getPronoun("me")+args.substring(2);
		}
		else if ((lcargs.startsWith("him "))||(lcargs.equals("him")))
		{
			args=caller.getPronoun("him")+args.substring(3);
		}
		else if ((lcargs.startsWith("her "))||(lcargs.equals("her")))
		{
			args=caller.getPronoun("her")+args.substring(3);
		}
		else if ((lcargs.startsWith("them "))||(lcargs.equals("them")))
		{
			args=caller.getPronoun("them")+args.substring(4);
		}
		lcargs = args.toLowerCase();
		Iterator loop = caller.getLocation().asRoom().getMobileContentsIterator();
		while ((!found)&&(loop.hasNext()))
		{
			Item thisone = (Item)loop.next();
			if (caller.canSee(thisone))
			{
				Iterator nameloop = thisone.getNames().iterator();
				while ((!found)&&(nameloop.hasNext()))
				{
					String thisname = PlayerIO.stripColour(nameloop.next().toString()).toLowerCase();
					if ((lcargs.startsWith(thisname+" "))||(lcargs.equals(thisname)))
					{
						String newargs;
						if (args.length()==thisname.length())
						{
							newargs="";
						}
						else
						{
							newargs=args.substring(thisname.length()+1);
						}
						while (newargs.startsWith(" "))
						{
							newargs=newargs.substring(1);
						}
						if (thisone.asMobile().getGender().equals("female"))
						{
							caller.setPronoun("her",thisone.getName());
						}
						else
						{
							caller.setPronoun("him",thisone.getName());
						}
						caller.setPronoun("them",thisone.getName());
						vars.setVariable("$"+varname,thisone);
						found=findNext(caller,newargs,spec,vars,pos+1);
					}
				}
			}
		}
		return found;
	}

	private boolean findText(Mobile caller, String args, String spec, Variables vars, String varname, int pos)
	{
		//caller.displayText("findText");
		if (args.length()==0)
		{
			return false;
		}
		else
		{
			int index = args.indexOf(' ');
			if (index<0)
			{
				vars.setVariable("$"+Integer.toString(pos),args);
				return findNext(caller,"",spec,vars,pos+1);
			}
			else
			{
				vars.setVariable("$"+varname,args.substring(0,index));
				args=args.substring(index+1);
				while (args.startsWith(" "))
				{
					args=args.substring(1);
				}
				return findNext(caller,args,spec,vars,pos+1);
			}
		}
	}

	private boolean findObject(Mobile caller, String args, String spec, Variables vars, String varname, int pos)
	{
		//caller.displayText("findObject");
		if (args.length()==0)
		{
			return false;
		}
		else
		{
			int index = args.indexOf(' ');
			String id;
			if (index<0)
			{
				id=args;
				args="";
			}
			else
			{
				id=args.substring(0,index);
				args=args.substring(index+1);
				while (args.startsWith(" "))
				{
					args=args.substring(1);
				}
			}
			CodeableObject obj = caller.getZone().getWorld().findCodeableObject(id);
			if (obj!=null)
			{
				vars.setVariable("$"+varname,obj);
				return findNext(caller,args,spec,vars,pos+1);
			}
			else
			{
				return false;
			}
		}
	}

	private boolean findFixedText(Mobile caller, String args, String spec, Variables vars, String param, int pos)
	{
		//caller.displayText("findFixedText "+param);
		String start;
		String nextargs;
		if (args.indexOf(' ')<0)
		{
			start=args;
			nextargs="";
		}
		else
		{
			start=args.substring(0,args.indexOf(' '));
			nextargs=args.substring(start.length()+1);
		}
		int index = param.indexOf(start.toLowerCase());
		boolean good=false;
		if (index>=0)
		{
			if ((index==0)||(param.charAt(index-1)=='/'))
			{
				if (((index+start.length())==param.length())||(param.charAt(index+start.length())=='/'))
				{
					while (nextargs.startsWith(" "))
					{
						nextargs=nextargs.substring(1);
					}
					good=findNext(caller,nextargs,spec,vars,pos);
				}
			}
		}
		if ((!good)&&(param.endsWith("/")))
		{
			while (args.startsWith(" "))
			{
				args=args.substring(1);
			}
			good=findNext(caller,args,spec,vars,pos);
		}
		return good;
	}

	private boolean findNext(Mobile caller, String args, String spec, Variables vars, int pos)
	{
		//caller.displayText("Scanning \""+args+"\" for \""+spec+"\"");
		if (spec.length()==0)
		{
			if (args.length()==0)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			int index = spec.indexOf(' ');
			String param;
			if (index>=0)
			{
				param=spec.substring(0,index);
				spec=spec.substring(index+1);
			}
			else
			{
				param=spec;
				spec="";
			}
			if ((param.startsWith("*"))&&(param.endsWith("*"))&&(param.length()>=2))
			{
				param=param.substring(1,param.length()-1);
				String varname;
				if (param.indexOf(",")>=0)
				{
					varname=param.substring(param.indexOf(",")+1);
					param=param.substring(0,param.indexOf(","));
				}
				else
				{
					varname=Integer.toString(pos);
				}
				if (param.equals("text"))
				{
					if (spec.length()==0)
					{
						vars.setVariable("$"+varname,args);
						return true;
					}
					else
					{
						return findText(caller,args,spec,vars,varname,pos);
					}
				}
				else if (param.equals("item"))
				{
					return findItem(caller,args,spec,vars,varname,pos);
				}
				else if (param.equals("contained"))
				{
					return findContainedItem(caller,args,spec,vars,varname,pos);
				}
				else if (param.equals("mobile"))
				{
					return findMobile(caller,args,spec,vars,varname,pos);
				}
				else if (param.equals("object"))
				{
					return findObject(caller,args,spec,vars,varname,pos);
				}
				else
				{
					return false;
				}
			}
			else
			{
				return findFixedText(caller,args,spec,vars,param,pos);
			}
		}
	}

	public boolean matches(Mobile caller, String args, Variables vars)
	{
		if ((type.equals("wiz"))&&(caller.asPlayer()!=null)&&(!caller.asPlayer().isWiz()))
		{
			return false;
		}
		else if ((type.equals("dpower"))&&(caller.asPlayer()!=null)&&(!caller.asPlayer().isDPower()))
		{
			return false;
		}
		else if ((type.equals("power"))&&(caller.asPlayer()!=null)&&(!caller.asPlayer().isPower()))
		{
			return false;
		}
		else if (caller.getLevel()<level)
		{
			return false;
		}
		else
		{
			return findNext(caller,args,spectext,vars,2);
		}
	}
}
