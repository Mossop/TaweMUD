package com.esp.tawemud;

/**
 * An interface for anything that can hold mails.
 *
 * Anything that can recieve a mail must do it through this interface.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public interface MailHandler
{
	/**
	 * Adds a mail message to the internal store.
	 *
	 * @param message the mail message
	 */
	public void addMail(Mail message);

	/**
	 * Retrieves a mail message from the store.
	 *
	 * @param message The message number (0 based)
	 * @return  The mail message, or null if it does not exist
	 */
	public Mail getMail(int message);

	/**
	 * Returns the number of mails in the store.
	 *
	 * @return  The number of mails
	 */
	public int getMailCount();

	/**
	 * Removes a mail from the store.
	 *
	 * @param message The message to be erased (0 based)
	 */
	public void removeMail(int message);

	/**
	 * Get a list of messages in the store for display purposes.
	 *
	 * @return  Text showing messages agains message numbers
	 */
	public String getMessages();
}
