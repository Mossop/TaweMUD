package com.esp.tawemud.xml;

import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.XMLReader;
import java.io.PrintWriter;

public class BaseHandler implements ContentHandler, ErrorHandler
{
	protected Locator locator;
	private BaseHandler previous;
	private XMLReader parser;
	protected PrintWriter out;

	public BaseHandler(PrintWriter newout)
	{
		locator=null;
		out=newout;
		previous=null;
		parser=null;
	}

	//Error handling functions

	private void logError(String type, SAXParseException err)
	{
		out.println(type+" at line "+err.getLineNumber()+" of "+err.getSystemId()+"@/");
		out.println(err.getMessage()+"@/@/");
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

	//Document handling functions

	public void setDocumentLocator(Locator locator)
	{
		this.locator=locator;
	}

	public void startDocument() throws SAXException
	{
	}

	public void endDocument() throws SAXException
	{
	}

	public void startElement(String name, Attributes attrs) throws SAXException
	{
		registerHandler(new BaseHandler(out));
	}

	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException
	{
		startElement(localName,atts);
	}

	public void endElement(String name) throws SAXException
	{
		if (previous!=null)
		{
			parser.setContentHandler(previous);
			parser.setErrorHandler(previous);
		}
	}

	public void endElement(String namespaceURI, String localName, String qName) throws SAXException
	{
		endElement(localName);
	}

	public void startPrefixMapping(String prefix, String uri) throws SAXException
	{
	}

	public void endPrefixMapping(String prefix) throws SAXException
	{
	}

	public void characters(char[] ch, int start, int length) throws SAXException
	{
	}

	public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException
	{
	}

	public void processingInstruction(String target, String data) throws SAXException
	{
	}

	public void skippedEntity(String name)
	{
	}

	// Parsing handling stuff

	protected void registerHandler(BaseHandler newhandler)
	{
		newhandler.plugin(this,parser);
	}

	public void plugin(BaseHandler previous, XMLReader newparser)
	{
		parser=newparser;
		this.previous=previous;
		parser.setContentHandler(this);
		parser.setErrorHandler(this);
	}
}
