/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.handler;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.gears.client.Factory;
import com.google.gwt.gears.client.database.Database;
import com.google.gwt.gears.client.database.DatabaseException;
import org.sigmah.client.offline.sigmah.OnlineMode;
import org.sigmah.client.offline.sigmah.dao.OrganizationDAO;
import org.sigmah.shared.command.GetOrganization;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.OrganizationDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetOrganization} command. Use only local data.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetOrganizationHandler implements CommandHandler<GetOrganization> {

    @Override
    public CommandResult execute(GetOrganization cmd, User user) throws CommandException {
        OrganizationDTO organizationDTO = null;

        final Factory factory = Factory.getInstance();
        if(factory != null) {

            final Database database = factory.createDatabase();
            database.open(OnlineMode.LOCAL_DATABASE_NAME);
            try {
                organizationDTO = OrganizationDAO.selectOrganization(cmd.getId(), database);

            } catch (DatabaseException ex) {
                Log.debug("Error while reading the organization dto "+cmd.getId()+" from the local database.", ex);

            } finally {
                try {
                    database.close();
                } catch (DatabaseException ex) {
                    Log.debug("Database closing error.", ex);
                }
            }
        }

        return organizationDTO;
    }
    
}
