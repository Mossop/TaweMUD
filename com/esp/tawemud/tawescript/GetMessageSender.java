package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;

/**
 * Find out who sent a message.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetMessageSender extends TestAction
{
	/**
	 * The item holding the message.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The message number (1 is the first)
	 *
	 * @required
	 */
	public String message;
	/**
	 * A variable to hold the senders <EM>name</EM>
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
				int number=Integer.parseInt(variables.parseString(message))-1;
				if (number<mh.getMailCount())
				{
					variables.setVariable(dest,mh.getMail(number).getSender());
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
		else
		{
			return false;
		}
	}
}
