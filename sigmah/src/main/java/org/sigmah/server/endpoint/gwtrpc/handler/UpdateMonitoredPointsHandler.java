package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.sigmah.shared.command.UpdateMonitoredPoints;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.MonitoredPointsResultList;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.reminder.MonitoredPoint;
import org.sigmah.shared.domain.reminder.MonitoredPointHistory;
import org.sigmah.shared.domain.reminder.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class UpdateMonitoredPointsHandler implements CommandHandler<UpdateMonitoredPoints> {

	private final EntityManager em;
	private final Mapper mapper;

	@Inject
	public UpdateMonitoredPointsHandler(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
	}

	@Override
	public CommandResult execute(UpdateMonitoredPoints cmd, User user) throws CommandException {

		final ArrayList<MonitoredPointDTO> resultList = new ArrayList<MonitoredPointDTO>();

		MonitoredPoint point;
		for (final MonitoredPointDTO pointDTO : cmd.getList()) {

			// Retrieves entity.
			point = em.find(MonitoredPoint.class, pointDTO.getId());

			boolean completionDateChanged = false;
			if (point.getCompletionDate() == null && pointDTO.getCompletionDate() != null)
				completionDateChanged = true;
			if (pointDTO.getCompletionDate() == null && point.getCompletionDate() != null)
				completionDateChanged = true;
			boolean expectedDateChanged = !pointDTO.getExpectedDate().equals(point.getExpectedDate());
			boolean labelChanged = !pointDTO.getLabel().equals(point.getLabel());

			// Updates it.
			point.setCompletionDate(pointDTO.getCompletionDate());
			point.setExpectedDate(pointDTO.getExpectedDate());
			point.setLabel(pointDTO.getLabel());

			// Saves it.
			point = em.merge(point);

			// History

			if (completionDateChanged) {

				Date lastDateOpened = new Date(0);
				Date lastDateClosed = new Date(0);

				for (MonitoredPointHistory hist : point.getHistory()) {
					if (hist.getType() == ReminderChangeType.CLOSED && hist.getDate().after(lastDateClosed))
						lastDateClosed = hist.getDate();
					if (hist.getType() == ReminderChangeType.OPENED && hist.getDate().after(lastDateOpened))
						lastDateOpened = hist.getDate();
				}

				MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				if (lastDateOpened.after(lastDateClosed) || lastDateClosed.equals(lastDateOpened))
					hist.setType(ReminderChangeType.CLOSED);
				else
					hist.setType(ReminderChangeType.OPENED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");

				point.addHistory(hist);
			}

			if (labelChanged) {
				MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

			if (expectedDateChanged) {
				MonitoredPointHistory hist = new MonitoredPointHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				point.addHistory(hist);
			}

			resultList.add(mapper.map(point, MonitoredPointDTO.class));
		}

		return new MonitoredPointsResultList(resultList);
	}
}
