package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ProjectModelJS;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectModelAsyncDAO extends AbstractAsyncDAO<ProjectModelDTO> {
	
	private final PhaseModelAsyncDAO phaseModelAsyncDAO;

	@Inject
	public ProjectModelAsyncDAO(PhaseModelAsyncDAO phaseModelAsyncDAO) {
		this.phaseModelAsyncDAO = phaseModelAsyncDAO;
	}

	@Override
	public void saveOrUpdate(final ProjectModelDTO t, AsyncCallback<ProjectModelDTO> callback, Transaction transaction) {
		final ObjectStore projectModelStore = transaction.getObjectStore(Store.PROJECT_MODEL);
		
		final ProjectModelJS projectModelJS = ProjectModelJS.toJavaScript(t);
		projectModelStore.put(projectModelJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving project model " + t.getId() + '.', caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Project model " + t.getId() + " has been successfully saved.");
            }
        });
	}

	@Override
	public void get(int id, final AsyncCallback<ProjectModelDTO> callback, final Transaction transaction) {
		if(transaction.useObjectFromCache(ProjectModelDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore projectModelStore = transaction.getObjectStore(Store.PROJECT_MODEL);

		projectModelStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final ProjectModelJS projectModelJS = request.getResult();
				if(projectModelJS != null) {
					final ProjectModelDTO projectModelDTO = projectModelJS.toDTO();

					final RequestManager<ProjectModelDTO> requestManager = new RequestManager<ProjectModelDTO>(projectModelDTO, callback);

					final JsArrayInteger phaseModels = projectModelJS.getPhaseModels();
					if(phaseModels != null) {
						final ArrayList<PhaseModelDTO> dtos = new ArrayList<PhaseModelDTO>();
						projectModelDTO.setPhaseModels(dtos);

						for(int index = 0; index < phaseModels.length(); index++) {
							phaseModelAsyncDAO.get(phaseModels.get(index), new RequestManagerCallback<ProjectModelDTO, PhaseModelDTO>(requestManager) {
								@Override
								public void onRequestSuccess(PhaseModelDTO result) {
									dtos.add(result);
								}
							}, transaction);
						}
					}

					requestManager.ready();
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.PROJECT_MODEL;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> dependencies = new ArrayList<BaseAsyncDAO>();
		dependencies.add(phaseModelAsyncDAO);
		return dependencies;
	}
	
}
