package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sigmah.server.dao.ReminderDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.reminder.Reminder;
import org.sigmah.server.domain.reminder.ReminderHistory;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.ReminderDTO;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;

/**
 * Handler for {@link UpdateReminders} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateRemindersHandler extends AbstractCommandHandler<UpdateReminders, ListResult<ReminderDTO>> {

	/**
	 * Injected DAO.
	 */
	@Inject
	private ReminderDAO reminderDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ReminderDTO> execute(final UpdateReminders cmd, final UserExecutionContext context) throws CommandException {

		final List<ReminderDTO> resultList = new ArrayList<ReminderDTO>();
		final User user = context.getUser();

		performUpdate(cmd.getList(), user, resultList);

		return new ListResult<>(resultList);
	}

	/**
	 * Update the given reminders in a transaction.
	 * 
	 * @param reminders List of reminders to update.
	 * @param user Current user.
	 * @param resultList List of results.
	 */
	@Transactional
	protected void performUpdate(final List<ReminderDTO> reminders, final User user, final List<ReminderDTO> resultList) {
		for (final ReminderDTO reminderDTO : reminders) {
			
			// Retrieves entity.
			Reminder reminder = reminderDAO.findById(reminderDTO.getId());

			boolean completionDateChanged = false;
			if (reminder.getCompletionDate() == null && reminderDTO.getCompletionDate() != null) {
				completionDateChanged = true;
			}
			if (reminderDTO.getCompletionDate() == null && reminder.getCompletionDate() != null) {
				completionDateChanged = true;
			}

			final boolean expectedDateChanged = !reminderDTO.getExpectedDate().equals(reminder.getExpectedDate());
			final boolean labelChanged = !reminderDTO.getLabel().equals(reminder.getLabel());

			// Updates it.
			reminder.setCompletionDate(reminderDTO.getCompletionDate());
			reminder.setExpectedDate(reminderDTO.getExpectedDate());
			reminder.setLabel(reminderDTO.getLabel());

			// History.
			if (completionDateChanged) {

				Date lastDateOpened = new Date(0);
				Date lastDateClosed = new Date(0);

				for (final ReminderHistory hist : reminder.getHistory()) {
					if (hist.getType() == ReminderChangeType.CLOSED && hist.getDate().after(lastDateClosed)) {
						lastDateClosed = hist.getDate();
					}
					if (hist.getType() == ReminderChangeType.OPENED && hist.getDate().after(lastDateOpened)) {
						lastDateOpened = hist.getDate();
					}
				}

				final ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				if (lastDateOpened.after(lastDateClosed) || lastDateClosed.equals(lastDateOpened)) {
					hist.setType(ReminderChangeType.CLOSED);
				} else {
					hist.setType(ReminderChangeType.OPENED);
				}
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");

				reminder.addHistory(hist);
			}

			if (labelChanged) {
				final ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.LABEL_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminder.addHistory(hist);
			}

			if (expectedDateChanged) {
				final ReminderHistory hist = new ReminderHistory();
				hist.setDate(new Date());
				hist.setType(ReminderChangeType.DATE_MODIFIED);
				hist.setUserId(user.getId());
				hist.setValue(user.getName() + ", " + user.getFirstName() + " <" + user.getEmail() + ">");
				reminder.addHistory(hist);
			}

			// Saves it.
			reminder = reminderDAO.persist(reminder, user);

			resultList.add(mapper().map(reminder, ReminderDTO.class, ReminderDTO.Mode.WITH_HISTORY));
		}
	}

}
