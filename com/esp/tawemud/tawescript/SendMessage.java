package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.MailHandler;
import com.esp.tawemud.Mail;
import com.esp.tawemud.multiline.MultiLineMail;

/**
 * Puts a message on a bulletin board or into a mailbox.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class SendMessage extends TestAction
{
	/**
	 * The player to write the message.
	 *
	 * @required
	 */
	public String player;
	/**
	 * The name to appear as the sender of the message.
	 *
	 * @required
	 */
	public String sender;
	/**
	 * The item to put the message onto.
	 */
	public String target;

	public boolean doTest(TaweServer server, Variables variables)
	{
		CodeableObject item = variables.getObject(target,server);
		if ((item!=null)&&(item.asItem()!=null))
		{
			MailHandler mh = item.asItem().getMailHandler();
			if (mh!=null)
			{
				CodeableObject rlplayer = variables.getObject(player,server);
				if ((rlplayer!=null)&&(rlplayer.asPlayer()!=null))
				{
					return rlplayer.asPlayer().setMultiLine(new MultiLineMail(mh,variables.parseString(sender)));
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