package org.sigmah.server.dao;

import java.util.Map;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Site;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.Site} domain object.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface SiteDAO extends DAO<Site, Integer> {

	/**
	 * Efficiently updates the {@link org.sigmah.server.domain.AttributeValue} of a given
	 * {@link org.sigmah.server.domain.Site}.
	 * 
	 * @param siteId
	 *          the id of the Site entity to update
	 * @param attributeValues
	 *          a map of attribute ids => attribute value
	 */
	void updateAttributeValues(int siteId, Map<Integer, Boolean> attributeValues);

}
