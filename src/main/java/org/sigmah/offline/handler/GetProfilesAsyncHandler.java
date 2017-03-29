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
 
  ProfileAsyncDAO profileAsyncDAO;
  
  

  public GetProfilesAsyncHandler(ProfileAsyncDAO profileAsyncDAO) {
	this.profileAsyncDAO = profileAsyncDAO;
}

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
