package org.sigmah.server.dao;

import java.util.Set;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Activity;

/**
 * DAO for the {@link org.sigmah.server.domain.Activity} domain object. Implemented automatically by proxy, see the
 * Activity class for query definitions.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ActivityDAO extends DAO<Activity, Integer> {

	Integer queryMaxSortOrder(final Integer databaseId);

	Set<Activity> getActivitiesByDatabaseId(final Integer databaseId);

}
