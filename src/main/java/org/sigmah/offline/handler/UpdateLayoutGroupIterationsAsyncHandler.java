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
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateLayoutGroupIterations;
import org.sigmah.shared.command.UpdateLayoutGroupIterations.IterationChange;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

public class UpdateLayoutGroupIterationsAsyncHandler implements AsyncCommandHandler<UpdateLayoutGroupIterations, ListResult<IterationChange>>,
    DispatchListener<UpdateLayoutGroupIterations, ListResult<IterationChange>> {

  private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

  @Inject
  public UpdateLayoutGroupIterationsAsyncHandler(UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
    this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
  }

	@Override
	public void execute(final UpdateLayoutGroupIterations command, final OfflineExecutionContext executionContext,
			final AsyncCallback<ListResult<IterationChange>> callback) {

	final RequestManager<ListResult<IterationChange>> requestManager = new RequestManager<ListResult<IterationChange>>(null, callback);

	callback.onSuccess(new ListResult<IterationChange>(command.getIterationChanges()));

	// Saving the action in the local database
	updateDiaryAsyncDAO.saveOrUpdate(command);

	requestManager.ready();
  }


  @Override
  public void onSuccess(final UpdateLayoutGroupIterations command, ListResult<IterationChange> result, Authentication authentication) {
	// Updating the local database
  }

}
