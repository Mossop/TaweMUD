package com.esp.tawemud.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.DocumentType;
import java.io.Writer;
import java.io.IOException;
import org.apache.crimson.tree.XmlDocument;

public class XmlWriter
{
	public static void write(Document doc, Writer output) throws IOException
	{
		DocumentType dt = doc.getDoctype();
		XmlDocument xmldoc = new XmlDocument();
		Node copy = doc.getDocumentElement().cloneNode(true);
		if (dt!=null)
		{
			xmldoc.setDoctype(dt.getPublicId(),dt.getSystemId(),null);
		}
		xmldoc.changeNodeOwner(copy);
		xmldoc.appendChild(copy);
		xmldoc.write(output);
		output.flush();
	}
}
