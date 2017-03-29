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
public class GetHistoryAsyncHandler implements AsyncCommandHandler<GetHistory, ListResult<HistoryTokenListDTO>>, DispatchListener<GetHistory, ListResult<HistoryTokenListDTO>> {

	private final HistoryAsyncDAO historyAsyncDAO;

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
