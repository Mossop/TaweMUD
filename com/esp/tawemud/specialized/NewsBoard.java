package com.esp.tawemud.specialized;

import com.esp.tawemud.items.Board;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.PlayerIO;
import com.esp.tawemud.Mail;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

public class NewsBoard extends Board
{
	public NewsBoard(TaweServer server)
	{
		super(server);
	}

	public void storeInElement(Document builder, Element node)
	{
		super.storeInElement(builder,node);
		node.setAttribute("class",getClass().getName());
	}

	public String getMessages()
	{
		StringWriter buffer = new StringWriter();
		PrintWriter out = new PrintWriter(buffer);
		SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm dd/MM/yyyy");
		Mail thismail;
		for (int loop=getMailCount()-1; loop>=0; loop--)
		{
			thismail = getMail(loop);
			out.println("@+W"+dateformat.format(thismail.getDate())+" @+B"+thismail.getSubject()+"@*@/@/");
			out.println(thismail.getContent()+"@/@/");
			out.println("@+R"+thismail.getSender()+"@*@/@/");
		}
		return buffer.toString();
	}

	public String getDescription()
	{
		return super.getStoreableDescription()+"@/"+super.getMessages();
	}
}
