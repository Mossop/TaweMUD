package com.esp.tawemud;

import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.File;
import java.util.StringTokenizer;

/**
 * This class is never instantiated. It is purely used to create a new instance of the mud and start
 * it running.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public class TaweMUD
{
	/**
	 * Starts a new copy of the mud process running.
	 *
	 * Parses any command line arguments and uses them and the classpath to determine where the
	 * world directory is.
	 *
	 * @param args  The command line arguments
	 */
	public static void main(String[] args)
	{
		String url = null;
		if (args.length==0)
		{
			StringTokenizer tokens = new StringTokenizer(System.getProperty("java.class.path"),File.pathSeparator);
			boolean found=false;
			while ((tokens.hasMoreTokens())&&(!found))
			{
				String thisone=tokens.nextToken();
				if (thisone.toLowerCase().endsWith("tawebase.jar"))
				{
					try
					{
						url=(new File(thisone)).getAbsoluteFile().getParentFile().toURL().toString();
						found=true;
					}
					catch (Exception e)
					{
					}
				}
			}
			if (found)
			{
				System.out.println("Guessing that the world directory is "+url);
			}
		}
		else
		{
			try
			{
				url=(new File(args[0])).toURL().toString();
			}
			catch (Exception e)
			{
			}
		}
		if (url!=null)
		{
			StringWriter buffer = new StringWriter();
			PrintWriter out = new PrintWriter(buffer);
			try
			{
				ClassLoader loader = new TaweLoader(url+"TaweMUD.jar");
				ServerBase newserver = (ServerBase)loader.loadClass(TaweServer.PACKAGE+".TaweServer").newInstance();
				newserver.setWorldURL(url);
				if (newserver.loadFullWorld(out))
				{
					System.out.println("Loaded world");
					out.flush();
					newserver.startup(buffer.toString());
					System.out.println("Server started");
				}
				else
				{
					System.out.println("Error loading basic world");
				}
			}
			catch (Exception e)
			{
				out.println("Error starting up - "+e.getMessage());
				e.printStackTrace(out);
			}
			out.flush();
			System.out.println(buffer.toString());
		}
		else
		{
			System.out.println("You must specify a world directory.");
		}
	}
}
