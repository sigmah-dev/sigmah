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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Iterator;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateProjectFavorite;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.UserDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateProjectFavoriteHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class UpdateProjectFavoriteAsyncHandler implements AsyncCommandHandler<UpdateProjectFavorite, CreateResult> {

	private ProjectAsyncDAO projectAsyncDAO;
	
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	
	
	public UpdateProjectFavoriteAsyncHandler(ProjectAsyncDAO projectAsyncDAO, UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
		this.projectAsyncDAO = projectAsyncDAO;
		this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
	}

	@Override
	public void execute(final UpdateProjectFavorite command, final OfflineExecutionContext executionContext, final AsyncCallback<CreateResult> callback) {
		updateDiaryAsyncDAO.saveOrUpdate(command);
		
		projectAsyncDAO.get(command.getProjectId(), new AsyncCallback<ProjectDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				if(result != null) {
					switch(command.getUpdateType()) {
						case ADD:
							result.getFavoriteUsers().add(getUser(executionContext));
							break;

						case REMOVE:
							final Integer userId = executionContext.getAuthentication().getUserId();
							if(userId != null) {
								final Iterator<UserDTO> iterator = result.getFavoriteUsers().iterator();
								while(iterator.hasNext()) {
									final UserDTO userDTO = iterator.next();
									if(userId.equals(userDTO.getId())) {
										iterator.remove();
									}
								}
							} else {
								Log.warn("Current user identifier is null.");
							}
							break;
					}

					// Saving the changes
					projectAsyncDAO.saveOrUpdate(result, new AsyncCallback<ProjectDTO>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(ProjectDTO result) {
							callback.onSuccess(new CreateResult(result));
						}
					});

				} else {
					Log.warn("Project '" + command.getProjectId() + "' not found. Favorite state has not been modified.");
					callback.onSuccess(null);
				}
			}
		});
	}
	
	private UserDTO getUser(OfflineExecutionContext executionContext) {
		final Authentication authentication = executionContext.getAuthentication();
		
		final UserDTO userDTO = new UserDTO();
		userDTO.setId(authentication.getUserId());
		userDTO.setName(authentication.getUserName());
		userDTO.setFirstName(authentication.getUserFirstName());
		userDTO.setEmail(authentication.getUserEmail());
		
		return userDTO;
	}
	
}
