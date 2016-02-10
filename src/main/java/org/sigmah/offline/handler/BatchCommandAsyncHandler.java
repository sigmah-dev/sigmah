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
import java.util.Arrays;
import java.util.List;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.LocalDispatchServiceAsync;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.BatchCommand;
import org.sigmah.shared.command.base.Command;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.Result;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.BatchCommandHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BatchCommandAsyncHandler implements AsyncCommandHandler<BatchCommand, ListResult<Result>> {
	
	private LocalDispatchServiceAsync dispatcher;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(BatchCommand batch, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<Result>> callback) {
		final List<Command> commands = batch.getCommands();
		final Result[] results = new Result[commands.size()];
		
		final RequestManager<ListResult<Result>> requestManager = new RequestManager<ListResult<Result>>(new ListResult<Result>(), new CommandResultHandler<ListResult<Result>>() {

			@Override
			protected void onCommandSuccess(ListResult<Result> result) {
				result.setList(Arrays.asList(results));
				callback.onSuccess(result);
			}
		});
		
		
		for(int index = 0; index < commands.size(); index++) {
			final int entry = index;
			dispatcher.execute(commands.get(index), new RequestManagerCallback<ListResult<Result>, Result>(requestManager) {
				
				@Override
				public void onRequestSuccess(Result result) {
					results[entry] = result;
				}
			});
		}
		
		requestManager.ready();
	}

	/**
	 * Defines the local dispatcher to use.
	 * 
	 * @param dispatcher Local dispatcher.
	 */
	public void setDispatcher(LocalDispatchServiceAsync dispatcher) {
		this.dispatcher = dispatcher;
	}
	
}
