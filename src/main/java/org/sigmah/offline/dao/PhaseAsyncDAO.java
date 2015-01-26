package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PhaseJS;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.PhaseModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PhaseAsyncDAO extends AbstractAsyncDAO<PhaseDTO> {
	
	private final PhaseModelAsyncDAO phaseModelAsyncDAO;

	@Inject
	public PhaseAsyncDAO(PhaseModelAsyncDAO phaseModelAsyncDAO) {
		this.phaseModelAsyncDAO = phaseModelAsyncDAO;
	}
	
	@Override
	public void saveOrUpdate(PhaseDTO t, AsyncCallback<PhaseDTO> callback, Transaction transaction) {
		final ObjectStore phaseStore = transaction.getObjectStore(Store.PHASE);
		
		final PhaseJS phaseJS = PhaseJS.toJavaScript(t);
		phaseStore.put(phaseJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving phase " + phaseJS.getId() + '.', caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Phase " + phaseJS.getId() + " has been successfully saved.");
            }
        });
		
		// Saving the phase model
		phaseModelAsyncDAO.saveOrUpdate(t.getPhaseModel(), null, transaction);
	}
	
	public void saveOrUpdate(List<PhaseDTO> phases, Transaction transaction) {
		for(final PhaseDTO phase : phases) {
			saveOrUpdate(phase, null, transaction);
		}
	}

	@Override
	public void get(int id, final AsyncCallback<PhaseDTO> callback, final Transaction transaction) {
		// Searching in the transaction cache
		if(transaction.useObjectFromCache(PhaseDTO.class, id, callback)) {
			return;
		}
		
		// Loading from the database
		final ObjectStore phaseStore = transaction.getObjectStore(Store.PHASE);

		phaseStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final PhaseJS phaseJS = request.getResult();
				if(phaseJS != null) {
					final PhaseDTO phaseDTO = phaseJS.toDTO();

					final RequestManager<PhaseDTO> requestManager = new RequestManager<PhaseDTO>(phaseDTO, callback);

					phaseModelAsyncDAO.get(phaseJS.getPhaseModel(), new RequestManagerCallback<PhaseDTO, PhaseModelDTO>(requestManager) {
						@Override
						public void onRequestSuccess(PhaseModelDTO result) {
							phaseDTO.setPhaseModel(result);
						}
					}, transaction);

					requestManager.ready();
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.PHASE;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(phaseModelAsyncDAO);
		return list;
	}
}
