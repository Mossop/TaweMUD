package com.esp.tawemud;

import java.net.Socket;

/**
 * The basic requirements for the I/O routines for player input.
 *
 * It is assumes that the actual class will buffer all output and input. flush() will
 * then send any pending output to the player, while hasData() can be continually checked
 * to see if anything exists in the input buffer. Additionally the class can be set to be
 * blocking, in which case an attempt to read a line will block until one is there or the
 * connection is lost.
 *
 * This interface only exists to allow code rebooting.
 *
 * @author  Dave Townsend
 * @version 1.5
 * @see com.esp.tawemud.TaweLoader
 */
public interface IOBase
{
	/**
	 * Sets the server we can use.
	 *
	 * @param server	The new server
	 */
	public void setServer(ServerBase server);

	/**
	 * Sets the name of our player.
	 *
	 * @param name	The players name
	 */
	public void setPlayer(String name);
	
	/**
	 * Returns terminal width.
	 *
	 * @return  The width of the players terminal
	 */
	public int getWidth();

	/**
	 * Returns terminal height.
	 *
	 * @return  The height of the players terminal
	 */
	public int getHeight();

	/**
	 * Returns a string description of the terminal type.
	 *
	 * @return  The short description of the terminal type
	 */
	public String getType();

	/**
	 * Returns connecting socket.
	 *
	 * @return  The socket the player is connecting with
	 */
	public Socket getSocket();

	/**
	 * Displays a prompt to the player.
	 *
	 * @param text  The prompt
	 */
	public void printPrompt(String text);

	/**
	 * Displays some text to the player.
	 *
	 * @param text  The text
	 */
	public void println(String text);

	/**
	 * Sets whether characters the player types should get echoed back to the player.
	 *
	 * @param value True to echo characters back, False not to.
	 */
	public void setEchoing(boolean value);

	/**
	 * Tests if there is a command waiting to be read.
	 *
	 * @return True if there is a command in the buffer.
	 */
	public boolean hasData();

	/**
	 * Flushes the internal buffer to the player.
	 */
	public void flush();

	/**
	 * Reads a line from the input buffer.
	 *
	 * If the class is set to be blocking then this method will block until a line
	 * is available, or the connection is closed.
	 *
	 * @return  A line of text, or null if the connection has closed or there is no line
	 * @return  waiting and the class is not blocking
	 */
	public String readLine();

	/**
	 * Returns whether the class is blocking or not.
	 *
	 * @return  True if the class is blocking, False otherwise
	 */
	public boolean isBlocking();

	/**
	 * Sets whether or not the class is blocking.
	 *
	 * @param isit  True to set the class to be blocking
	 */
	public void setBlocking(boolean isit);

	/**
	 * Checks if the connection is totally closed.
	 *
	 * @return  True if the connection is closed, False otherwise
	 */
	public boolean isClosed();

	/**
	 * Checks if the connection is about to close or is already closed.
	 *
	 * @return True if the connection is closing, false otherwise
	 */
	public boolean isClosing();

	/**
	 * Closes the connection.
	 */
	public void close();

	/**
	 * Attempt to keep the network connection.
	 */
	public void ping();
}
