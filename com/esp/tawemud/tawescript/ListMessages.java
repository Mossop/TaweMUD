package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;

/**
 * Gets a list of messages from a mailbox.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class ListMessages extends TestAction
{
	/**
	 * The item with the messages.
	 *
	 * @required
	 */
	public String item;
	/**
	 * A variable for the list.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject rlitem = variables.getObject(item,server);
		if ((rlitem!=null)&&(rlitem.asItem()!=null))
		{
			MailHandler mh = rlitem.asItem().getMailHandler();
			if (mh!=null)
			{
				variables.setVariable(dest,mh.getMessages());
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
}