package com.esp.tawemud.tawescript;

import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TravelMessages;
import com.esp.tawemud.TaweServer;

/**
 * Sets a travel message on a mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SetTravMess extends SingleAction
{
	/**
	 * The mobile.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The travel message to be set.
	 *
	 * @required
	 */
	public String mess;
	/**
	 * The value to set the message to.
	 *
	 * @required
	 */
	public String value;

	public boolean doAction(TaweServer server, Variables variables)
	{
		CodeableObject object = variables.getObject(item,server);
		if ((object!=null)&&(object.asMobile()!=null))
		{
			TravelMessages trav = object.asMobile().getTravelMessages();
			trav.setMessage(variables.parseString(mess),variables.parseString(value));
		}
		return false;
	}
}