package org.sigmah.offline.handler;

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
import org.sigmah.shared.command.result.Authentication;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateProjectHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateProjectAsyncHandler implements AsyncCommandHandler<UpdateProject, VoidResult>, DispatchListener<UpdateProject, VoidResult> {

	private final ValueAsyncDAO valueAsyncDAO;
	private final UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

	@Inject
	public UpdateProjectAsyncHandler(ValueAsyncDAO valueAsyncDAO, UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
		this.valueAsyncDAO = valueAsyncDAO;
		this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
	}
	
	
	@Override
	public void execute(final UpdateProject command, final OfflineExecutionContext executionContext, final AsyncCallback<VoidResult> callback) {
		// Updating the local database
		final RequestManager<VoidResult> requestManager = new RequestManager<VoidResult>(null, callback);
		
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
	
}
