package com.esp.tawemud.multiline;

public abstract class SimpleMultiLineHandler implements MultiLineHandler
{
	private String prompt;
	private boolean done;
	private StringBuffer buffer;

	public SimpleMultiLineHandler(String prompt)
	{
		this.prompt=prompt;
		done=false;
		buffer = new StringBuffer();
	}

	public String getFirstPrompt()
	{
		return prompt;
	}

	public String sendLine(String line)
	{
		if (line.equals(".."))
		{
			done=true;
			if (buffer.length()>0)
			{
				buffer.delete(buffer.length()-2,buffer.length());
			}
			complete();
			return "";
		}
		else if (line.equals("..abort"))
		{
			done=true;
			return "";
		}
		else
		{
			buffer.append(line+"\n@/");
			return prompt;
		}
	}

	protected abstract void complete();

	public boolean isFinished()
	{
		return done;
	}

	protected StringBuffer getBuffer()
	{
		return buffer;
	}
}