/*
 *  All Sigmah code is released under the GNU General Public License v3
 *  See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sigmah.handler;

import java.util.ArrayList;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.CountryResult;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.CountryDTO;
import org.sigmah.shared.exception.CommandException;

/**
 * Handler for the {@link GetCountries} command. Use only local data.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetCountriesHandler implements CommandHandler<GetCountries> {

    @Override
    public CommandResult execute(GetCountries cmd, User user) throws CommandException {
        CountryResult result = new CountryResult(new ArrayList<CountryDTO>());

        return result;
    }

}
