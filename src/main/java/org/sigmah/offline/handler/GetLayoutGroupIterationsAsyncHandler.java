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
import org.sigmah.offline.dao.LayoutGroupIterationsAsyncDAO;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetLayoutGroupIterations;
import org.sigmah.shared.command.GetProject;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.NotCachedException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.layout.LayoutGroupIterationDTO;

@Singleton
public class GetLayoutGroupIterationsAsyncHandler implements AsyncCommandHandler<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>>, DispatchListener<GetLayoutGroupIterations, ListResult<LayoutGroupIterationDTO>> {

	private final LayoutGroupIterationsAsyncDAO layoutGroupIterationsAsyncDAO;

	@Inject
	public GetLayoutGroupIterationsAsyncHandler(LayoutGroupIterationsAsyncDAO layoutGroupIterationsAsyncDAO) {
		this.layoutGroupIterationsAsyncDAO = layoutGroupIterationsAsyncDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final GetLayoutGroupIterations command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<LayoutGroupIterationDTO>> callback) {
		layoutGroupIterationsAsyncDAO.getListResult(command.getContainerId(), command.getLayoutGroupId(), command.getAmendmentId(), callback);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(GetLayoutGroupIterations command, ListResult<LayoutGroupIterationDTO> result, Authentication authentication) {
		for (LayoutGroupIterationDTO layoutGroupIterationDTO : result.getData()) {
			layoutGroupIterationsAsyncDAO.saveOrUpdate(layoutGroupIterationDTO);
		}
	}
}
