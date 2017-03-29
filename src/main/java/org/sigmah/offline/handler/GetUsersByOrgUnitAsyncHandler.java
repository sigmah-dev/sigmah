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

import java.util.HashSet;
import java.util.Set;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dao.UserAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetUsersByOrgUnit;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

public class GetUsersByOrgUnitAsyncHandler implements AsyncCommandHandler<GetUsersByOrgUnit, ListResult<UserDTO>>,
	DispatchListener<GetUsersByOrgUnit, ListResult<UserDTO>> {

	private final OrgUnitAsyncDAO orgUnitDAO;
	private final UserAsyncDAO userDAO;


	public GetUsersByOrgUnitAsyncHandler(OrgUnitAsyncDAO orgUnitDAO, UserAsyncDAO userDAO) {
		this.orgUnitDAO = orgUnitDAO;
		this.userDAO = userDAO;
	}

	@Override
	public void execute(final GetUsersByOrgUnit command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<UserDTO>> callback) {
		orgUnitDAO.get(command.getOrgUnitId(), new AsyncCallback<OrgUnitDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(OrgUnitDTO orgUnit) {
				userDAO.getByOrgUnits(traverseOrgUnitIds(orgUnit), callback);
			}
		});
	}

	private Set<Integer> traverseOrgUnitIds(OrgUnitDTO orgUnit) {
		Set<Integer> orgUnitIds = new HashSet<Integer>();
		OrgUnitDTO parent = orgUnit;
		while (parent != null) {
			orgUnitIds.add(parent.getId());

			parent = parent.getParentOrgUnit();
		}
		return orgUnitIds;
	}

	@Override
	public void onSuccess(GetUsersByOrgUnit command, ListResult<UserDTO> result, Authentication authentication) {
		for (UserDTO userDTO : result.getData()) {
			userDAO.saveOrUpdate(userDTO);
		}
	}
}
