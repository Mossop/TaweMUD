package com.esp.tawemud;

import com.esp.tawemud.plugins.RmiPluginInterface;
import java.rmi.RMISecurityManager;
import java.rmi.Naming;

/**
 * Controls the mud remotely via RMI.
 *
 * A useful class for cleanly shutting down the mud without having to log in.
 * Assumes that the RMI plugin has been loaded on the mud.
 *
 * @author  Dave Townsend
 * @version 1.0
 * @see com.esp.tawemud.plugins.RmiPlugin
 */
public class MudControl
{
	/**
	 * The interface to the mud.
	 */
	private RmiPluginInterface mud;

	/**
	 * Initialises the class and tries to connect to the mud.
	 */
	public MudControl()
	{
		try
		{
			System.setSecurityManager(new RMISecurityManager());
			mud=(RmiPluginInterface)Naming.lookup("///TaweMudRmi");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public boolean isConnected()
	{
		return (mud!=null);
	}

	/**
	 * Performs some action on the mud. Possible commands are codereboot, worldreboot, inforeboot,
	 * emotereboot, halt.
	 *
	 * @param command The command to perform
	 */
	public void doCommand(String command) throws Exception
	{
		if (isConnected())
		{
			if (command.equals("codereboot"))
			{
				mud.rebootCode();
				System.out.println("Code rebooted");
			}
			else if (command.equals("worldreboot"))
			{
				mud.rebootWorld();
				System.out.println("World rebooted");
			}
			else if (command.equals("inforeboot"))
			{
				mud.rebootInfo();
				System.out.println("Info rebooted");
			}
			else if (command.equals("emotereboot"))
			{
				mud.rebootEmotes();
				System.out.println("Emotes rebooted");
			}
			else if (command.equals("halt"))
			{
				mud.haltServer();
				System.out.println("Mud halted");
			}
		}
	}

	/**
	 * Called from the command line. An instance of this class is created and if
	 * a connection to the mud was succesfull then the commands present on the
	 * command line are run.
	 *
	 * @param args  The command line arguments.
	 */
	public static void main(String[] args)
	{
		if (args.length>0)
		{
			MudControl control = new MudControl();
			if (control.isConnected())
			{
				try
				{
					for (int loop=0; loop<args.length; loop++)
					{
						control.doCommand(args[loop]);
					}
				}
				catch (Exception e)
				{
					System.out.println(e.getMessage());
					e.printStackTrace();
				}
			}
			else
			{
				System.out.println("Could not connect to mud.");
			}
		}
		else
		{
			System.out.println("What do you want to do?");
		}
	}
}
