package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ProfileAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProfiles;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

public class GetProfilesAsyncHandler implements AsyncCommandHandler<GetProfiles, ListResult<ProfileDTO>>,
    DispatchListener<GetProfiles, ListResult<ProfileDTO>> {
  @Inject
  ProfileAsyncDAO profileAsyncDAO;

  @Override
  public void execute(GetProfiles command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ProfileDTO>> callback) {
    profileAsyncDAO.getListResult(callback);
  }

  @Override
  public void onSuccess(GetProfiles command, ListResult<ProfileDTO> result, Authentication authentication) {
    for (ProfileDTO profileDTO : result.getData()) {
      profileAsyncDAO.saveOrUpdate(profileDTO);
    }
  }
}
