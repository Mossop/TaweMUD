package com.esp.tawemud.plugins;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.PrintWriter;
import com.esp.tawemud.Plugin;
import com.esp.tawemud.TaweServer;

public interface PluginInterface
{
	public boolean startup(TaweServer server, Plugin plugin);

	public void shutdown();

	public void storeInElement(Document builder, Element node);

	public void parseElement(Element node, PrintWriter out);
}
