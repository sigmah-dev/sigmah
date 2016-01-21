package org.sigmah.server.handler;

import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Country;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.country.CountryDTO;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetCountry} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class GetCountryHandler extends AbstractCommandHandler<GetCountry, CountryDTO> {

	private final CountryDAO countryDAO;

	@Inject
	public GetCountryHandler(final CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CountryDTO execute(final GetCountry cmd, final UserExecutionContext context) throws CommandException {

		final Country country = countryDAO.findById(cmd.getId());

		return mapper().map(country, new CountryDTO());
	}

}
