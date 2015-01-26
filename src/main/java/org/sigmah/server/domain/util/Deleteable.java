package org.sigmah.server.domain.util;

/**
 * Deleteable entities should implement this interface.
 * 
 * @author Alex Bertram
 */
public interface Deleteable {

	/**
	 * Marks this current element as deleted.<br/>
	 * The row is not removed from the database.
	 */
	void delete();

	/**
	 * Returns if the current element has been deleted.
	 * 
	 * @return {@code true} if the current element has been deleted, {@code false} otherwise.
	 */
	boolean isDeleted();

}
