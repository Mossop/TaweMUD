package com.esp.tawemud;

import java.net.Socket;
import java.io.IOException;

/**
 * Guides the player through the login process.
 *
 * Runs as a thread in its own right, then hands the player over to the server if
 * they log in successfully.
 *
 * @author  Dave Townsend
 * @version 1.5
 */
public class LoginHandler implements Runnable
{
	/**
	 * The players client.
	 */
	private IOBase client;
	/**
	 * The server.
	 */
	private TaweServer server;

	/**
	 * Create a new LoginHandler.
	 *
	 * Sets up the class and starts its thread running.
	 *
	 * @param newclient The new player's client
	 * @param ourserver The server
	 */
	public LoginHandler(Socket newclient, TaweServer ourserver)
	{
		try
		{
			server=ourserver;
			client = new PlayerIO(newclient,true,ourserver);
			(new Thread(this,"LoginHandler")).start();
		}
		catch (Exception e)
		{
			ourserver.logCrash(e);
			try
			{
				newclient.close();
			}
			catch (Exception f)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Handles the login process.
	 */
	public void run()
	{
		try
		{
			client.setEchoing(true);
			client.println("\u001B[2J\u001B[H\u001B[7h"+server.getConnectMessage()+"@*");
			client.println("");
			client.printPrompt("What do you wish to be called? ");
			client.flush();
			String name=null;
			name=client.readLine();
			if ((name==null)||(!server.userExists(name)))
			{
				client.println("Sorry, this mud is only open to powers at the moment.");
				client.close();
			}
			if ((!client.isClosing())&&(name!=null))
			{
				if (server.isBanned(name))
				{
					client.println("You're character has been temporarily banned from the game.");
					client.println("Please email a power to be allowed access again.");
					client.close();
				}
				else
				{
					String password;
					client.setEchoing(false);
					do
					{
						client.printPrompt("Enter your password: ");
						client.flush();
						password = client.readLine();
						client.println("");
						if ((password!=null)&&(!server.authenticate(name,password)))
						{
							client.println("Sorry, that password is incorrect.");
							server.sendWizMessage("Bad password from "+client.getSocket().getInetAddress()+" for "+name,"",0,2,true);
						}
					} while ((password!=null)&&(!server.authenticate(name,password)));
					if (password!=null)
					{
						client.setEchoing(true);
						server.connectPlayer(name,client);
					}
				}
			}
		}
		catch (Exception e)
		{
			server.logCrash(e);
		}
	}
}

