package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TravelMessages;
import com.esp.tawemud.TaweServer;

/**
 * Copies a travel message into a variable.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class CopyTravMess extends SingleAction
{
	/**
	 * The mobile to get the message from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The travel message to get.
	 *
	 * @required
	 */
	public String mess;
	/**
	 * The variable to store the message in.
	 *
	 * @required
	 */
	public String dest;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject object = variables.getObject(item,server);
		if ((object!=null)&&(object.asMobile()!=null))
		{
			String message = object.asMobile().getTravelMessages().getMessage(variables.parseString(mess));
			Variables travvars = new Variables();
			travvars.setVariable("%n",object.asMobile().getName());
			variables.setVariable(dest,variables.parseString(travvars.parseString(message)));
		}
		return false;
	}
}