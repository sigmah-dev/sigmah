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
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.UpdateProject;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.element.event.ValueEventWrapper;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.client.i18n.I18N;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.sync.SuccessCallback;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;
import org.sigmah.shared.dto.referential.ContainerInformation;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateProjectHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateProjectAsyncHandler implements AsyncCommandHandler<UpdateProject, VoidResult>, DispatchListener<UpdateProject, VoidResult> {

    @Inject
	private ValueAsyncDAO valueAsyncDAO;
    
    @Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
    
    @Inject
    private ProjectAsyncDAO projectAsyncDAO;

    /**
     * {@inheritDoc}
     */
	@Override
	public void execute(final UpdateProject command, final OfflineExecutionContext executionContext, final AsyncCallback<VoidResult> callback) {
		// Updating the local database
		final RequestManager<VoidResult> requestManager = new RequestManager<VoidResult>(null, callback);
        
        try {
            checkComputations(command.getValues(), command.getProjectId());
        } catch (UpdateConflictException e) {
            callback.onFailure(e);
            return;
        }
        
		for(final ValueEventWrapper valueEventWrapper : command.getValues()) {
			final String id = ValueJSIdentifierFactory.toIdentifier(command, valueEventWrapper);
			Log.info("Modification de la valeur de l'élément " + id);
			
			final int futureRequestId = requestManager.prepareRequest();
			final int delayId = requestManager.prepareRequest();
			valueAsyncDAO.get(id, new RequestManagerCallback<VoidResult, ValueResult>(requestManager) {
				@Override
				public void onRequestSuccess(ValueResult result) {
					valueAsyncDAO.saveOrUpdate(command, valueEventWrapper, result, new RequestManagerCallback<VoidResult, VoidResult>(requestManager, futureRequestId) {
						@Override
						public void onRequestSuccess(VoidResult result) {
                            // Delay the callback to allow IndexedDB to cleanly
                            // close its transaction.
                            new Timer() {
                                @Override
                                public void run() {
                                    requestManager.setRequestSuccess(delayId);
                                }
                            }.schedule(100);
						}
					});
				}
			});
		}
		
		// Saving the action in the local database
		updateDiaryAsyncDAO.saveOrUpdate(command);
		requestManager.ready();
	}

    /**
     * {@inheritDoc}
     */ 
	@Override
	public void onSuccess(final UpdateProject command, VoidResult result, Authentication authentication) {
		// Updating local database
		for(final ValueEventWrapper valueEventWrapper : command.getValues()) {
			final String id = ValueJSIdentifierFactory.toIdentifier(command, valueEventWrapper);
			
			valueAsyncDAO.get(id, new AsyncCallback<ValueResult>() {
				@Override
				public void onFailure(Throwable caught) {
					Log.warn("Error while updating local database for element '" + id + "'.", caught);
				}

				@Override
				public void onSuccess(ValueResult result) {
					valueAsyncDAO.saveOrUpdate(command, valueEventWrapper, result, new AsyncCallback<VoidResult>() {
						@Override
						public void onFailure(Throwable caught) {
						}
						@Override
						public void onSuccess(VoidResult result) {
						}
					});
				}
			});
		}
	}
    
    /**
     * Search for computations and verify if the value matches the constraints of the field.
     * 
     * @param valueEvents List of changes.
     */
    private void checkComputations(final List<ValueEventWrapper> valueEvents, int projectId) throws UpdateConflictException {
        
        final ArrayList<String> conflicts = new ArrayList<String>();
        
        for (final ValueEventWrapper valueEvent : valueEvents) {
            final FlexibleElementDTO source = valueEvent.getSourceElement();
            if (source instanceof ComputationElementDTO && ((ComputationElementDTO) source).hasConstraints()) {
                final ComputationElementDTO computationElement = (ComputationElementDTO) source;
                final ComputedValue clientResult = ComputedValues.from(valueEvent.getSingleValue());
                
                switch (clientResult.matchesConstraints(computationElement)) {
                    case -1:
                        conflicts.add(I18N.MESSAGES.conflictComputationTooLowOffline(computationElement.getLabel(), valueEvent.getSingleValue(), computationElement.getMinimumValue()));
                        break;
                    case 1:
                        conflicts.add(I18N.MESSAGES.conflictComputationTooHighOffline(computationElement.getLabel(), valueEvent.getSingleValue(), computationElement.getMaximumValue()));
                        break;
                    default:
                        break;
                }
            }
        }
        
        if(!conflicts.isEmpty()) {
            final ContainerInformation containerInformation = new ContainerInformation(projectId, I18N.CONSTANTS.loading(), I18N.CONSTANTS.loading(), true);
            projectAsyncDAO.getWithoutDependencies(projectId, new SuccessCallback<ProjectDTO>() {
                
                @Override
                public void onSuccess(ProjectDTO result) {
                    containerInformation.setName(result.getName());
                    containerInformation.setFullName(result.getFullName());
                }
            });
            
			// A conflict was found.
			throw new UpdateConflictException(containerInformation, conflicts.toArray(new String[0]));
		}
    }
	
}
