package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;

/**
 * Deletes a message from a mailbox or bulletin board.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class DeleteMessage extends TestAction
{
	/**
	 * The item to delete the message from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The message number to delete (1 is the first message)
	 *
	 * @required
	 */
	public String message;

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
					mh.removeMail(number);
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
