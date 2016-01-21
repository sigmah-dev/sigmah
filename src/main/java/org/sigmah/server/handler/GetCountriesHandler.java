package org.sigmah.server.handler;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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

		return new ListResult<CountryDTO>(mapper().mapCollection(countries, CountryDTO.class, cmd.getMappingMode()));
	}

}
