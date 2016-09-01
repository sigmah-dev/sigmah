package org.sigmah.server.service;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.util.Date;

import org.sigmah.server.dao.ActivityDAO;
import org.sigmah.server.dao.LocationTypeDAO;
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
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.util.Handlers;
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
	private final ProjectDAO projectDAO;
	private final LocationTypeDAO locationTypeDAO;

	@Inject
	public ActivityService(ActivityDAO activityDAO, ProjectDAO projectDAO, LocationTypeDAO locationTypeDAO) {
		this.activityDAO = activityDAO;
		this.projectDAO = projectDAO;
		this.locationTypeDAO = locationTypeDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Activity create(final PropertyMap properties, final UserExecutionContext context) throws UnauthorizedAccessException {

		final Project project = getDatabase(properties);
		Handlers.assertDesignPrivileges(context.getUser(), project);

		// Create the entity.
		final Activity activity = new Activity();
		activity.setDatabase(project);
		activity.setSortOrder(calculateNextSortOrderIndex(project.getId()));
		activity.setLocationType(getLocationType(project, properties, context.getUser()));

		applyProperties(activity, properties);

		return activityDAO.persist(activity, context.getUser());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Activity update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) throws UnauthorizedAccessException {

		final Activity activity = activityDAO.findById(entityId);

		Handlers.assertDesignPrivileges(context.getUser(), activity.getDatabase());
		applyProperties(activity, changes);

		return activityDAO.persist(activity, context.getUser());
	}

	// -------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// -------------------------------------------------------------------------------------------------------------

	private Project getDatabase(PropertyMap properties) {
		int databaseId = (Integer) properties.get("databaseId");

		Project project = projectDAO.findById(databaseId);
		return project;
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
