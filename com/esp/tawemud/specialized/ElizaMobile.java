package com.esp.tawemud.specialized;

import com.esp.tawemud.items.Mobile;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;

public class ElizaMobile extends Mobile
{
	public ElizaMobile(TaweServer server)
	{
		super(server);
	}
	
	public void displayText(String text)
	{
		String rltext = PlayerIO.stripColour(text);
		if (rltext.startsWith("Mossop"))
		{
			if (rltext.indexOf('\'')>=0)
			{
				rltext=rltext.substring(rltext.indexOf('\'')+1);
				if (rltext.indexOf('\'')>=0)
				{
					rltext=rltext.substring(0,rltext.indexOf('\''));
				}
				server.parseCommand(this,"tell mossop "+rltext);
			}
			else
			{
				super.displayText(text);
			}
		}
		else
		{
			super.displayText(text);
		}
	}
}
