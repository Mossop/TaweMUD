package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Checks whether a flag is set on an item.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Check extends TestAction
{
	/**
	 * The item to check on. This may be any item or a zone.
	 */
	public String item;
	/**
	 * The flag to be checked.
	 *
	 * @required
	 */
	public String flag;
	/**
	 * The type of flag. This may be start, wear, quest, skill, startskill, spell or startspell. Anything else implies a standard flag.
	 *
	 * @default normal
	 */
	public String type = "normal";

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject thisitem = variables.getObject(item,server);
		String rltype=variables.parseString(type);
		if (thisitem!=null)
		{
			if (rltype.length()==0)
			{
				rltype="normal";
			}
			return thisitem.checkFlag(variables.parseString(flag),rltype);
		}
		else
		{
			return false;
		}
	}
}
