package org.sigmah.server.handler;

import java.util.List;

import org.sigmah.server.dao.CountryDAO;
import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Country;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.country.CountryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import java.util.ArrayList;

/**
 * Handler for {@link GetCountries} command
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class GetCountriesHandler extends AbstractCommandHandler<GetCountries, ListResult<CountryDTO>> {

	/**
	 * Logger.
	 */
	private static final Logger LOG = LoggerFactory.getLogger(GetCountriesHandler.class);

	private final CountryDAO countryDAO;

	@Inject
	public GetCountriesHandler(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<CountryDTO> execute(final GetCountries cmd, final UserExecutionContext context) throws CommandException {

		LOG.debug("Get countries with projects? {}.", cmd.isContainingProjects());

		final List<Country> countries;

		if (cmd.isContainingProjects()) {
			final String query = "SELECT c FROM Country c WHERE c IN (SELECT co.id FROM Project p, IN(p.country) co)";
			countries = em().createQuery(query, Country.class).getResultList();

		} else {
			countries = countryDAO.queryAllCountriesAlphabetically();
		}
		
		final ArrayList<CountryDTO> dtos = new ArrayList<>();
		for (final Country country : countries) {
			dtos.add(mapper().map(country, new CountryDTO()));
		}

		return new ListResult<>(dtos);
	}

}
