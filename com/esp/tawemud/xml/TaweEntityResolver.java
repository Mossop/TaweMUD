package com.esp.tawemud.xml;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import com.esp.tawemud.TaweServer;

public class TaweEntityResolver implements EntityResolver
{
	public InputSource resolveEntity(String publicid, String systemid)
	{
		if (publicid!=null)
		{
			if ((publicid.startsWith("-//TaweMUD//DTD TaweMUD "))&&(publicid.endsWith("//EN")))
			{
				publicid=publicid.substring(24,publicid.length()-4);
				String resource=TaweServer.PACKAGE.replace('.','/');
				resource=resource+"/"+publicid+".dtd";
				return new InputSource(getClass().getClassLoader().getResourceAsStream(resource));
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}
}
