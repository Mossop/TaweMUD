package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.Message;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * Gets information about attributes on the specified type.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class TypeInfo extends SingleAction
{
	/**
	 * The type to get information on.
	 *
	 * @required
	 */
	public String type;
	/**
	 * A variable to hold the information.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		try
		{
			StringWriter result = new StringWriter();
			PrintWriter out = new PrintWriter(result);
			Class found = server.getClassFromType(variables.parseString(type));
			out.println(found.getName().substring(TaweServer.PACKAGE.length()+1)+" type@/");
			if (!found.getSuperclass().getName().equals("java.lang.Object"))
			{
				out.println("Inherits from "+found.getSuperclass().getName().substring(TaweServer.PACKAGE.length()+1)+"@/");
			}
			out.println("@/Settable attributes:@/");
			Method[] methods = found.getMethods();
			for (int loop=0; loop<methods.length; loop++)
			{
				if (methods[loop].getName().startsWith("set"))
				{
					out.println("  "+methods[loop].getName().substring(3).toLowerCase()+"@/");
				}
			}
			out.println("@/Gettable attributes:@/");
			for (int loop=0; loop<methods.length; loop++)
			{
				if (methods[loop].getName().startsWith("get"))
				{
					out.println("  "+methods[loop].getName().substring(3).toLowerCase()+"@/");
				}
			}
			out.flush();
			variables.setVariable(dest,result.toString());
		}
		catch (Exception e)
		{
			variables.setVariable(dest,"No such type exists.");
			e.printStackTrace();
		}
		return false;
	}
}
