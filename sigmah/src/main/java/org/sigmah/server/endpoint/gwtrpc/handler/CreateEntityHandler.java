/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.server.policy.ActivityPolicy;
import org.sigmah.server.policy.LayoutGroupPolicy;
import org.sigmah.server.policy.OrgUnitModelPolicy;
import org.sigmah.server.policy.PersonalEventPolicy;
import org.sigmah.server.policy.PrivacyGroupPolicy;
import org.sigmah.server.policy.ProfilePolicy;
import org.sigmah.server.policy.ProjectModelPolicy;
import org.sigmah.server.policy.ProjectPolicy;
import org.sigmah.server.policy.ProjectReportModelPolicy;
import org.sigmah.server.policy.ProjectReportPolicy;
import org.sigmah.server.policy.PropertyMap;
import org.sigmah.server.policy.SitePolicy;
import org.sigmah.server.policy.UserDatabasePolicy;
import org.sigmah.server.policy.UserPolicy;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.Activity;
import org.sigmah.shared.domain.Attribute;
import org.sigmah.shared.domain.AttributeGroup;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectFunding;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.reminder.MonitoredPoint;
import org.sigmah.shared.domain.reminder.MonitoredPointList;
import org.sigmah.shared.domain.reminder.Reminder;
import org.sigmah.shared.domain.reminder.ReminderList;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.layout.LayoutGroupDTO;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.report.ProjectReportModelSectionDTO;
import org.sigmah.shared.dto.report.ReportModelDTO;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.exception.IllegalAccessCommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Alex Bertram (akbertram@gmail.com)
 * @see org.sigmah.shared.command.CreateEntity
 */
public class CreateEntityHandler extends BaseEntityHandler implements CommandHandler<CreateEntity> {

    private final Injector injector;
    private final ProjectMapper mapper;

    private static final Log log = LogFactory.getLog(CreateEntityHandler.class);

    @Inject
    public CreateEntityHandler(EntityManager em, ProjectMapper mapper, Injector injector) {
        super(em);
        this.injector = injector;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(CreateEntity cmd, User user) throws CommandException {

        Map<String, Object> properties = cmd.getProperties().getTransientMap();
        PropertyMap propertyMap = new PropertyMap(cmd.getProperties().getTransientMap());

        if ("UserDatabase".equals(cmd.getEntityName())) {
            UserDatabasePolicy policy = injector.getInstance(UserDatabasePolicy.class);
            return new CreateResult((Integer) policy.create(user, propertyMap));
        } else if ("Activity".equals(cmd.getEntityName())) {
            ActivityPolicy policy = injector.getInstance(ActivityPolicy.class);
            return new CreateResult((Integer) policy.create(user, propertyMap));
        } else if ("AttributeGroup".equals(cmd.getEntityName())) {
            return createAttributeGroup(cmd, properties);
        } else if ("Attribute".equals(cmd.getEntityName())) {
            return createAttribute(cmd, properties);
        } else if ("Indicator".equals(cmd.getEntityName())) {
            return createIndicator(user, cmd, properties);
        } else if ("Project".equals(cmd.getEntityName())) {
            final ProjectPolicy policy = injector.getInstance(ProjectPolicy.class);
            final Project createdProject = (Project) policy.create(user, propertyMap);
            final ProjectDTOLight mappedProject = mapper.map(createdProject, false);
            return new CreateResult(mappedProject);
        } else if ("Site".equals(cmd.getEntityName())) {
            SitePolicy policy = injector.getInstance(SitePolicy.class);
            return new CreateResult((Integer) policy.create(user, propertyMap));
        } else if ("PersonalEvent".equals(cmd.getEntityName())) {
            PersonalEventPolicy policy = injector.getInstance(PersonalEventPolicy.class);
            return new CreateResult((Integer) policy.create(user, propertyMap));
        } else if ("ProjectFunding".equals(cmd.getEntityName())) {
            return createFunding(properties);
        } else if ("ProjectReport".equals(cmd.getEntityName())) {
            ProjectReportPolicy policy = injector.getInstance(ProjectReportPolicy.class);
            return new CreateResult((Integer) policy.create(user, propertyMap));
        } else if ("ProjectReportDraft".equals(cmd.getEntityName())) {
            ProjectReportPolicy policy = injector.getInstance(ProjectReportPolicy.class);
            return new CreateResult(policy.createDraft(user, propertyMap));
        } else if ("MonitoredPoint".equals(cmd.getEntityName())) {
            return createMonitoredPoint(properties);
        } else if ("Reminder".equals(cmd.getEntityName())) {
            return createReminder(properties);
        } else if ("User".equals(cmd.getEntityName())) {
            return createUser(user, propertyMap);
        }else if ("PrivacyGroup".equals(cmd.getEntityName())) {
            return createPrivacyGroup(user, propertyMap);
        }else if ("Profile".equals(cmd.getEntityName())) {
            return createProfile(user, propertyMap);
        }else if ("ProjectModel".equals(cmd.getEntityName())) {
            return createProjectModel(user, propertyMap);
        }else if ("ProjectReportModel".equals(cmd.getEntityName())) {
            return createProjectReportModel(user, propertyMap);
        }else if ("OrgUnitModel".equals(cmd.getEntityName())) {
            return createOrgUnitModel(user, propertyMap);
        }else if ("GroupLayout".equals(cmd.getEntityName())) {
            return createLayoutGroupModel(user, propertyMap);
        }else {
            throw new CommandException("Invalid entity class " + cmd.getEntityName());
        }
    }

    private CommandResult createLayoutGroupModel(User user, PropertyMap propertyMap) {
		LayoutGroupPolicy policy = injector.getInstance(LayoutGroupPolicy.class);
		LayoutGroupDTO newGroupModel = (LayoutGroupDTO) policy.create(user, propertyMap);
    	if(newGroupModel != null){
    		CreateResult c = new CreateResult(newGroupModel.getId());
    		c.setEntity(newGroupModel);
    		return c;
    	}   		
    	else
    		return null;
	}
    
	private CommandResult createOrgUnitModel(User user, PropertyMap propertyMap) {
		OrgUnitModelPolicy policy = injector.getInstance(OrgUnitModelPolicy.class);
		OrgUnitModelDTO newOrgUnitModel = (OrgUnitModelDTO) policy.create(user, propertyMap);
    	if(newOrgUnitModel != null){
    		CreateResult c = new CreateResult(newOrgUnitModel.getId());
    		c.setEntity(newOrgUnitModel);
    		return c;
    	}   		
    	else
    		return null;
	}

    private CommandResult createProjectReportModel(User user, PropertyMap propertyMap) {
    	ProjectReportModelPolicy policy = injector.getInstance(ProjectReportModelPolicy.class);
    	ReportModelDTO newProjectReportModel = (ReportModelDTO) policy.create(user, propertyMap);
    	if(newProjectReportModel != null){
    		CreateResult c = new CreateResult(newProjectReportModel.getId());
    		c.setEntity(newProjectReportModel);
    		return c;
    	}   		
    	else
    		return null;
    }
    
    private CommandResult createProjectModel(User user, PropertyMap propertyMap) {
    	ProjectModelPolicy policy = injector.getInstance(ProjectModelPolicy.class);
    	ProjectModelDTO newProjectModel = (ProjectModelDTO) policy.create(user, propertyMap);
    	if(newProjectModel != null){
    		CreateResult c = new CreateResult(newProjectModel.getId());
    		c.setEntity(newProjectModel);
    		return c;
    	}   		
    	else
    		return null;
    }
    
    private CommandResult createProfile(User user, PropertyMap propertyMap) {
    	ProfilePolicy policy = injector.getInstance(ProfilePolicy.class);
    	ProfileDTO newProfile = (ProfileDTO) policy.create(user, propertyMap);
    	if(newProfile != null){
    		CreateResult c = new CreateResult(newProfile.getId());
    		c.setEntity(newProfile);
    		return c;
    	}   		
    	else
    		return null;
    }
    
    private CommandResult createPrivacyGroup(User user, PropertyMap propertyMap) {
    	PrivacyGroupPolicy policy = injector.getInstance(PrivacyGroupPolicy.class);
    	PrivacyGroupDTO newPrivacyGroup = (PrivacyGroupDTO) policy.create(user, propertyMap);
    	if(newPrivacyGroup != null){
    		CreateResult c = new CreateResult(newPrivacyGroup.getCode());
    		c.setEntity(newPrivacyGroup);
    		return c;
    	}   		
    	else
    		return null;
    }

    private CommandResult createUser(User user, PropertyMap propertyMap) {
    	UserPolicy policy = injector.getInstance(UserPolicy.class);
    	UserDTO newUser = (UserDTO) policy.create(user, propertyMap);
    	if(newUser != null){
    		CreateResult c = new CreateResult(newUser.getIdd());
    		c.setEntity(newUser);
    		return c;
    	}   		
    	else
    		return null;
    }
    private CommandResult createAttributeGroup(CreateEntity cmd, Map<String, Object> properties) {

        AttributeGroup group = new AttributeGroup();
        updateAttributeGroupProperties(group, properties);

        em.persist(group);

        Activity activity = em.find(Activity.class, properties.get("activityId"));
        activity.getAttributeGroups().add(group);

        return new CreateResult(group.getId());
    }

    private CommandResult createAttribute(CreateEntity cmd, Map<String, Object> properties) {

        Attribute attribute = new Attribute();
        attribute.setGroup(em.getReference(AttributeGroup.class, properties.get("attributeGroupId")));

        updateAttributeProperties(properties, attribute);

        em.persist(attribute);

        return new CreateResult(attribute.getId());
    }

    private CommandResult createIndicator(User user, CreateEntity cmd, Map<String, Object> properties)
            throws IllegalAccessCommandException {

        Indicator indicator = new Indicator();
        
        if (properties.containsKey("activityId")) {
        	Object o = properties.get("activityId");
        	indicator.setActivity(em.getReference(Activity.class, o));
        	assertDesignPriviledges(user, indicator.getActivity().getDatabase());
        		
        } else if (properties.containsKey("databaseId")) {        	
        	Object o = properties.get("databaseId");
        	indicator.setDatabase(em.getReference(UserDatabase.class, o));
        	assertDesignPriviledges(user, indicator.getDatabase());
        }
    
        updateIndicatorProperties(indicator, properties);
        em.persist(indicator);

        return new CreateResult(indicator.getId());

    }

    private CommandResult createFunding(Map<String, Object> properties) {

        // Retrieves parameters.
        Object fundingId = properties.get("fundingId");
        Object fundedId = properties.get("fundedId");
        Object percentage = properties.get("percentage");

        // Retrieves projects.
        final Project fundingProject = em.find(Project.class, fundingId);
        final Project fundedProject = em.find(Project.class, fundedId);

        // Retrieves the eventual already existing link.
        final Query query = em.createQuery("SELECT f FROM ProjectFunding f WHERE f.funding = :p1 AND f.funded = :p2");
        query.setParameter("p1", fundingProject);
        query.setParameter("p2", fundedProject);

        ProjectFunding funding;

        // Updates or creates the link.
        boolean create = false;
        try {
            funding = (ProjectFunding) query.getSingleResult();
        } catch (NoResultException e) {
            funding = new ProjectFunding();
            funding.setFunding(fundingProject);
            funding.setFunded(fundedProject);
            create = true;
        }

        funding.setPercentage((Double) percentage);

        // Saves.
        em.persist(funding);

        final CreateResult result = new CreateResult(injector.getInstance(Mapper.class).map(funding,
                ProjectFundingDTO.class));

        // Sets update or create to inform the client-side.
        result.setNewId(create ? -1 : 1);

        return result;
    }

    private CommandResult createMonitoredPoint(Map<String, Object> properties) {

        if (log.isDebugEnabled()) {
            log.debug("[createMonitoredPoint] Starts monitored point creation.");
        }

        // Retrieves parameters.
        Object expectedDate = properties.get("expectedDate");
        Object label = properties.get("label");
        Object projectId = properties.get("projectId");

        // Retrieves project.
        final Project project = em.find(Project.class, projectId);

        if (log.isDebugEnabled()) {
            log.debug("[createMonitoredPoint] Retrieves the project #" + project.getId() + ".");
        }

        // Retrieves list.
        MonitoredPointList list = project.getPointsList();

        // Creates the list if needed.
        if (list == null) {

            if (log.isDebugEnabled()) {
                log.debug("[createMonitoredPoint] The project #" + project.getId()
                        + " doesn't have a points list. Creates it.");
            }

            list = new MonitoredPointList();
            list.setPoints(new ArrayList<MonitoredPoint>());
            project.setPointsList(list);
        }

        // Creates point.
        final MonitoredPoint point = new MonitoredPoint();
        point.setLabel((String) label);
        point.setExpectedDate(new Date((Long) expectedDate));
        point.setCompletionDate(null);
        point.setFile(null);

        // Adds it to the list.
        list.addMonitoredPoint(point);

        // Saves it.
        em.persist(project);

        if (log.isDebugEnabled()) {
            log.debug("[createMonitoredPoint] Ends monitored point creation #" + point.getId() + " in list #"
                    + list.getId() + ".");
        }

        return new CreateResult(injector.getInstance(Mapper.class).map(point, MonitoredPointDTO.class));
    }
    
    private CommandResult createReminder(Map<String, Object> properties) {

        if (log.isDebugEnabled()) {
            log.debug("[createReminder] Starts reminder creation.");
        }

        // Retrieves parameters.
        Object expectedDate = properties.get("expectedDate");
        Object label = properties.get("label");
        Object projectId = properties.get("projectId");

        // Retrieves project.
        final Project project = em.find(Project.class, projectId);

        if (log.isDebugEnabled()) {
            log.debug("[createReminder] Retrieves the project #" + project.getId() + ".");
        }

        // Retrieves list.
        ReminderList list = project.getRemindersList();

        // Creates the list if needed.
        if (list == null) {

            if (log.isDebugEnabled()) {
                log.debug("[createReminder] The project #" + project.getId()
                        + " doesn't have a reminders list. Creates it.");
            }

            list = new ReminderList();
            list.setReminders(new ArrayList<Reminder>());
            project.setRemindersList(list);
        }

        // Creates point.
        final Reminder reminder = new Reminder();
        reminder.setLabel((String) label);
        reminder.setExpectedDate(new Date((Long) expectedDate));
        reminder.setCompletionDate(null);

        // Adds it to the list.
        list.addReminder(reminder);

        // Saves it.
        em.persist(project);

        if (log.isDebugEnabled()) {
            log.debug("[createReminder] Ends reminder creation #" + reminder.getId() + " in list #" + list.getId()
                    + ".");
        }

        return new CreateResult(injector.getInstance(Mapper.class).map(reminder, ReminderDTO.class));
    }
}
