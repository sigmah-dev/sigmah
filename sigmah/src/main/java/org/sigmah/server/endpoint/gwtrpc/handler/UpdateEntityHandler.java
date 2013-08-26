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
import org.sigmah.shared.domain.reminder.MonitoredPointHistory;
import org.sigmah.shared.domain.reminder.Reminder;
import org.sigmah.shared.domain.reminder.ReminderChangeType;
import org.sigmah.shared.domain.reminder.ReminderHistory;
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

		} else if ("reminder.Reminder".equals(cmd.getEntityName())) {
			updateReminder(user, cmd);
		} else if ("reminder.MonitoredPoint".equals(cmd.getEntityName())) {
			updateMonitoredPoint(user, cmd);
		} else if ("ProjectFunding".equals(cmd.getEntityName())) {
			updateProjectFunding(cmd);
		} else if ("Project".equals(cmd.getEntityName())) {
			ProjectPolicy policy = injector.getInstance(ProjectPolicy.class);
			policy.update(user, cmd.getId(), changeMap);
		} else {
			throw new RuntimeException("unknown entity type");
		}

		return null;
	}

	private VoidResult updateProjectFunding(UpdateEntity cmd) {

		ProjectFunding projectFunding = em.find(ProjectFunding.class, cmd.getId());
		if (projectFunding != null) {
			projectFunding.setPercentage((Double) cmd.getChanges().get("percentage"));
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
		if (indicator.getName().length() > 1024)
			indicator.setName(indicator.getName().substring(0, 1024));
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

	private VoidResult updateReminder(User user, UpdateEntity cmd) {
		Reminder reminderToUpdate = em.find(Reminder.class, cmd.getId());
		if (reminderToUpdate != null) {

			boolean date_modified = !(new Date((Long) cmd.getChanges().get("expectedDate")).equals(reminderToUpdate
			                .getExpectedDate()));
			boolean label_modified = !((String) cmd.getChanges().get("label")).equals(reminderToUpdate.getLabel());

			// Update 3 properties: ExpectedDate,Label,Deleted
			reminderToUpdate.setExpectedDate(new Date((Long) cmd.getChanges().get("expectedDate")));
			reminderToUpdate.setLabel((String) cmd.getChanges().get("label"));
			Boolean deleted = (Boolean) cmd.getChanges().get("deleted");
			if (deleted != null)
				reminderToUpdate.setDeleted(deleted);

			em.merge(reminderToUpdate);

			if (!reminderToUpdate.isDeleted()) {

				if (date_modified) {
					ReminderHistory hist = new ReminderHistory();
					hist.setDate(new Date());
					hist.setType(ReminderChangeType.DATE_MODIFIED);
					hist.setUserId(user.getId());
					hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
					reminderToUpdate.addHistory(hist);
				}

				if (label_modified) {
					ReminderHistory hist = new ReminderHistory();
					hist.setDate(new Date());
					hist.setType(ReminderChangeType.LABEL_MODIFIED);
					hist.setUserId(user.getId());
					hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
					reminderToUpdate.addHistory(hist);
				}

			} else {
				ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DELETED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminderToUpdate.addHistory(hist);
			}
		}
		return new VoidResult();
	}

	private VoidResult updateMonitoredPoint(User user, UpdateEntity cmd) {

		MonitoredPoint point = em.find(MonitoredPoint.class, cmd.getId());
		if (point != null) {

			boolean date_modified = !(new Date((Long) cmd.getChanges().get("expectedDate")).equals(point
			                .getExpectedDate()));
			boolean label_modified = !((String) cmd.getChanges().get("label")).equals(point.getLabel());

			// Update 3 properties: ExpectedDate,Label,Deleted
			point.setExpectedDate(new Date((Long) cmd.getChanges().get("expectedDate")));
			point.setLabel((String) cmd.getChanges().get("label"));
			Boolean deleted = (Boolean) cmd.getChanges().get("deleted");
			if (deleted != null)
				point.setDeleted(deleted);

			em.merge(point);

			if (!point.isDeleted()) {
				if (date_modified) {
					MonitoredPointHistory hist = new MonitoredPointHistory();
					hist.setDate(new Date());
					hist.setType(ReminderChangeType.DATE_MODIFIED);
					hist.setUserId(user.getId());
					hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
					point.addHistory(hist);
				}

				if (label_modified) {
					MonitoredPointHistory hist = new MonitoredPointHistory();
					hist.setDate(new Date());
					hist.setType(ReminderChangeType.LABEL_MODIFIED);
					hist.setUserId(user.getId());
					hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
					point.addHistory(hist);
				}

			} else {
				MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DELETED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}
		}
		return new VoidResult();
	}

}
