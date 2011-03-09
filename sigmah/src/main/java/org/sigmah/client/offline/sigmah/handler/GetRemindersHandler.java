/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.handler;

import java.util.ArrayList;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.RemindersResultList;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetReminders} command. Use only local data.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetRemindersHandler implements CommandHandler<GetReminders> {

    @Override
    public CommandResult execute(GetReminders cmd, User user) throws CommandException {
        final ArrayList<ReminderDTO> reminders = new ArrayList<ReminderDTO>();


        final RemindersResultList resultList = new RemindersResultList(reminders);

        return resultList;
    }

}
