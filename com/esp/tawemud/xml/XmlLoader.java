package com.esp.tawemud.xml;

import org.xml.sax.XMLReader;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import org.w3c.dom.Document;
import java.io.IOException;
import java.io.PrintWriter;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.ParserConfigurationException;

public class XmlLoader
{
	public static Document parse(String systemuri, PrintWriter out) throws SAXException, IOException, ParserConfigurationException
	{
		return parse(new InputSource(systemuri),out,true);
	}

	public static Document parse(String systemuri, PrintWriter out, boolean validating) throws SAXException, IOException, ParserConfigurationException
	{
		return parse(new InputSource(systemuri),out,validating);
	}

	public static Document parse(InputSource input, PrintWriter out) throws SAXException, IOException, ParserConfigurationException
	{
		return parse(input,out,true);
	}

	public static Document parse(InputSource input, PrintWriter out, boolean validating) throws SAXException, IOException, ParserConfigurationException
	{
		try
		{
			DocumentBuilderFactory docbuilderf = DocumentBuilderFactory.newInstance();
			docbuilderf.setValidating(validating);
			DocumentBuilder db = docbuilderf.newDocumentBuilder();
			db.setErrorHandler(new SimpleErrorHandler(out));
			return db.parse(input);
		}
		catch (SAXException e)
		{
			return null;
		}
		catch (Exception e)
		{
			e.printStackTrace(out);
			return null;
		}
		/*SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setValidating(validating);
		XMLReader parser = spf.newSAXParser().getXMLReader();
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		BaseHandler handler = new ElementHandler(doc,doc,out);
		handler.plugin(null,parser);
		parser.parse(input);
		return doc;*/
	}
}
