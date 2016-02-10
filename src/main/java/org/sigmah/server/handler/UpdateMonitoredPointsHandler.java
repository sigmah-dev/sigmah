package org.sigmah.server.handler;

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
import java.util.List;

import org.sigmah.server.dao.MonitoredPointDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.reminder.MonitoredPointHistory;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link UpdateMonitoredPoints} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateMonitoredPointsHandler extends AbstractCommandHandler<UpdateMonitoredPoints, ListResult<MonitoredPointDTO>> {

	/**
	 * Injected DAO.
	 */
	@Inject
	private MonitoredPointDAO monitoredPointDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<MonitoredPointDTO> execute(final UpdateMonitoredPoints cmd, final UserExecutionContext context) throws CommandException {

		final List<MonitoredPointDTO> resultList = new ArrayList<MonitoredPointDTO>();
		final User user = context.getUser();

		performUpdate(cmd.getList(), user, context, resultList);

		return new ListResult<>(resultList);
	}

	/**
	 * Update the given reminders in a transaction.
	 * 
	 * @param monitoredPoints List of reminders to update.
	 * @param user Current user.
	 * @param context Execution context.
	 * @param resultList List of results.
	 */
	@Transactional
	protected void performUpdate(final List<MonitoredPointDTO> monitoredPoints, final User user, final UserExecutionContext context, final List<MonitoredPointDTO> resultList) {
		for (final MonitoredPointDTO pointDTO : monitoredPoints) {
			
			// Retrieves entity.
			MonitoredPoint point = monitoredPointDAO.findById(pointDTO.getId());

			boolean completionDateChanged = false;
			if (point.getCompletionDate() == null && pointDTO.getCompletionDate() != null) {
				completionDateChanged = true;
			}
			if (pointDTO.getCompletionDate() == null && point.getCompletionDate() != null) {
				completionDateChanged = true;
			}
			final boolean expectedDateChanged = !pointDTO.getExpectedDate().equals(point.getExpectedDate());
			final boolean labelChanged = !pointDTO.getLabel().equals(point.getLabel());

			// Updates it.
			point.setCompletionDate(pointDTO.getCompletionDate());
			point.setExpectedDate(pointDTO.getExpectedDate());
			point.setLabel(pointDTO.getLabel());

			// History.
			if (completionDateChanged) {

				Date lastDateOpened = new Date(0);
				Date lastDateClosed = new Date(0);

				for (final MonitoredPointHistory hist : point.getHistory()) {
					if (hist.getType() == ReminderChangeType.CLOSED && hist.getDate().after(lastDateClosed)) {
						lastDateClosed = hist.getDate();
					}
					if (hist.getType() == ReminderChangeType.OPENED && hist.getDate().after(lastDateOpened)) {
						lastDateOpened = hist.getDate();
					}
				}

				final MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());

				if (lastDateOpened.after(lastDateClosed) || lastDateClosed.equals(lastDateOpened)) {
					hist.setType(ReminderChangeType.CLOSED);
				} else {
					hist.setType(ReminderChangeType.OPENED);
				}

				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");

				point.addHistory(hist);
			}

			if (labelChanged) {
				final MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

			if (expectedDateChanged) {
				final MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

			// Saves it.
			point = monitoredPointDAO.persist(point, context.getUser());

			resultList.add(mapper().map(point, new MonitoredPointDTO(), MonitoredPointDTO.Mode.FULL));
		}
	}
}
