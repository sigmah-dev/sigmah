package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.Date;

import javax.persistence.EntityManager;

import org.dozer.Mapper;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.RemindersResultList;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.reminder.Reminder;
import org.sigmah.shared.domain.reminder.ReminderChangeType;
import org.sigmah.shared.domain.reminder.ReminderHistory;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class UpdateRemindersHandler implements CommandHandler<UpdateReminders> {

	private final EntityManager em;
	private final Mapper mapper;

	@Inject
	public UpdateRemindersHandler(EntityManager em, Mapper mapper) {
		this.em = em;
		this.mapper = mapper;
	}

	@Override
	public CommandResult execute(UpdateReminders cmd, User user) throws CommandException {

		final ArrayList<ReminderDTO> resultList = new ArrayList<ReminderDTO>();

		Reminder reminder;
		for (final ReminderDTO reminderDTO : cmd.getList()) {

			// Retrieves entity.
			reminder = em.find(Reminder.class, reminderDTO.getId());

			boolean completionDateChanged = false;
			if (reminder.getCompletionDate() == null && reminderDTO.getCompletionDate() != null)
				completionDateChanged = true;
			if (reminderDTO.getCompletionDate() == null && reminder.getCompletionDate() != null)
				completionDateChanged = true;
			boolean expectedDateChanged = !reminderDTO.getExpectedDate().equals(reminder.getExpectedDate());
			boolean labelChanged = !reminderDTO.getLabel().equals(reminder.getLabel());

			// Updates it.
			reminder.setCompletionDate(reminderDTO.getCompletionDate());
			reminder.setExpectedDate(reminderDTO.getExpectedDate());
			reminder.setLabel(reminderDTO.getLabel());

			// Saves it.
			reminder = em.merge(reminder);

			// History

			if (completionDateChanged) {

				Date lastDateOpened = new Date(0);
				Date lastDateClosed = new Date(0);

				for (ReminderHistory hist : reminder.getHistory()) {
					if (hist.getType() == ReminderChangeType.CLOSED && hist.getDate().after(lastDateClosed))
						lastDateClosed = hist.getDate();
					if (hist.getType() == ReminderChangeType.OPENED && hist.getDate().after(lastDateOpened))
						lastDateOpened = hist.getDate();
				}

				ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				if (lastDateOpened.after(lastDateClosed) || lastDateClosed.equals(lastDateOpened))
					hist.setType(ReminderChangeType.CLOSED);
				else
					hist.setType(ReminderChangeType.OPENED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");

				reminder.addHistory(hist);
			}

			if (labelChanged) {
				ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminder.addHistory(hist);
			}

			if (expectedDateChanged) {
				ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminder.addHistory(hist);
			}

			resultList.add(mapper.map(reminder, ReminderDTO.class));
		}

		return new RemindersResultList(resultList);
	}
}