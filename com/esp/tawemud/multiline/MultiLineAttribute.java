package com.esp.tawemud.multiline;

import com.esp.tawemud.CodeableObject;
import java.lang.reflect.Method;

public class MultiLineAttribute extends SimpleMultiLineHandler
{
	private CodeableObject item;
	private String attr;

	public MultiLineAttribute(CodeableObject item, String attribute)
	{
		super("");
		this.item=item;
		attr=attribute;
	}

	public String getFirstPrompt()
	{
		return "Type what you like. Use return only for seperating paragraphs since "
			+ "the mud will automatically word wrap whatever you do.@/When you are "
			+ "finished just do \"..\" on a line on its own.@/"
			+ "..abort on a line on its own will cancel this.@/";
	}

	public void complete()
	{
		String contents = getBuffer().toString();
		item.setValue(attr,contents);
	}
}
