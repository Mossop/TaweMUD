package com.esp.tawemud;

import com.sun.javadoc.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * A javadoc doclet for generating script dtd and help pages.
 *
 * @author  Dave Townsend
 * @version 1.5
 */
public class ScriptDoclet
{
	/**
	 * The file to store the dtd in.
	 */
	private File dtdfile = null;
	/**
	 * The directory to store the manual in.
	 */
	private File manualdir = null;

	/**
	 * The ClassDoc for the com.esp.tawemud.tawescript.ContentAction class.
	 */
	private ClassDoc contentclass;
	/**
	 * The ClassDoc for the com.esp.tawemud.tawescript.SingleAction class.
	 */
	private ClassDoc singleclass;
	/**
	 * The ClassDoc for the com.esp.tawemud.tawescript.TestAction class.
	 */
	private ClassDoc testclass;
	/**
	 * The ClassDoc for the com.esp.tawemud.tawescript.GroupAction class.
	 */
	private ClassDoc groupclass;

	/**
	 * Works out paths from the options given.
	 *
	 * @param options The javadoc options
	 */
	public ScriptDoclet(String[][] options)
	{
		for (int loop=0; loop<options.length; loop++)
		{
			if (options[loop][0].equals("-dtd"))
			{
				dtdfile = new File(options[loop][1]);
			}
			else if (options[loop][0].equals("-manual"))
			{
				manualdir = new File(options[loop][1]);
			}
		}
	}

	/**
	 * Creates a dtd for the scripting.
	 *
	 * @param classes The class information
	 */
	public boolean createDTD(List classes)
	{
		if (dtdfile!=null)
		{
			PrintWriter out;
			try
			{
				dtdfile.createNewFile();
				out = new PrintWriter(new FileWriter(dtdfile));
			}
			catch (Exception e)
			{
				System.err.println("Could not open the dtd - "+e.getMessage());
				return true;
			}
			System.out.println("Generating dtd...");
			Iterator loop = classes.iterator();
			StringBuffer scripts = new StringBuffer("<!ENTITY % scriptactions \"");
			while (loop.hasNext())
			{
				ClassDoc thisclass = (ClassDoc)loop.next();
				scripts.append(thisclass.name());
				if (loop.hasNext())
				{
					scripts.append('|');
				}
			}
			scripts.append("\">");
			out.println(scripts);
			out.println();
			out.println("<!ELEMENT Spec (#PCDATA)>");
			out.println("<!ATTLIST Spec type (mortal|wiz|dpower|power) \"mortal\">");
			out.println("<!ATTLIST Spec level CDATA \"0\">");
			out.println("<!ATTLIST Spec name CDATA #REQUIRED>");
			out.println();
			out.println("<!ELEMENT Code (%scriptactions;)*>");
			out.println();
			out.println("<!ELEMENT NLCommand (InfoPage?,Spec*,Code)>");
			out.println("<!ATTLIST NLCommand name CDATA #REQUIRED>");
			out.println("<!ATTLIST NLCommand version CDATA \"0.00\">");
			out.println();
			out.println("<!ELEMENT Command (InfoPage?,(%scriptactions;)*)>");
			out.println("<!ATTLIST Command name CDATA #REQUIRED>");
			out.println("<!ATTLIST Command class CDATA \"\">");
			out.println("<!ATTLIST Command version CDATA \"0.00\">");
			out.println("<!ATTLIST Command args CDATA \"10\">");
			out.println();
			out.println("<!ELEMENT CommandAlias EMPTY>");
			out.println("<!ATTLIST CommandAlias command CDATA #REQUIRED>");
			out.println("<!ATTLIST CommandAlias alias CDATA #REQUIRED>");
			out.println();
			out.println("<!ELEMENT Special (%scriptactions;)*>");
			out.println("<!ATTLIST Special identifier CDATA \"\">");
			out.println("<!ATTLIST Special version CDATA \"0.00\">");
			out.println("<!ATTLIST Special nexttime CDATA \"-1\">");
			out.println("<!ATTLIST Special type (unknown|autorun|enter|exit|reset|state|level) \"unknown\">");
			out.println();
			out.println("<!ELEMENT OnPass (%scriptactions;)*>");
			out.println();
			out.println("<!ELEMENT OnFail (%scriptactions;)*>");
			out.println("<!ELEMENT Subroutine (%scriptactions;)*>");
			out.println("<!ATTLIST Subroutine name CDATA #REQUIRED>");
			out.println("<!ATTLIST Subroutine inputs CDATA \"\">");
			out.println("<!ATTLIST Subroutine outputs CDATA \"\">");
			out.println("<!ATTLIST Subroutine version CDATA \"0.00\">");
			out.println();
			loop=classes.iterator();
			while (loop.hasNext())
			{
				ClassDoc thisclass = (ClassDoc)loop.next();
				out.print("<!ELEMENT "+thisclass.name()+" ");
				if (thisclass.subclassOf(contentclass))
				{
					out.print("(#PCDATA)");
				}
				else if (thisclass.subclassOf(singleclass))
				{
					out.print("EMPTY");
				}
				else if (thisclass.subclassOf(testclass))
				{
					out.print("(OnPass?,OnFail?)");
				}
				else if (thisclass.subclassOf(groupclass))
				{
					out.print("(%scriptactions;)*");
				}
				else
				{
					System.err.println("Could not get the type of "+thisclass.name());
				}
				out.println(">");
				FieldDoc[] fields = thisclass.fields();
				for (int fieldloop=0; fieldloop<fields.length; fieldloop++)
				{
					if (fields[fieldloop].isPublic())
					{
						out.print("<!ATTLIST "+thisclass.name()+" "+fields[fieldloop].name()+" ");
						if (fields[fieldloop].tags("required").length==0)
						{
							Tag[] tags = fields[fieldloop].tags("default");
							if (tags.length>0)
							{
								String def=tags[0].text();
								if ((def.length()>=2)&&(def.startsWith("\""))&&(def.endsWith("\"")))
								{
									def=def.substring(1,def.length()-1);
								}
								out.print("CDATA \""+def+"\"");
							}
							else
							{
								out.print("CDATA \"\"");
							}
						}
						else
						{
							out.print("CDATA #REQUIRED");
						}
						out.println(">");
					}
				}
				out.println();
			}
			out.flush();
			out.close();
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Formats a set of classes for the simple and detailed index.
	 *
	 * @param out The PrintWriter for the simple index
	 * @param listout The PrintWriter for the detailed index
	 * @param classes The classes to be included
	 * @param section The title of the section
	 */
	private void formatSection(PrintWriter out, PrintWriter listout, List classes, String section) throws Exception
	{
		out.println("    <H2>"+section+"</H2>");
		out.println("    <UL>");
		listout.println("      <TR><TD COLSPAN=\"2\"><H2>"+section+"</H2></TD></TR>");
		listout.println("    <UL>");
		Iterator loop=classes.iterator();
		while (loop.hasNext())
		{
			ClassDoc thisclass = (ClassDoc)loop.next();
			out.println("      <LI><A TARGET=\"mainpane\" HREF=\""+thisclass.name()+".html\">"+thisclass.name()+"</A></LI>");
			listout.println("        <TR><TD VALIGN=\"TOP\"><A TARGET=\"mainpane\" HREF=\""+thisclass.name()+".html\">"+thisclass.name()+"</A></TD><TD>"+thisclass.commentText()+"</TD></TR>");
		}
		out.println("    </UL>");
	}

	/**
	 * Creates a manual for the scripting language.
	 *
	 * @param classes the class information
	 */
	public boolean createManual(List classes)
	{
		if (manualdir!=null)
		{
			if (manualdir.isDirectory())
			{
				try
				{
					System.out.println("Generating manual...");
					PrintWriter out = new PrintWriter(new FileWriter(new File(manualdir,"index.html")));
					out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"\"http://www.w3.org/TR/REC-html40/loose.dtd>");
					out.println("<HTML>");
					out.println("  <HEAD>");
					out.println("    <TITLE>TaweMUD Scripting Manual</TITLE>");
					out.println("  </HEAD>");
					out.println("  <FRAMESET cols=\"20%,80%\">");
					out.println("    <FRAME src=\"sideindex.html\" name=\"indexpane\">");
					out.println("    <FRAME src=\"scriptlist.html\" name=\"mainpane\">");
					out.println("  </FRAMESET>");
					out.println("  <NOFRAMES>");
					out.println("    <BODY>");
					out.println("    </BODY>");
					out.println("  </NOFRAMES>");
					out.println("</HTML>");
					out.flush();
					out.close();
					List contentactions = new ArrayList();
					List singleactions = new ArrayList();
					List groupactions = new ArrayList();
					List testactions = new ArrayList();
					Iterator loop=classes.iterator();
					while (loop.hasNext())
					{
						ClassDoc thisclass = (ClassDoc)loop.next();
						if (thisclass.subclassOf(contentclass))
						{
							contentactions.add(thisclass);
						}
						else if (thisclass.subclassOf(singleclass))
						{
							singleactions.add(thisclass);
						}
						else if (thisclass.subclassOf(testclass))
						{
							testactions.add(thisclass);
						}
						else if (thisclass.subclassOf(groupclass))
						{
							groupactions.add(thisclass);
						}
						else
						{
							System.err.println("Could not get the type of "+thisclass.name());
						}
						out = new PrintWriter(new FileWriter(new File(manualdir,thisclass.name()+".html")));
						out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"\"http://www.w3.org/TR/REC-html40/loose.dtd>");
						out.println("<HTML>");
						out.println("  <HEAD>");
						out.println("    <TITLE>"+thisclass.name()+" Action</TITLE>");
						out.println("  </HEAD>");
						out.println("  <BODY>");
						out.println("    <H1>"+thisclass.name()+" Action</H1>");
						Tag[] version = thisclass.tags("version");
						if (version.length>0)
						{
							out.println("    <H3>Version "+version[0].text()+"</H3>");
						}
						out.println("    <P>"+thisclass.commentText()+"</P>");
						FieldDoc[] fields = thisclass.fields();
						int fieldcount=0;
						for (int fieldloop=0; fieldloop<fields.length; fieldloop++)
						{
							if (fields[fieldloop].isPublic())
							{
								fieldcount++;
							}
						}
						if (fieldcount>0)
						{
							out.println("    <P><B>Parameters:</B></P>");
							out.println("    <TABLE CELLPADDING=\"5\">");
							out.println("      <TR><TD><B>Name</B></TD><TD><B>Default</B></TD><TD><B>Description</B></TD></TR>");
							for (int fieldloop=0; fieldloop<fields.length; fieldloop++)
							{
								if (fields[fieldloop].isPublic())
								{
									out.println("      <TR>");
									out.println("        <TD>"+fields[fieldloop].name()+"</TD>");
									if (fields[fieldloop].tags("required").length==0)
									{
										Tag[] tags = fields[fieldloop].tags("default");
										if (tags.length>0)
										{
											out.println("        <TD ALIGN=\"CENTER\">"+tags[0].text()+"</TD>");
										}
										else
										{
											out.println("        <TD ALIGN=\"CENTER\">blank</TD>");
										}
									}
									else
									{
										out.println("        <TD ALIGN=\"CENTER\">required</TD>");
									}
									out.println("        <TD>"+fields[fieldloop].commentText()+"</TD>");
									out.println("      </TR>");
								}
							}
							out.println("    </TABLE>");
						}
						else
						{
							out.println("    <P><B>No Parameters</B></P>");
						}
						out.println("  </BODY>");
						out.println("</HTML>");
						out.flush();
						out.close();
					}
					out = new PrintWriter(new FileWriter(new File(manualdir,"sideindex.html")));
					PrintWriter listout = new PrintWriter(new FileWriter(new File(manualdir,"scriptlist.html")));
					out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"\"http://www.w3.org/TR/REC-html40/loose.dtd>");
					out.println("<HTML>");
					out.println("  <HEAD>");
					out.println("    <TITLE>Action List</TITLE>");
					out.println("  </HEAD>");
					out.println("  <BODY>");
					out.println("    <H2>Action List</H2>");
					listout.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"\"http://www.w3.org/TR/REC-html40/loose.dtd>");
					listout.println("<HTML>");
					listout.println("  <HEAD>");
					listout.println("    <TITLE>Action List</TITLE>");
					listout.println("  </HEAD>");
					listout.println("  <BODY>");
					listout.println("    <H2>Action List</H2>");
					listout.println("    <TABLE>");
					out.println("    <P><A TARGET=\"mainpane\" HREF=\"scriptlist.html\">Index</A></P>");
					formatSection(out,listout,singleactions,"Simple Actions");
					formatSection(out,listout,contentactions,"Text Actions");
					formatSection(out,listout,testactions,"Test Actions");
					formatSection(out,listout,groupactions,"Group Actions");
					out.println("  </BODY>");
					out.println("</HTML>");
					listout.println("    </TABLE>");
					listout.println("  </BODY>");
					listout.println("</HTML>");
					out.flush();
					out.close();
					listout.flush();
					listout.close();
				}
				catch (Exception e)
				{
					System.err.println("Error writing file - "+e.getMessage());
				}
			}
			else
			{
				System.err.println("Manual directory does not exist!");
			}
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Takes the class information, first sorts the classes into alphabetic order, removing
	 * those of no interest then calls createDTD() and createManual()
	 *
	 * @param scriptpackage The javadoc information for com.esp.tawemud.tawescript
	 */
	public void parseClasses(PackageDoc scriptpackage)
	{
		if (scriptpackage!=null)
		{
			List list = new ArrayList();
			ClassDoc[] classes = scriptpackage.ordinaryClasses();
			for (int loop=0; loop<classes.length; loop++)
			{
				if (classes[loop].name().equals("ContentAction"))
				{
					contentclass=classes[loop];
				}
				else if (classes[loop].name().equals("SingleAction"))
				{
					singleclass=classes[loop];
				}
				else if (classes[loop].name().equals("TestAction"))
				{
					testclass=classes[loop];
				}
				else if (classes[loop].name().equals("GroupAction"))
				{
					groupclass=classes[loop];
				}
				if (!classes[loop].isAbstract())
				{
					String name=classes[loop].name();
					if (!((name.equals("Command"))||(name.equals("Special"))||(name.equals("Subroutine"))||(name.equals("NLCommand"))||(name.equals("CommandAlias"))||(name.equals("GroupAction"))||(name.equals("Variables"))||(name.equals("Spec"))))
					{
						int pos=0;
						while ((pos<list.size())&&(((ClassDoc)list.get(pos)).name().compareTo(name)<0))
						{
							pos++;
						}
						list.add(pos,classes[loop]);
					}
				}
			}
			if (!((createDTD(list))|(createManual(list))))
			{
				System.err.println("Nothing to do!");
			}
		}
	}

	/**
	 * Runs the doclet with information about the classes.
	 *
	 * Creates an instance of the doclet and runs it with the package com.esp.tawemud.tawescript
	 *
	 * @param root  The javadoc information
	 * @return  True
	 */
	public static boolean start(RootDoc root)
	{
		(new ScriptDoclet(root.options())).parseClasses(root.packageNamed(TaweServer.PACKAGE+".tawescript"));
		return true;
	}

	/**
	 * Returns the number of parts for the options we want.
	 *
	 * @param option  The option to test
	 * @return  The number of parts to the specified option or 0 if it is unrecognised.
	 */
	public static int optionLength(String option)
	{
		if (option.equals("-dtd"))
		{
			return 2;
		}
		else if (option.equals("-manual"))
		{
			return 2;
		}
		else
		{
			return 0;
		}
	}
}
