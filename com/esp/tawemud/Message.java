package com.esp.tawemud;

import com.esp.tawemud.items.Mobile;

/**
 * A class to hold a command string from a mobile.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class Message
{
	/**
	 * The mobile.
	 */
	private Mobile item;
	/**
	 * The command string.
	 */
	private String message;

	/**
	 * Creates a new Message.
	 *
	 * @param itemid  The mobile
	 * @param mess  The command string
	 */
	public Message(Mobile itemid, String mess)
	{
		item=itemid;
		message=mess;
	}

	/**
	 * Returns the command string.
	 *
	 * @return  The command string
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * Returns the mobile.
	 *
	 * @return  The mobile
	 */
	public Mobile getItem()
	{
		return item;
	}
}
