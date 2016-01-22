package org.sigmah.offline.handler;

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

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.CountryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCountries;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.country.CountryDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetCountriesHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetCountriesAsyncHandler implements AsyncCommandHandler<GetCountries, ListResult<CountryDTO>>, DispatchListener<GetCountries, ListResult<CountryDTO>> {

	private final CountryAsyncDAO countryDAO;

	@Inject
	public GetCountriesAsyncHandler(CountryAsyncDAO countryDAO) {
		this.countryDAO = countryDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(GetCountries command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<CountryDTO>> callback) {
		// TODO: Should handle the "containingProjects" property
		// Maybe add a boolean in the local database to allow the usage of an Index,
		// instead of having to make a join.
		countryDAO.getListResult(callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetCountries command, ListResult<CountryDTO> result, Authentication authentication) {
		countryDAO.saveAll(result, null);
	}

}
