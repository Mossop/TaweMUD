package com.esp.tawemud.tawescript;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.esp.tawemud.CodeableObject;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Mobile;
import java.util.StringTokenizer;

public interface BaseCommand
{
	public String getName();
	
	public String getHelp(Mobile mobile);

	public void parseElement(Element node);

	public Element getElement(Document builder);

	public boolean callCommand(TaweServer server, Mobile caller, String found, String args);
}
