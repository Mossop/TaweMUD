package com.esp.tawemud;

/**
 * A generic interface for anything that has an identifier and a description.
 *
 * This interface is fairly redundant and will likely be removed soon.
 *
 * @author  Dave Townsend
 * @version 1.0
 */
public interface About
{
	/**
	 * Returns the description of this object.
	 *
	 * @return The description as a String
	 */
	public String getDescription();

	/**
	 * Sets a new description for this object.
	 *
	 * @param description The new description for the object.
	 */
	public void setDescription(String description);

	/**
	 * Returns the unique identifier for this object.
	 *
	 * Note that the indentifier will be unique within the current context.
	 *
	 * @return  The identifier of this object.
	 */
	public String getIdentifier();
}
