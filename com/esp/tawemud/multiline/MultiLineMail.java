package com.esp.tawemud.multiline;

import com.esp.tawemud.Mail;
import com.esp.tawemud.MailHandler;

public class MultiLineMail extends SimpleMultiLineHandler
{
	private MailHandler target;
	String subject;
	boolean gotsubject;
	String sender;

	public MultiLineMail(MailHandler target, String sender)
	{
		super("");
		gotsubject=false;
		subject="";
		this.target=target;
		this.sender=sender;
	}

	public String getFirstPrompt()
	{
		return "Type your message below. End the message by putting .. on a line on its own.@/"
			+ "To cancel the message put ..abort on a line on its own.@/"
			+ "@+WSubject:@* ";
	}

	public String sendLine(String line)
	{
		if (gotsubject)
		{
			return super.sendLine(line);
		}
		else
		{
			gotsubject=true;
			subject=line;
			return "";
		}
	}

	public void complete()
	{
		Mail message = new Mail();
		message.setContent(getBuffer());
		message.setSubject(new StringBuffer(subject));
		message.setSender(sender);
		target.addMail(message);
	}
}
