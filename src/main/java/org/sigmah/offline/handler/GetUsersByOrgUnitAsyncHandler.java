package org.sigmah.offline.handler;

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

	@Inject
	GetUsersByOrgUnitAsyncHandler(OrgUnitAsyncDAO orgUnitDAO, UserAsyncDAO userDAO) {
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
				Set<Integer> orgUnitIds = new HashSet<Integer>();
				orgUnitIds.add(orgUnit.getId());
				for (OrgUnitDTO child : orgUnit.getChildrenOrgUnits()) {
					orgUnitIds.add(child.getId());
				}

				userDAO.getByOrgUnits(orgUnitIds, callback);
			}
		});
	}

	@Override
	public void onSuccess(GetUsersByOrgUnit command, ListResult<UserDTO> result, Authentication authentication) {
		for (UserDTO userDTO : result.getData()) {
			userDAO.saveOrUpdate(userDTO);
		}
	}
}
