package com.esp.tawemud.items;

import java.io.PrintWriter;
import com.esp.tawemud.TaweServer;
import org.w3c.dom.Element;
import org.w3c.dom.Document;

public class CloseableContainer extends Container
{
	private String keytype;
	
	public CloseableContainer(TaweServer server)
	{
		super(server);
		keytype="key";
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		if (!keytype.equals("key"))
		{
			node.setAttribute("keytype",keytype);
		}
	}

	public void setKeyType(String value)
	{
		keytype=value;
	}
	
	public String getKeyType()
	{
		return keytype;
	}
	
	public CloseableContainer asCloseableContainer()
	{
		return this;
	}
}
