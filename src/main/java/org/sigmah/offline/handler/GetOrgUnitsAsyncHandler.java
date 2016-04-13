package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetOrgUnits;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetOrgUnitsHandler}.
 * Used when the user is offline.
 */
@Singleton
public class GetOrgUnitsAsyncHandler implements AsyncCommandHandler<GetOrgUnits, ListResult<OrgUnitDTO>>, DispatchListener<GetOrgUnits, ListResult<OrgUnitDTO>> {

	private final OrgUnitAsyncDAO orgUnitAsyncDAO;

	@Inject
	public GetOrgUnitsAsyncHandler(OrgUnitAsyncDAO orgUnitAsyncDAO) {
		this.orgUnitAsyncDAO = orgUnitAsyncDAO;
	}

	@Override
	public void execute(final GetOrgUnits command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<OrgUnitDTO>> callback) {
		final List<OrgUnitDTO> orgUnitDTOs = new ArrayList<OrgUnitDTO>();
		final int[] requests = new int[]{0};
		for (Integer orgUnitId : command.getOrgUnitIds()) {
			orgUnitAsyncDAO.get(orgUnitId, new AsyncCallback<OrgUnitDTO>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(OrgUnitDTO orgUnitDTO) {
					orgUnitDTOs.add(orgUnitDTO);

					if (++requests[0] == command.getOrgUnitIds().size()) {
						callback.onSuccess(new ListResult<OrgUnitDTO>(orgUnitDTOs));
					}
				}
			});
		}
	}

	@Override
	public void onSuccess(GetOrgUnits command, ListResult<OrgUnitDTO> result, Authentication authentication) {
		for (OrgUnitDTO orgUnitDTO : result.getData()) {
			orgUnitAsyncDAO.saveOrUpdate(orgUnitDTO);
		}
	}
}
