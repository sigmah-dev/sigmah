package org.sigmah.offline.handler;

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
@Singleton
public class GetCountryAsyncHandler implements AsyncCommandHandler<GetCountry, CountryDTO>, DispatchListener<GetCountry, CountryDTO> {

	private final CountryAsyncDAO countryDAO;

	@Inject
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
