package com.esp.tawemud;

import java.net.InetAddress;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Whois extends Thread
{
	private InetAddress address;
	private StringBuffer city;
	private StringBuffer country;

	public Whois()
	{
		super("Whois");
	}

	public void lookup(InetAddress address, StringBuffer city, StringBuffer country)
	{
		this.address=address;
		this.city=city;
		this.country=country;
		start();
	}

	public void run()
	{
		String last1="";
		String last2="";
		boolean found=false;
		String host = address.getHostName().toLowerCase();
		int dots=2;
		if ((host.endsWith(".com"))||(host.endsWith(".net"))||(host.endsWith(".edu"))||(host.endsWith(".org")))
		{
			dots=1;
		}
		int index=host.lastIndexOf('.');
		while (dots>0)
		{
			index=host.lastIndexOf('.',index-1);
			dots--;
		}
		host=host.substring(index+1);
		try
		{
			Socket socket = new Socket("whois.networksolutions.com",43);
			PrintStream out = new PrintStream(socket.getOutputStream());
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out.println(host);
			String data;
			do
			{
				data=in.readLine();
				if (data!=null)
				{
					if (data.indexOf("Registrant")>=0)
					{
						while ((data!=null)&&(data.length()>0))
						{
							last2=last1;
							last1=data;
							data=in.readLine();
						}
						while (last2.startsWith(" "))
						{
							last2=last2.substring(1);
						}
						while (last1.startsWith(" "))
						{
							last1=last1.substring(1);
						}
						if (last2.indexOf(",")>=0)
						{
							last2=last2.substring(0,last2.indexOf(","));
						}
						country.append(last1);
						city.append(last2);
						found=true;
					}
				}
			} while (data!=null);
			socket.close();
			if (!found)
			{
				socket = new Socket("whois.ripe.net",43);
				out = new PrintStream(socket.getOutputStream());
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out.println(address.getHostAddress());
				do
				{
					data=in.readLine();
					if (data!=null)
					{
						if (data.indexOf("country:")>=0)
						{
							found=true;
							data=data.substring(8);
							while (data.startsWith(" "))
							{
								data=data.substring(1);
							}
							country.append(data);
						}
					}
				} while ((data!=null)&&(!found));
			}
		}
		catch (Exception e)
		{
		}
	}
}
