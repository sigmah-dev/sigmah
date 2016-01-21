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

		return mapper().map(country, CountryDTO.class);
	}

}
