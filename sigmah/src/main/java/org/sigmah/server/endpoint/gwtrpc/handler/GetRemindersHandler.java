/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.dozer.Mapper;
import org.hibernate.Session;
import org.hibernate.ejb.HibernateEntityManager;
import org.sigmah.shared.command.GetReminders;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.RemindersResultList;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.reminder.Reminder;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetReminders} command.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetRemindersHandler implements CommandHandler<GetReminders> {

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetRemindersHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(GetReminders cmd, User user) throws CommandException {
        final ArrayList<ReminderDTO> dtos = new ArrayList<ReminderDTO>();

        final Session session = ((HibernateEntityManager) em).getSession();
        session.disableFilter("userVisible");

        // Use a set to be avoid duplicated entries.
        final HashSet<OrgUnit> units = new HashSet<OrgUnit>();

        // Crawl the org units hierarchy from the user root org unit.
        GetProjectHandler.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, false);

        // Retrieves all the corresponding org units.
        for (final OrgUnit unit : units) {

            // Builds and executes the query.
            final Query query = em.createQuery("SELECT p.remindersList.reminders FROM Project p WHERE :unit MEMBER OF p.partners");
            query.setParameter("unit", unit);

            final List<Reminder> reminders = (List<Reminder>) query.getResultList();
            for (final Reminder reminder : reminders) {
                if(reminder.getCompletionDate() == null) // Not completed only
                    dtos.add(mapper.map(reminder, ReminderDTO.class));
            }

        }

        final RemindersResultList result = new RemindersResultList();
        result.setList(dtos);

        return result;
    }
    
}
