package com.esp.tawemud;

import java.util.Vector;
import java.io.PrintWriter;
import org.w3c.dom.Element;

public interface ServerBase
{
	public boolean connectPlayer(String name, IOBase client);

	public boolean loadBasicWorld(PrintWriter out);
	
	public boolean loadFullWorld(PrintWriter out);
	
	public void addZone(Element node, PrintWriter out);
	
	public void setWorldURL(String url);
	
	public void startup(String log) throws Exception;

	public void sendWizMessage(String message, String noshow, int vis, int level, boolean dolog);
}

