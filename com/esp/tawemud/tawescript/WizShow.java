package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;

/**
 * Displays a message to all wizards logged in.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class WizShow extends ContentAction
{
	/**
	 * The visiblity to display at.
	 *
	 * @default 0
	 */
	public String visibility;
	/**
	 * The level to display at. (1 - wiz, 2 - dpower, 3 - power)
	 *
	 * @default 1
	 */
	public String level;
	/**
	 * Players not to see the text.
	 */
	public String noshow;
	/**
	 * Whether to log the message or not.
	 *
	 * @default false
	 */
	public String log;

	public boolean doAction(TaweServer server, Variables variables)
	{
		int vis = 0;
		try
		{
			vis = Integer.parseInt(variables.parseString(visibility));
		}
		catch (Exception e)
		{
			vis=0;
		}
		int lev = 1;
		try
		{
			lev = Integer.parseInt(variables.parseString(level));
		}
		catch (Exception e)
		{
			lev=0;
		}
		String mess = variables.parseString(contents.toString());
		server.sendWizMessage(mess,variables.parseString(noshow),vis,lev);
		if ((log!=null)&&(log.equalsIgnoreCase("true")))
		{
			server.log(mess);
		}
		return false;
	}
}
