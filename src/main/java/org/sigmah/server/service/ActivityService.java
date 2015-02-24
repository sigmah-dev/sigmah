package org.sigmah.server.service;

import java.util.Date;

import org.sigmah.server.dao.ActivityDAO;
import org.sigmah.server.dao.LocationTypeDAO;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.LocationType;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.LocationTypeDTO;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.security.UnauthorizedAccessException;

/**
 * {@link Activity} corresponding service implementation.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ActivityService extends AbstractEntityService<Activity, Integer, ActivityDTO> {

	private final ActivityDAO activityDAO;
	private final UserDatabaseDAO databaseDAO;
	private final LocationTypeDAO locationTypeDAO;

	@Inject
	public ActivityService(ActivityDAO activityDAO, UserDatabaseDAO databaseDAO, LocationTypeDAO locationTypeDAO) {
		this.activityDAO = activityDAO;
		this.databaseDAO = databaseDAO;
		this.locationTypeDAO = locationTypeDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Activity create(final PropertyMap properties, final UserExecutionContext context) throws UnauthorizedAccessException {

		final UserDatabase database = getDatabase(properties);
		assertDesignPrivileges(context.getUser(), database);

		// Create the entity.
		final Activity activity = new Activity();
		activity.setDatabase(database);
		activity.setSortOrder(calculateNextSortOrderIndex(database.getId()));
		activity.setLocationType(getLocationType(database, properties, context.getUser()));

		applyProperties(activity, properties);

		return activityDAO.persist(activity, context.getUser());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Activity update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) throws UnauthorizedAccessException {

		final Activity activity = activityDAO.findById(entityId);

		assertDesignPrivileges(context.getUser(), activity.getDatabase());
		applyProperties(activity, changes);

		return activityDAO.persist(activity, context.getUser());
	}

	// -------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------------------------

	private static void assertDesignPrivileges(User user, UserDatabase database) throws UnauthorizedAccessException {
		if (!database.isAllowedDesign(user)) {
			throw new UnauthorizedAccessException("Access denied to database '" + database.getId() + "'.");
		}
	}

	private UserDatabase getDatabase(PropertyMap properties) {
		int databaseId = (Integer) properties.get("databaseId");

		UserDatabase database = databaseDAO.findById(databaseId);
		return database;
	}

	private LocationType getLocationType(UserDatabase database, PropertyMap properties, final User user) {
		// Normally in activityInfo, location types are associated with activities/indicatorGroups, but sigmah does not
		// enforce this rule yet.
		final Integer locationTypeId = properties.get("locationTypeId");

		if (locationTypeId != null) {
			return locationTypeDAO.getReference(locationTypeId);
		} else {
			return locationTypeFromDatabase(database, user);
		}
	}

	private Integer calculateNextSortOrderIndex(int databaseId) {
		Integer maxSortOrder = activityDAO.queryMaxSortOrder(databaseId);
		return maxSortOrder == null ? 1 : maxSortOrder + 1;
	}

	private void applyProperties(Activity activity, PropertyMap changes) {
		if (changes.containsKey("name")) {
			activity.setName((String) changes.get("name"));
		}

		if (changes.containsKey("assessment")) {
			activity.setAssessment((Boolean) changes.get("assessment"));
		}

		if (changes.containsKey("locationType")) {
			activity.setLocationType(locationTypeDAO.getReference(((LocationTypeDTO) changes.get("locationType")).getId()));
		}

		if (changes.containsKey("category")) {
			activity.setCategory((String) changes.get("category"));
		}

		if (changes.containsKey("mapIcon")) {
			activity.setMapIcon((String) changes.get("mapIcon"));
		}

		if (changes.containsKey("reportingFrequency")) {
			activity.setReportingFrequency((Integer) changes.get("reportingFrequency"));
		}

		if (changes.containsKey("sortOrder")) {
			activity.setSortOrder((Integer) changes.get("sortOrder"));
		}
		if (changes.containsKey("isDeleted")) {
			activity.delete();
		}

		activity.getDatabase().setLastSchemaUpdate(new Date());
	}
}
