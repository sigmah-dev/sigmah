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

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.CountryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCountry;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.dto.country.CountryDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetCountryHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */

public class GetCountryAsyncHandler implements AsyncCommandHandler<GetCountry, CountryDTO>, DispatchListener<GetCountry, CountryDTO> {

	private final CountryAsyncDAO countryDAO;

	
	public GetCountryAsyncHandler(CountryAsyncDAO countryDAO) {
		this.countryDAO = countryDAO;
	}
	
	@Override
	public void execute(GetCountry command, OfflineExecutionContext executionContext, AsyncCallback<CountryDTO> callback) {
		countryDAO.get(command.getId(), callback);
	}

	@Override
	public void onSuccess(GetCountry command, CountryDTO result, Authentication authentication) {
		if(result != null) {
			countryDAO.saveOrUpdate(result);
		}
	}
}
