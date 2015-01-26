/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.handler;

import java.util.ArrayList;
import org.sigmah.shared.command.GetUsersByOrganization;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.UserListResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetUsersByOrganization} command. Use only local data.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetUsersByOrganizationHandler implements CommandHandler<GetUsersByOrganization> {

    @Override
    public CommandResult execute(GetUsersByOrganization cmd, User user) throws CommandException {
        UserListResult result = new UserListResult(new ArrayList<UserDTO>());

        return result;
    }

}
