package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;

/**
 * Sets a pronoun on a mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetPronoun extends SingleAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String mobile;
	/**
	 * The items to appear in the pronouns list.
	 *
	 * @required
	 */
	public String item;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject thismobile = variables.getObject(mobile,server);
		CodeableObject thisitem = variables.getObject(item,server);
		if ((thismobile!=null)&&(thismobile.asMobile()!=null)&&(thisitem!=null))
		{
			if (thisitem.asMobile()!=null)
			{
				thismobile.asMobile().setPronoun("them",thisitem.asItem().getName());
				if (thisitem.asMobile().getGender().equals("male"))
				{
					thismobile.asMobile().setPronoun("him",thisitem.asItem().getName());
				}
				else
				{
					thismobile.asMobile().setPronoun("her",thisitem.asItem().getName());
				}
			}
			else if (thisitem.asItem()!=null)
			{
				thismobile.asMobile().setPronoun("it",thisitem.asItem().getName());
			}
		}
		return false;
	}
}
