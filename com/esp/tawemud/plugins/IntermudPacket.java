package com.esp.tawemud.plugins;

import java.util.StringTokenizer;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Iterator;

public class IntermudPacket
{
	String type;
	String origin;
	String origuser;
	String destination;
	String destuser;
	List optionlist;

	public IntermudPacket(String type, String origin, String origuser, String destination, String destuser)
	{
		this.type=type;
		this.origin=origin;
		this.origuser=origuser;
		this.destination=destination;
		this.destuser=destuser;
		optionlist=new LinkedList();
	}

	public IntermudPacket(String options)
	{
		optionlist = parseList(new StringTokenizer(options,",",true));
		this.type=optionlist.get(0).toString();
		this.origin=optionlist.get(2).toString();
		this.origuser=optionlist.get(3).toString();
		this.destination=optionlist.get(4).toString();
		this.destuser=optionlist.get(5).toString();
		optionlist.remove(0);
		optionlist.remove(0);
		optionlist.remove(0);
		optionlist.remove(0);
		optionlist.remove(0);
		optionlist.remove(0);
	}

	public String displayString(String string)
	{
		String result="\""+string+"\"";
		return result;
	}

	public String displayList(List list)
	{
		String result="({";
		Iterator loop = list.iterator();
		try
		{
			while (loop.hasNext())
			{
				Object thisone=loop.next();
				if (Class.forName("java.util.List").isInstance(thisone))
				{
					result+=displayList((List)thisone)+",";
				}
				else if (Class.forName("java.util.Map").isInstance(thisone))
				{
					result+=displayMap((Map)thisone)+",";
				}
				else if (Class.forName("java.lang.String").isInstance(thisone))
				{
					result+=displayString((String)thisone)+",";
				}
				else if (Class.forName("java.lang.Integer").isInstance(thisone))
				{
					result+=thisone.toString()+",";
				}
			}
		}
		catch (ClassNotFoundException e)
		{
		}
		result+="})";
		return result;
	}

	public String displayMap(Map map)
	{
		String result="([";
		Iterator loop = map.keySet().iterator();
		try
		{
			while (loop.hasNext())
			{
				String key=loop.next().toString();
				result+="\""+key+"\":";
				Object thisone=map.get(key);
				if (Class.forName("java.util.List").isInstance(thisone))
				{
					result+=displayList((List)thisone)+",";
				}
				else if (Class.forName("java.util.Map").isInstance(thisone))
				{
					result+=displayMap((Map)thisone)+",";
				}
				else if (Class.forName("java.lang.String").isInstance(thisone))
				{
					result+="\""+thisone.toString()+"\",";
				}
				else if (Class.forName("java.lang.Integer").isInstance(thisone))
				{
					result+=thisone.toString()+",";
				}
			}
		}
		catch (ClassNotFoundException e)
		{
		}
		result+="])";
		return result;
	}

	public List parseList(StringTokenizer data)
	{
		List result = new LinkedList();
		boolean done=false;
		while ((data.hasMoreTokens())&&(!done))
		{
			String next = data.nextToken("(,\"}");
			if (next.equals("\""))
			{
				next=data.nextToken("\\\"");
				if (next.equals("\""))
				{
					next="";
				}
				else
				{
					String test=data.nextToken("\\\"");
					while (test.equals("\\")&&(!test.equals("\"")))
					{
						next=next+data.nextToken("\\\"");
						next=next+data.nextToken("\\\"");
						test=data.nextToken("\\\"");
					}
				}
				result.add(next);
			}
			else if (next.equals("("))
			{
				next=data.nextToken("{[");
				if (next.equals("{"))
				{
					result.add(parseList(data));
				}
				else
				{
					result.add(parseMap(data));
				}
			}
			else if (next.equals(","))
			{
			}
			else if (next.equals("}"))
			{
				data.nextToken(")");
				data.nextToken(",");
				done=true;
			}
			else
			{
				data.nextToken(",");
				result.add(next);
			}
		}
		return result;
	}

	public Map parseMap(StringTokenizer data)
	{
		Map result = new HashMap();
		boolean done=false;
		while ((data.hasMoreTokens())&&(!done))
		{
			String name = data.nextToken(",:\"]");
			if (name.equals("\""))
			{
				name=data.nextToken("\\\"");
				if (name.equals("\""))
				{
					name="";
				}
				else
				{
					String test=data.nextToken("\\\"");
					while ((test.equals("\\"))&&(!test.equals("\"")))
					{
						name=name+data.nextToken("\\\"");
						name=name+data.nextToken("\\\"");
						test=data.nextToken("\\\"");
					}
				}
			}
			else if (name.equals(","))
			{
				data.nextToken("]");
				data.nextToken(")");
				data.nextToken(",");
				done=true;
			}
			else if (name.equals("]"))
			{
				data.nextToken(")");
				data.nextToken(",");
				done=true;
			}
			if (!done)
			{
				data.nextToken(":");
				String next = data.nextToken("(\",]");
				if (next.equals("\""))
				{
					next=data.nextToken("\\\"");
					String test=data.nextToken("\\\"");
					while (test.equals("\\"))
					{
						next=next+data.nextToken("\\\"");
						next=next+data.nextToken("\\\"");
						test=data.nextToken("\\\"");
					}
					result.put(name,next);
				}
				else if (next.equals("("))
				{
					next=data.nextToken("{[");
					if (next.equals("{"))
					{
						result.put(name,parseList(data));
					}
					else
					{
						result.put(name,parseMap(data));
					}
				}
				else if (next.equals(","))
				{
				}
				else
				{
					data.nextToken(",");
					result.put(name,next);
				}
			}
		}
		return result;
	}

	public String getType()
	{
		return type;
	}

	public String getOrigin()
	{
		return origin;
	}

	public String getOriginUser()
	{
		return origuser;
	}

	public String getDestination()
	{
		return destination;
	}

	public String getDestinationUser()
	{
		return destuser;
	}

	public List getOptions()
	{
		return optionlist;
	}

	public ListIterator getOptionList()
	{
		return optionlist.listIterator();
	}

	public String toString()
	{
		LinkedList output = new LinkedList(optionlist);
		output.add(0,type);
		output.add(1,new Integer(5));
		output.add(2,origin);
		if (origuser.equals("0"))
		{
			output.add(3,new Integer(0));
		}
		else
		{
			output.add(3,origuser);
		}
		if (destination.equals("0"))
		{
			output.add(4,new Integer(0));
		}
		else
		{
			output.add(4,destination);
		}
		if (destuser.equals("0"))
		{
			output.add(5,new Integer(0));
		}
		else
		{
			output.add(5,destuser);
		}
		return displayList(output);
	}

	public static void main(String[] args)
	{
		IntermudPacket pckt = new IntermudPacket("\"startup-req-3\",5,\"TestTalker\",0,\"*gjs\",0,0,0,0,4242,0,0,\"Not Applicable\",\"Not Applicable\",\"JeamLand 1.2.1\",\"JL\",\"driver development\",\"dave.townsend@bigfoot.com\",([\"who\":1,\"finger\":1,\"locate\":1,\"tell\":1,\"channel\":1,]),([\"is_jeamland\":397,]),");
		System.out.println(pckt.getDestination());
	}
}
