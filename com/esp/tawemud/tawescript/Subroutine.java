package com.esp.tawemud.tawescript;

import com.esp.tawemud.Message;
import com.esp.tawemud.TaweServer;
import com.esp.tawemud.CodeableObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Subroutine extends GroupAction
{
	public String name;
	public String inputs="";
	public String outputs="";
	public String version="0.00";
	private CodeableObject owner;

	public Subroutine(CodeableObject owner)
	{
		super();
		name="";
		inputs="";
		outputs="";
		this.owner=owner;
		version="0.00";
	}

	public Element getElement(Document builder)
	{
		Element node = super.getElement(builder);
		node.setAttribute("name",name);
		node.setAttribute("inputs",inputs);
		node.setAttribute("outputs",outputs);
		node.setAttribute("version",version);
		return node;
	}

	public CodeableObject getOwner()
	{
		return owner;
	}

	public String getInputs()
	{
		return inputs;
	}

	public String getOutputs()
	{
		return outputs;
	}

	public String getName()
	{
		return name;
	}

	public boolean run(TaweServer server, Variables variables)
	{
		return super.run(server,variables);
	}
}
