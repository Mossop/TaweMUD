package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.Zone;
import com.esp.tawemud.TaweServer;
import java.lang.reflect.Method;

/**
 * Sets the value of an attribute on an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Set extends TestAction
{
	/**
	 * The item to change.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The attribute to change.
	 *
	 * @required
	 */
	public String attribute;
	/**
	 * The new value to set.
	 *
	 * @required
	 */
	public String value;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject object = variables.getObject(item,server);
		if (object!=null)
		{
			String attr = variables.parseString(attribute);
			String value = variables.parseString(this.value);
			return object.setValue(attr,value);
		}
		else
		{
			return false;
		}
	}
}

