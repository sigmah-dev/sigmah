/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.Date;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sigmah.server.policy.ActivityPolicy;
import org.sigmah.server.policy.PersonalEventPolicy;
import org.sigmah.server.policy.ProjectPolicy;
import org.sigmah.server.policy.ProjectReportPolicy;
import org.sigmah.server.policy.PropertyMap;
import org.sigmah.server.policy.SitePolicy;
import org.sigmah.shared.command.UpdateEntity;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.Attribute;
import org.sigmah.shared.domain.AttributeGroup;
import org.sigmah.shared.domain.Indicator;
import org.sigmah.shared.domain.ProjectFunding;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.UserDatabase;
import org.sigmah.shared.domain.reminder.MonitoredPoint;
import org.sigmah.shared.domain.reminder.Reminder;
import org.sigmah.shared.exception.CommandException;
import org.sigmah.shared.exception.IllegalAccessCommandException;

import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * @author Alex Bertram
 * @see org.sigmah.shared.command.UpdateEntity
 */
public class UpdateEntityHandler extends BaseEntityHandler implements CommandHandler<UpdateEntity> {

	private final static Log LOG = LogFactory.getLog(UpdateEntityHandler.class);

	private final Injector injector;

	@Inject
	public UpdateEntityHandler(EntityManager em, Injector injector) {
		super(em);
		this.injector = injector;
	}

	@Override
    public CommandResult execute(UpdateEntity cmd, User user) throws CommandException {

		if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Update command for entity: " + cmd.getEntityName() + ".");
		}

		Map<String, Object> changes = cmd.getChanges().getTransientMap();
		PropertyMap changeMap = new PropertyMap(changes);

		if ("Activity".equals(cmd.getEntityName())) {
			ActivityPolicy policy = injector.getInstance(ActivityPolicy.class);
			policy.update(user, cmd.getId(), changeMap);

		} else if ("AttributeGroup".equals(cmd.getEntityName())) {
			updateAttributeGroup(cmd, changes);

		} else if ("Attribute".equals(cmd.getEntityName())) {
			updateAttribute(user, cmd, changes);

		} else if ("Indicator".equals(cmd.getEntityName())) {
			updateIndicator(user, cmd, changes);

		} else if ("Site".equals(cmd.getEntityName())) {
			SitePolicy policy = injector.getInstance(SitePolicy.class);
			policy.update(user, cmd.getId(), changeMap);

		} else if ("PersonalEvent".equals(cmd.getEntityName())) {
            PersonalEventPolicy policy = injector.getInstance(PersonalEventPolicy.class);
			policy.update(user, cmd.getId(), changeMap);

		} else if ("ProjectReport".equals(cmd.getEntityName())) {
            ProjectReportPolicy policy = injector.getInstance(ProjectReportPolicy.class);
			policy.update(user, cmd.getId(), changeMap);
			
		}else if("reminder.Reminder".equals(cmd.getEntityName()))
		{
			updateReminder(cmd);
		}else if("reminder.MonitoredPoint".equals(cmd.getEntityName()))
		{
			updateMonitoredPoint(cmd);
		}
		else if("ProjectFunding".equals(cmd.getEntityName()))
		{
			updateProjectFunding(cmd);
		}
		else if("Project".equals(cmd.getEntityName()))
		{
			 ProjectPolicy policy = injector.getInstance(ProjectPolicy.class);
				policy.update(user, cmd.getId(), changeMap);
		}
		else {
			throw new RuntimeException("unknown entity type");
		}

		return null;
	}

    private VoidResult updateProjectFunding(UpdateEntity cmd) {
		
    	ProjectFunding projectFunding = em.find(ProjectFunding.class, cmd.getId());
    	if(projectFunding!=null)
    	{
    		projectFunding.setPercentage((Double)cmd.getChanges().get("percentage"));
    		em.merge(projectFunding);
    	}
		return new VoidResult();
	}

	private void updateIndicator(User user, UpdateEntity cmd, Map<String, Object> changes)
            throws IllegalAccessCommandException {
		Indicator indicator = em.find(Indicator.class, cmd.getId());

		// todo: make UserDatabase non-nullable
		UserDatabase db = indicator.getDatabase();
		if (db == null) {
			db = indicator.getActivity().getDatabase();
		}

		assertDesignPriviledges(user, db);

		updateIndicatorProperties(indicator, changes);
	}

    private void updateAttribute(User user, UpdateEntity cmd, Map<String, Object> changes) {
		Attribute attribute = em.find(Attribute.class, cmd.getId());

		// TODO: decide where attributes belong and how to manage them
		// assertDesignPriviledges(user, attribute.get);

		updateAttributeProperties(changes, attribute);
	}

    private void updateAttributeGroup(UpdateEntity cmd, Map<String, Object> changes) {
		AttributeGroup group = em.find(AttributeGroup.class, cmd.getId());

		updateAttributeGroupProperties(group, changes);
	}

	
	private VoidResult updateReminder(UpdateEntity cmd)
	{
		Reminder reminderToUpdate = em.find(Reminder.class, cmd.getId());
		if (reminderToUpdate != null) {
			//Update 3 properties: ExpectedDate,Label,Deleted
			reminderToUpdate.setExpectedDate(new Date((Long) cmd.getChanges().get("expectedDate")));
			reminderToUpdate.setLabel((String) cmd.getChanges().get("label"));
			String deleted = cmd.getChanges().get("deleted").toString();
			reminderToUpdate.setDeleted(Boolean.valueOf(deleted));
			em.merge(reminderToUpdate);
		}
		return new VoidResult();
	}

	private VoidResult updateMonitoredPoint(UpdateEntity cmd) {
		
		MonitoredPoint point = em.find(MonitoredPoint.class, cmd.getId());
		if (point != null) {
			//Update 3 properties: ExpectedDate,Label,Deleted
			point.setExpectedDate(new Date((Long) cmd.getChanges().get("expectedDate")));
			point.setLabel((String) cmd.getChanges().get("label"));
			String deleted = cmd.getChanges().get("deleted").toString();
			point.setDeleted(Boolean.valueOf(deleted));
			em.merge(point);
		}
		return new VoidResult();
	}

}
