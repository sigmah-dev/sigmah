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

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.sigmah.server.dao.ProjectDAO;
import org.sigmah.server.dao.ReminderDAO;
import org.sigmah.server.dao.ReminderListDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.reminder.Reminder;
import org.sigmah.server.domain.reminder.ReminderHistory;
import org.sigmah.server.domain.reminder.ReminderList;
import org.sigmah.server.service.base.AbstractEntityService;
import org.sigmah.server.service.util.PropertyMap;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * {@link Reminder} corresponding service implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ReminderService extends AbstractEntityService<Reminder, Integer, ReminderDTO> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(ReminderService.class);

	@Inject
	private ProjectDAO projectDAO;

	@Inject
	private ReminderDAO reminderDAO;

	@Inject
	private ReminderListDAO reminderListDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reminder create(final PropertyMap properties, final UserExecutionContext context) throws CommandException {

		if (LOG.isDebugEnabled()) {
			LOG.debug("Starts reminder creation.");
		}

		final User user = context.getUser();

		// Retrieves parameters.
		final Object expectedDate = properties.get(ReminderDTO.EXPECTED_DATE);
		final Object label = properties.get(ReminderDTO.LABEL);
		final Object projectId = properties.get(ReminderDTO.PROJECT_ID);

		if (!(projectId instanceof Integer)) {
			throw new CommandException("Invalid project id.");
		}

		// Retrieves project.
		LOG.debug("Retrieves the project #{}.", projectId);
		final Project project = em().find(Project.class, projectId);

		// Retrieves list.
		ReminderList list = reminderListDAO.findByProjectId(project.getId());

		// Creates the list if needed.
		if (list == null) {

			LOG.debug("The project #{} does not possess a reminders list. Creates it.", project.getId());

			list = new ReminderList();
			list.setReminders(new ArrayList<Reminder>());
			project.setRemindersList(list);
		}

		// Creates point.
		final Reminder reminder = new Reminder();
		reminder.setLabel((String) label);
		reminder.setExpectedDate(new Date((Long) expectedDate));
		reminder.setCompletionDate(null);
		reminder.setDeleted(false);

		// Adds it to the list.
		list.addReminder(reminder);

		ReminderHistory hist = new ReminderHistory();
		hist.setDate(new Date());
		hist.setType(ReminderChangeType.CREATED);
		hist.setUserId(user.getId());
		hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
		reminder.addHistory(hist);

		// Saves it.
		reminderListDAO.persist(list, user);
		projectDAO.persist(project, user);

		LOG.debug("Ends reminder creation #{} in list #{}.", reminder.getId(), list.getId());

		return reminder;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Reminder update(final Integer entityId, final PropertyMap changes, final UserExecutionContext context) throws CommandException {

		final Reminder reminderToUpdate = reminderDAO.findById(entityId);

		if (reminderToUpdate == null) {
			throw new CommandException("Cannot find Reminder with id #" + entityId + ".");
		}

		final User user = context.getUser();

		final boolean date_modified = !(new Date((Long) changes.get(ReminderDTO.EXPECTED_DATE)).equals(reminderToUpdate.getExpectedDate()));
		final boolean label_modified = !((String) changes.get(ReminderDTO.LABEL)).equals(reminderToUpdate.getLabel());

		// Update 3 properties: ExpectedDate,Label,Deleted
		reminderToUpdate.setExpectedDate(new Date((Long) changes.get(ReminderDTO.EXPECTED_DATE)));
		reminderToUpdate.setLabel((String) changes.get(ReminderDTO.LABEL));
		final Boolean deleted = (Boolean) changes.get(ReminderDTO.DELETED);
		if (deleted != null) {
			reminderToUpdate.setDeleted(deleted);
		}

		if (BooleanUtils.isFalse(reminderToUpdate.getDeleted())) {

			if (date_modified) {
				final ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminderToUpdate.addHistory(hist);
			}

			if (label_modified) {
				final ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminderToUpdate.addHistory(hist);
			}

		} else {
			final ReminderHistory hist = new ReminderHistory();
			hist.setDate(new Date());
			hist.setType(ReminderChangeType.DELETED);
			hist.setUserId(user.getId());
			hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
			reminderToUpdate.addHistory(hist);
		}

		return reminderDAO.persist(reminderToUpdate, user);
	}

}
