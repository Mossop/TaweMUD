package com.esp.tawemud.multiline;

public interface MultiLineHandler
{
	public String getFirstPrompt();

	public String sendLine(String line);

  public boolean isFinished();
}
