package com.esp.tawemud.xml;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import java.io.PrintWriter;

public class SimpleErrorHandler implements ErrorHandler
{
	protected PrintWriter out;

	public SimpleErrorHandler(PrintWriter newout)
	{
		out=newout;
	}

	//Error handling functions

	private void logError(String type, SAXParseException err)
	{
		out.println(type+" at line "+err.getLineNumber()+" of "+err.getSystemId());
		out.println(err.getMessage());
	}

	public void warning(SAXParseException err) throws SAXException
	{
		logError("Warning",err);
	}

	public void error(SAXParseException err) throws SAXException
	{
		logError("Error",err);
		throw err;
	}

	public void fatalError(SAXParseException err) throws SAXException
	{
		logError("Fatal error",err);
		throw err;
	}
}
