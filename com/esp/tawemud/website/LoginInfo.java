package com.esp.tawemud.website;

import com.esp.tawemud.plugins.RmiPluginInterface;
import java.rmi.Naming;

public class LoginInfo
{
	private String name;
	private short level;
	private boolean loggedin;
	private boolean wiz;
	private boolean dpower;
	private boolean power;
	
	public LoginInfo()
	{
		name="";
		loggedin=false;
		level=0;
		wiz=false;
		dpower=false;
		power=false;
	}
	
	public String getName()
	{
		return name;
	}
	
	public short getLevel()
	{
		return level;
	}
	
	public boolean isWiz()
	{
		return wiz;
	}
	
	public boolean isDPower()
	{
		return dpower;
	}
	
	public boolean isPower()
	{
		return power;
	}
	
	public boolean isLoggedIn()
	{
		return loggedin;
	}
	
	public void doLogout()
	{
		name="";
		loggedin=false;
		level=0;
		wiz=false;
		dpower=false;
		power=false;
	}
	
	public boolean doLogin(String name, String password) throws Exception
	{
		RmiPluginInterface rmi = (RmiPluginInterface)Naming.lookup("///TaweMudRmi");
		if (rmi!=null)
		{
			loggedin=rmi.authenticate(name,password);
			if (loggedin)
			{
				this.name=rmi.getName(name);
				level=rmi.getLevel(name);
				wiz=rmi.isWiz(name);
				dpower=rmi.isDPower(name);
				power=rmi.isPower(name);
			}
			return loggedin;
		}
		else
		{
			return false;
		}
	}
}
