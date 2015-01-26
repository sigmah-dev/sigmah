package org.sigmah.offline.handler;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.HistoryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetHistory;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetHistoryHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetHistoryAsyncHandler implements AsyncCommandHandler<GetHistory, ListResult<HistoryTokenListDTO>>, DispatchListener<GetHistory, ListResult<HistoryTokenListDTO>> {

	private final HistoryAsyncDAO historyAsyncDAO;

	@Inject
	public GetHistoryAsyncHandler(HistoryAsyncDAO historyAsyncDAO) {
		this.historyAsyncDAO = historyAsyncDAO;
	}
	
	@Override
	public void execute(GetHistory command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<HistoryTokenListDTO>> callback) {
		historyAsyncDAO.get(command, callback);
	}

	@Override
	public void onSuccess(GetHistory command, ListResult<HistoryTokenListDTO> result, Authentication authentication) {
		historyAsyncDAO.saveOrUpdate(command, result);
	}
}
