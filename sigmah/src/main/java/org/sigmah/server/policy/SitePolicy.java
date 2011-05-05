/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.policy;

import com.google.inject.Inject;
import org.sigmah.server.dao.*;
import org.sigmah.server.domain.*;
import org.sigmah.shared.dao.ActivityDAO;
import org.sigmah.shared.dao.AdminDAO;
import org.sigmah.shared.dao.UserDatabaseDAO;
import org.sigmah.shared.domain.Activity;
import org.sigmah.shared.domain.Location;
import org.sigmah.shared.domain.LocationType;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ReportingPeriod;
import org.sigmah.shared.domain.Site;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.UserPermission;
import org.sigmah.shared.dto.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

public class SitePolicy implements EntityPolicy<Site> {
    private final ActivityDAO activityDAO;
    private final AdminDAO adminDAO;
    private final LocationDAO locationDAO;
    private final PartnerDAO partnerDAO;
    private final ReportingPeriodDAO reportingPeriodDAO;
    private final SiteDAO siteDAO;
    private final UserDatabaseDAO userDatabaseDAO;
    private final EntityManager entityManager;
    

    @Inject
    public SitePolicy(ActivityDAO activityDAO, AdminDAO adminDAO, LocationDAO locationDAO,
                      PartnerDAO partnerDAO, SiteDAO siteDAO, ReportingPeriodDAO reportingPeriodDAO,
                      UserDatabaseDAO userDatabaseDAO, EntityManager entityManager) {
        this.locationDAO = locationDAO;
        this.siteDAO = siteDAO;
        this.reportingPeriodDAO = reportingPeriodDAO;
        this.partnerDAO = partnerDAO;
        this.activityDAO = activityDAO;
        this.adminDAO = adminDAO;
        this.userDatabaseDAO = userDatabaseDAO;
        this.entityManager = entityManager;
    }

    @Override
    public Integer create(User user, PropertyMap properties) {

    	
        Activity activity = null;
        UserDatabase database;
        LocationType locationType;
    	OrgUnit partner = null;
        
    	if(properties.containsKey("activityId")) {
    		activity = activityDAO.findById((Integer) properties.get("activityId"));
    		locationType = activity.getLocationType();
    		database = activity.getDatabase();
    		    		
    	} else if(properties.containsKey("databaseId")) {
    		database = userDatabaseDAO.findById((Integer)properties.get("databaseId"));
    		locationType = locationTypeFromDatabase(database);
			if(user.getOrganization() != null) {
				partner = user.getOrganization().getRoot();
			}
    	} else {
    		throw new RuntimeException("An activityId or databaseId must be provided to create a site");
    	}
    	
    	if(properties.containsKey("partner")) {
    		partner = partnerDAO.findById(((PartnerDTO) properties.get("partner")).getId());
    	}
    	
    	if(partner == null) {
    		throw new RuntimeException("No orgUnit id provided for new site");
    	}
    	
        /*
           * Create and save a new Location object in the database
           */

        Location location = new Location();
        location.setLocationType(locationType);
        updateLocationProperties(location, properties);

        locationDAO.persist(location);

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

        siteDAO.persist(site);

        updateAttributeValueProperties(site, properties, true);

        /*
           * Create the reporting period object
           * IF this is a report-once activity (punctual)
           *
           * otherwise ReportingPeriods are modeled separately on the client.
           */

        if (activity != null && activity.getReportingFrequency() == ActivityDTO.REPORT_ONCE) {

            ReportingPeriod period = new ReportingPeriod();
            period.setSite(site);
            period.setMonitoring(false);

            updatePeriodProperties(period, properties, true);

            reportingPeriodDAO.persist(period);

            updateIndicatorValueProperties(period, properties, true);

        }

        return site.getId();

    }

	private LocationType locationTypeFromDatabase(UserDatabase database) {

		Set<LocationType> locationTypes = database.getCountry().getLocationTypes();
		for(LocationType type : locationTypes) {
			if(type.getName().equals(LocationType.DEFAULT)) {
				return type;
			}
		}
		// still need to create the default location type for this country
		LocationType defaultType = new LocationType();
		defaultType.setName(LocationType.DEFAULT);
		defaultType.setCountry(database.getCountry());
		
		entityManager.persist(defaultType);
		
		return defaultType;
	}


    public void update(User user, Object id, PropertyMap changes)  {

        Site site = siteDAO.findById((Integer)id);

        assertSiteEditPrivileges(user, site.getDatabase(), site.getPartner());

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
    }

    /**
     * Asserts that the user has permission to edit a site in a given database
     * belonging to a given partner
     */
    protected void assertSiteEditPrivileges(User user, UserDatabase db, OrgUnit partner)  {

        if (db.getOwner().getId() == user.getId()) {
            return;
        }

        UserPermission perm = db.getPermissionByUser(user);
        if (perm.isAllowEditAll()) {
            return;
        }
        if (!perm.isAllowEdit()) {
            throw new IllegalAccessError();
        }
        if (perm.getPartner().getId() != partner.getId()) {
            throw new IllegalAccessError();
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
                    if(entity != null) {
                        locationDAO.addAdminMembership(location.getId(), entity.getId());
                    }
                } else {
                    if(entity != null) {
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
                attributeValues.put(AttributeDTO.idForPropertyName(change.getKey()), (Boolean)change.getValue());
            }
        }
        if(!attributeValues.isEmpty()) {
            siteDAO.updateAttributeValues(site.getId(), attributeValues);
        }
   }

    protected void updateIndicatorValueProperties(ReportingPeriod period, PropertyMap changes, boolean creating) {

        for (Map.Entry<String, Object> change : changes.entrySet()) {

            String property = change.getKey();
            Object value = change.getValue();

            if (property.startsWith(IndicatorDTO.PROPERTY_PREFIX)) {

                int indicatorId = IndicatorDTO.indicatorIdForPropertyName(property);

                if(creating) {
                    if(value != null) {
                        reportingPeriodDAO.addIndicatorValue(period.getId(), indicatorId, (Double)value);
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

            } else if (isAdminBound &&
                    AdminLevelDTO.getPropertyName(location.getLocationType().getBoundAdminLevel().getId()).equals(property)) {

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