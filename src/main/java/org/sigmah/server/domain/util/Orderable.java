package org.sigmah.server.domain.util;

/**
 * Orderable entities should implement this interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface Orderable {

	int getSortOrder();

	void setSortOrder(int sortOrder);

}
