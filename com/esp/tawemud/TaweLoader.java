package com.esp.tawemud;

import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarEntry;
import java.util.Enumeration;
import java.net.URL;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class TaweLoader extends ClassLoader
{
	private File classpath;
	private boolean jarfile;
		
  public TaweLoader(String classurl) throws MalformedURLException
  {
  	super();
  	classpath=new File((new URL(classurl)).getFile());
  	jarfile=!classpath.isDirectory();
  }

	public void preload(String base) throws ClassNotFoundException
	{
		if (base.endsWith("."))
		{
			base=base.substring(0,base.length()-1);
		}
		if (base.startsWith(TaweServer.PACKAGE))
		{
			if (jarfile)
			{
				String resource = base.replace('.', '/');
				try
				{
					JarFile file = new JarFile(classpath);
					Enumeration entries = file.entries();
					JarEntry entry;
					String classname;
					while (entries.hasMoreElements())
					{
						entry=(JarEntry)entries.nextElement();
						if ((!entry.isDirectory())&&(entry.getName().startsWith(resource)))
						{
							if (entry.getName().endsWith(".class"))
							{
								classname=entry.getName();
								classname=classname.substring(0,classname.length()-6);
								classname=classname.replace('/','.');
								try
								{
									loadClass(classname);
								}
								catch (ClassNotFoundException e)
								{
									throw new ClassNotFoundException("Couldnt load "+classname,e);
								}
							}
						}
					}
					file.close();
				}
				catch (IOException e)
				{
				}
			}
			else
			{
				String resource = base.replace('.', File.separatorChar);
				File fd = new File(classpath,resource);
				if (fd.isDirectory())
				{
					String classname;
					File[] files = fd.listFiles();
					for (int loop=0; loop<files.length; loop++)
					{
						if ((files[loop].isDirectory())&&(!files[loop].getName().startsWith(".")))
						{
							preload(base+"."+files[loop].getName());
						}
						else if (files[loop].getName().endsWith(".class"))
						{
							classname=files[loop].getName();
							classname=base+"."+classname.substring(0,classname.length()-6);
							try
							{
								loadClass(classname);
							}
							catch (ClassNotFoundException e)
							{
								throw new ClassNotFoundException("Could load "+classname,e);
							}
						}
					}
				}
			}
		}
	}
	
	public String findClassName(String name)
	{
		String resource = name.replace ('.', '/') + ".class";
		if (jarfile)
		{
			boolean found=false;
			String result="";
			try
			{
				JarFile file = new JarFile(classpath);
				Enumeration entries = file.entries();
				while ((!found)&&(entries.hasMoreElements()))
				{
					result = ((JarEntry)entries.nextElement()).getName();
					if (result.equalsIgnoreCase(resource))
					{
						found=true;
					}
				}
				file.close();
			}
			catch (Exception e)
			{
			}
			if (found)
			{
				return result.substring(0,result.length()-6).replace('/','.');
			}
			else
			{
				return name;
			}
		}
		else
		{
			String dir = resource.substring(0,resource.lastIndexOf("/"));
			String clname = resource.substring(resource.lastIndexOf("/")+1);
			File fd = new File(classpath,dir);
			boolean found=false;
			String result="";
			if (fd.isDirectory())
			{
				File[] files = fd.listFiles();
				int loop=0;
				while ((!found)&&(loop<files.length))
				{
					if ((!files[loop].isDirectory())&&(files[loop].getName().equalsIgnoreCase(clname)))
					{
						result=files[loop].getName();
						found=true;
					}
					loop++;
				}
			}
			if (found)
			{
				return dir.replace('/','.')+"."+result.substring(0,result.length()-6);
			}
			else
			{
				return name;
			}
		}
	}
	
  protected Class loadClass (String name, boolean resolve) throws ClassNotFoundException
  {
    // Since all support classes of loaded class use same class loader
    // must check subclass cache of classes for things like Object

  	Class c = findLoadedClass (name);
    if (c == null)
    {
    	// See if the bootstrap loader knows about this class
      
			try
      {
        c = findSystemClass (name);
      }
      catch (Exception e)
      {
      }
    }

    if (c == null)
    {
      // Convert class name argument to filename
      // Convert package names into subdirectories
      String resource = name.replace ('.', '/') + ".class";

      try
      {
        byte data[] = loadClassData(resource);
        c = defineClass (name, data, 0, data.length);
        if (c == null)
        {
        	throw new ClassNotFoundException (name);
        }
      }
      catch (IOException e)
      {
        throw new ClassNotFoundException ("Error reading file: "+resource);
      }
    }
    if (resolve)
    {
    	resolveClass (c);
    }
    return c;
  }

  private byte[] loadClassData (String resource) throws IOException
  {
		if (jarfile)
		{
			return loadJarClassData(resource);
		}
		else
		{
			return loadDirClassData(resource);
		}
	}
	
	private byte[] loadJarClassData(String resource) throws IOException
	{
		JarFile file = new JarFile(classpath);
		JarEntry entry = file.getJarEntry(resource);
		if (entry!=null)
		{
			byte[] buffer = new byte[(int)entry.getSize()];
			DataInputStream in = new DataInputStream(file.getInputStream(entry));
			in.readFully(buffer);
			in.close();
			file.close();
			return buffer;
		}
		else
		{
			throw new IOException("Could not find resource "+resource);
		}
	}
	
	private byte[] loadDirClassData(String resource) throws IOException
	{
		File fd = new File(classpath,resource);
		if (fd.canRead())
		{
			byte[] buffer = new byte[(int)fd.length()];
			DataInputStream in = new DataInputStream(new FileInputStream(fd));
			in.readFully(buffer);
			return buffer;
    }
    else
    {
    	throw new IOException("Could not read from class "+resource);
    }
  }
  
  public InputStream getResourceAsStream(String resource)
  {
  	if (jarfile)
  	{
  		try
  		{
				JarFile file = new JarFile(classpath);
				JarEntry entry = file.getJarEntry(resource);
				if (entry!=null)
				{
					return file.getInputStream(entry);
				}
				else
				{
					return super.getResourceAsStream(resource);
				}
  		}
  		catch (Exception e)
  		{
  			e.printStackTrace();
				return super.getResourceAsStream(resource);
  		}
  	}
  	else
  	{
  		try
  		{
				File fd = new File(classpath,resource);
				if (fd.canRead())
				{
					return new FileInputStream(fd);
		    }
		    else
		    {
					return super.getResourceAsStream(resource);
		   	}
			}
		  catch (Exception e)
		  {
				return super.getResourceAsStream(resource);
			}
  	}
  }
}
