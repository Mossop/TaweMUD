package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;
import java.text.SimpleDateFormat;

/**
 * Gets a message from a mailbox or a bulletin board.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class GetMessage extends TestAction
{
	/**
	 * The item to get the message from.
	 *
	 * @required
	 */
	public String item;
	/**
	 * The message to get (1 is the first)
	 *
	 * @required
	 */
	public String message;
	/**
	 * A variable to hold the message.
	 *
	 * @required
	 */
	public String dest;

	public boolean doTest(TaweServer server, Variables variables)
	{
		try
		{
			CodeableObject rlitem = variables.getObject(item,server);
			if ((rlitem!=null)&&(rlitem.asItem()!=null))
			{
				MailHandler mh = rlitem.asItem().getMailHandler();
				int mess = Integer.parseInt(variables.parseString(message));

				if (mh!=null)
				{
					Mail thismessage = mh.getMail(mess-1);
					if (thismessage!=null)
					{
						StringBuffer result = new StringBuffer();
						result.append("From: "+thismessage.getSender()+"@/");
						result.append("Subject: "+thismessage.getSubject()+"@/");
						SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
						result.append("Sent: "+dateformat.format(thismessage.getDate())+"@/@/");
						result.append(thismessage.getContent());
						variables.setVariable(dest,result.toString());
						thismessage.setRead(true);
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
		catch (NumberFormatException e)
		{
			return false;
		}
	}
}