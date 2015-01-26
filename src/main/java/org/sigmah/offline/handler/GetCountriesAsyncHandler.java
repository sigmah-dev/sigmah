package org.sigmah.offline.handler;

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
	
	@Override
	public void execute(GetCountries command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<CountryDTO>> callback) {
		// TODO: Should handle the "containingProjects" property
		// Maybe add a boolean in the local database to allow the usage of an Index,
		// instead of having to make a join.
		countryDAO.getAll(callback);
	}

	@Override
	public void onSuccess(GetCountries command, ListResult<CountryDTO> result, Authentication authentication) {
		countryDAO.saveOrUpdate(result);
	}

}
