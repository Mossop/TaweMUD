package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import java.lang.reflect.Method;

/**
 * Gets an attribute from an item, zone or world.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Get extends TestAction
{
	/**
	 * The item to get the information from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The attribute to get.
	 *
	 * @required
	 */
	public String attribute;
	/**
	 * The variable to hold the attributes value.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject object = variables.getObject(item,server);
		String attr = variables.parseString(attribute);
		boolean found = false;
		if (object!=null)
		{
			Class thisclass = object.getClass();
			Method[] methods = thisclass.getMethods();
			int loop=0;
			String mname;
			while ((!found)&&(loop<methods.length))
			{
				mname=methods[loop].getName();
				if (methods[loop].getName().equalsIgnoreCase("get"+attr))
				{
					found=true;
					try
					{
						Object result = methods[loop].invoke(object,new Object[0]);
						if (result!=null)
						{
							try
							{
								variables.setVariable(dest,(CodeableObject)result);
							}
							catch (Exception e)
							{
								variables.setVariable(dest,result.toString());
							}
						}
						else
						{
							variables.setVariable(dest,"");
						}
					}
					catch (Exception e)
					{
						found=false;
					}
				}
				loop++;
			}
		}
		return found;
	}
}
