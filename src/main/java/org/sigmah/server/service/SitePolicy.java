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
import java.util.HashMap;
import java.util.Map;

import org.sigmah.server.dao.ActivityDAO;
import org.sigmah.server.dao.AdminDAO;
import org.sigmah.server.dao.LocationDAO;
import org.sigmah.server.dao.PartnerDAO;
import org.sigmah.server.dao.ReportingPeriodDAO;
import org.sigmah.server.dao.SiteDAO;
import org.sigmah.server.dao.UserDatabaseDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Activity;
import org.sigmah.server.domain.Location;
import org.sigmah.server.domain.LocationType;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.ReportingPeriod;
import org.sigmah.server.domain.Site;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.UserDatabase;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dto.ActivityDTO;
import org.sigmah.shared.dto.AdminEntityDTO;
import org.sigmah.shared.dto.AdminLevelDTO;
import org.sigmah.shared.dto.AttributeDTO;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.PartnerDTO;
import org.sigmah.shared.dto.SiteDTO;

import com.google.inject.Inject;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * {@link Site} srevice implementation.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SitePolicy extends AbstractEntityService<Site, Integer, SiteDTO> {

	private final ActivityDAO activityDAO;
	private final AdminDAO adminDAO;
	private final LocationDAO locationDAO;
	private final PartnerDAO partnerDAO;
	private final ReportingPeriodDAO reportingPeriodDAO;
	private final SiteDAO siteDAO;
	private final UserDatabaseDAO userDatabaseDAO;
	
	@Inject
	public SitePolicy(ActivityDAO activityDAO, AdminDAO adminDAO, LocationDAO locationDAO, PartnerDAO partnerDAO, SiteDAO siteDAO, ReportingPeriodDAO reportingPeriodDAO, UserDatabaseDAO userDatabaseDAO) {
		this.locationDAO = locationDAO;
		this.siteDAO = siteDAO;
		this.reportingPeriodDAO = reportingPeriodDAO;
		this.partnerDAO = partnerDAO;
		this.activityDAO = activityDAO;
		this.adminDAO = adminDAO;
		this.userDatabaseDAO = userDatabaseDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Site create(final PropertyMap properties, final UserExecutionContext context) {

		final User user = context.getUser();

		Activity activity = null;
		UserDatabase database;
		LocationType locationType;
		OrgUnit partner = null;

		if (properties.containsKey("activityId")) {
			activity = activityDAO.findById((Integer) properties.get("activityId"));
			locationType = activity.getLocationType();
			database = activity.getDatabase();

		} else if (properties.containsKey("databaseId")) {
			database = userDatabaseDAO.findById((Integer) properties.get("databaseId"));
			locationType = locationTypeFromDatabase(database, user);
			if (user.getOrganization() != null) {
				partner = user.getOrganization().getRoot();
			}
		} else {
			throw new RuntimeException("An activityId or databaseId must be provided to create a site");
		}

		if (properties.containsKey("partner")) {
			partner = partnerDAO.findById(((PartnerDTO) properties.get("partner")).getId());
		}

		if (partner == null) {
			throw new RuntimeException("No orgUnit id provided for new site");
		}

		/*
		 * Create and save a new Location object in the database
		 */

		Location location = new Location();
		location.setLocationType(locationType);
		updateLocationProperties(location, properties);

		locationDAO.persist(location, user);

		updateAdminProperties(location, properties, true);

		/*
		 * Create and persist the Site object
		 */

		Site site = new Site();
		site.setLocation(location);
		site.setActivity(activity);
		site.setDatabase(database);
		site.setPartner(partner);
		site.setDateCreated(new Date());

		updateSiteProperties(site, properties, true);

		siteDAO.persist(site, user);

		updateAttributeValueProperties(site, properties, true);

		/*
		 * Create the reporting period object IF this is a report-once activity (punctual) otherwise ReportingPeriods are
		 * modeled separately on the client.
		 */

		if (activity != null && activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {

			ReportingPeriod period = new ReportingPeriod();
			period.setSite(site);
			period.setMonitoring(false);

			updatePeriodProperties(period, properties, true);

			reportingPeriodDAO.persist(period, user);

			updateIndicatorValueProperties(period, properties, true);
		}

		return site;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Site update(final Integer id, final PropertyMap changes, final UserExecutionContext context) {

		final Site site = siteDAO.findById(id);

		assertSiteEditPrivileges(context.getUser(), site.getDatabase(), site.getPartner());

		site.setDateEdited(new Date());

		updateSiteProperties(site, changes, false);
		updateAttributeValueProperties(site, changes, false);
		updateLocationProperties(site.getLocation(), changes);
		updateAdminProperties(site.getLocation(), changes, false);

		if (!site.getReportingPeriods().isEmpty()) {
			ReportingPeriod period = site.getReportingPeriods().iterator().next();
			updatePeriodProperties(period, changes, false);
			updateIndicatorValueProperties(period, changes, false);
		}

		return site;
	}

	/**
	 * Asserts that the user has permission to edit a site in a given database belonging to a given partner
	 */
	protected void assertSiteEditPrivileges(User user, UserDatabase db, OrgUnit partner) {
		
		if (db.getOwner().getId().equals(user.getId())) {
			return;
		}
		
		if (!Handlers.isGranted(user.getOrgUnitsWithProfiles(), GlobalPermissionEnum.EDIT_INDICATOR)) {
			throw new IllegalAccessError("User '" + user.getEmail() + "' can't edit database '" + db.getId() + "' because he misses '" + GlobalPermissionEnum.EDIT_INDICATOR + "' permission.");
		}
		
		if (!Handlers.isProjectVisible((Project) db, user)) {
			throw new IllegalAccessError("Database '" + db.getId() + "' is not visible by user '" + user.getEmail() + "'.");
		}
	}

	protected void updateAdminProperties(Location location, PropertyMap changes, boolean creating) {

		for (Map.Entry<String, Object> change : changes.entrySet()) {
			String property = change.getKey();
			Object value = change.getValue();

			if (property.startsWith(AdminLevelDTO.PROPERTY_PREFIX)) {

				int levelId = AdminLevelDTO.levelIdForPropertyName(property);
				AdminEntityDTO entity = (AdminEntityDTO) value;

				if (creating) {
					if (entity != null) {
						locationDAO.addAdminMembership(location.getId(), entity.getId());
					}
				} else {
					if (entity != null) {
						locationDAO.updateAdminMembership(location.getId(), levelId, entity.getId());
					} else {
						locationDAO.removeMembership(location.getId(), levelId);
					}
				}
			}
		}
	}

	protected void updateAttributeValueProperties(Site site, PropertyMap changes, boolean creating) {

		Map<Integer, Boolean> attributeValues = new HashMap<Integer, Boolean>();

		for (Map.Entry<String, Object> change : changes.entrySet()) {
			if (change.getKey().startsWith(AttributeDTO.PROPERTY_PREFIX)) {
				attributeValues.put(AttributeDTO.idForPropertyName(change.getKey()), (Boolean) change.getValue());
			}
		}
		if (!attributeValues.isEmpty()) {
			siteDAO.updateAttributeValues(site.getId(), attributeValues);
		}
	}

	protected void updateIndicatorValueProperties(ReportingPeriod period, PropertyMap changes, boolean creating) {

		for (Map.Entry<String, Object> change : changes.entrySet()) {

			String property = change.getKey();
			Object value = change.getValue();

			if (property.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {

				int indicatorId = IndicatorDTO.indicatorIdForPropertyName(property);

				if (creating) {
					if (value != null) {
						reportingPeriodDAO.addIndicatorValue(period.getId(), indicatorId, (Double) value);
					}
				} else {
					reportingPeriodDAO.updateIndicatorValue(period.getId(), indicatorId, (Double) value);
				}
			}
		}
	}

	protected void updateSiteProperties(Site site, PropertyMap changes, boolean creating) {

		for (Map.Entry<String, Object> change : changes.entrySet()) {

			String property = change.getKey();
			Object value = change.getValue();

			if ("date1".equals(property)) {
				site.setDate1((Date) value);

			} else if ("date2".equals(property)) {
				site.setDate2((Date) value);

			} else if ("assessmentId".equals(property)) {
				site.setAssessment(siteDAO.findById((Integer) value));

			} else if ("comments".equals(property)) {
				site.setComments((String) value);

			} else if ("status".equals(property)) {
				site.setStatus((Integer) value);
			}
		}
	}

	protected void updateLocationProperties(Location location, PropertyMap changes) {

		boolean isAdminBound = location.getLocationType().getBoundAdminLevel() != null;

		for (Map.Entry<String, Object> change : changes.entrySet()) {

			String property = change.getKey();
			Object value = change.getValue();

			if ("locationName".equals(property)) {
				location.setName((String) value);

			} else if ("locationAxe".equals(property)) {
				location.setAxe((String) value);

			} else if ("x".equals(property)) {
				location.setX((Double) value);

			} else if ("y".equals(property)) {
				location.setY((Double) changes.get("y"));

			} else if (isAdminBound && AdminLevelDTO.getPropertyName(location.getLocationType().getBoundAdminLevel().getId()).equals(property)) {

				location.setName(adminDAO.findById(((AdminEntityDTO) value).getId()).getName());
			}
		}
	}

	protected void updatePeriodProperties(ReportingPeriod period, PropertyMap changes, boolean creating) {
		for (Map.Entry<String, Object> change : changes.entrySet()) {

			String property = change.getKey();
			Object value = change.getValue();

			if ("date1".equals(property)) {
				period.setDate1((Date) value);

			} else if ("date2".equals(property)) {
				period.setDate2((Date) value);
			}
		}
	}
}
