package com.esp.tawemud;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.InterruptedIOException;

public class ServerListener extends Thread
{
	private ServerSocket listener;
	private TaweServer server;
	private boolean stopped;

	public ServerListener(TaweServer parent) throws IOException
	{
		super("Listener");
		server=parent;
		listener = new ServerSocket(1764);
		stopped=false;
		start();
	}

	private synchronized boolean getStopped()
	{
		return stopped;
	}

	private synchronized void setStopped()
	{
		stopped=true;
	}

	public void shutdown()
	{
		setStopped();
		while (isAlive())
		{
			try
			{
				sleep(0,100);
			}
			catch (Exception e)
			{
			}
		}
	}

	public void run()
	{
		try
		{
			listener.setSoTimeout(100);
			while (!getStopped())
			{
				try
				{
					Socket client = listener.accept();
					server.sendWizMessage("Connect from "+client.getInetAddress().toString(),"",0,2,true);
					LoginHandler lh = new LoginHandler(client,server);
				}
				catch (InterruptedIOException e)
				{
				}
			}
			listener.close();
		}
		catch (Exception e)
		{
			server.sendWizMessage("Listener has died","",0,2,true);
			server.logCrash(e);
		}
	}
}
