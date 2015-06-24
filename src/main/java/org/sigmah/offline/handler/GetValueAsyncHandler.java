package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ValueResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetValueHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetValueAsyncHandler implements AsyncCommandHandler<GetValue, ValueResult>, DispatchListener<GetValue, ValueResult> {
	
	private final ValueAsyncDAO valueAsyncDAO;

	@Inject
	public GetValueAsyncHandler(ValueAsyncDAO valueAsyncDAO) {
		this.valueAsyncDAO = valueAsyncDAO;
	}
	
	@Override
	public void execute(GetValue command, OfflineExecutionContext executionContext, AsyncCallback<ValueResult> callback) {
		valueAsyncDAO.get(command, callback);
	}

	@Override
	public void onSuccess(GetValue command, ValueResult result, Authentication authentication) {
		if(result != null) {
			valueAsyncDAO.saveOrUpdate(command, result);
		}
	}
}
