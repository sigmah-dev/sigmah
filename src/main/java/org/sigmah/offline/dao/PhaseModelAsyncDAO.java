package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PhaseModelJS;
import org.sigmah.shared.dto.PhaseModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PhaseModelAsyncDAO extends AbstractAsyncDAO<PhaseModelDTO> {

	@Override
	public void saveOrUpdate(PhaseModelDTO t, AsyncCallback<PhaseModelDTO> callback, Transaction transaction) {
		final ObjectStore phaseModelStore = transaction.getObjectStore(Store.PHASE_MODEL);

		final PhaseModelJS phaseModelJS = PhaseModelJS.toJavaScript(t);
		phaseModelStore.put(phaseModelJS).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while saving phase model " + phaseModelJS.getId() + '.', caught);
			}

			@Override
			public void onSuccess(Request result) {
				Log.trace("Phase model " + phaseModelJS.getId() + " has been successfully saved.");
			}
		});
	}

	@Override
	public void get(final int id, final AsyncCallback<PhaseModelDTO> callback, final Transaction transaction) {
		if (transaction.useObjectFromCache(PhaseModelDTO.class, id, callback)) {
			return;
		}

		final ObjectStore phaseModelStore = transaction.getObjectStore(Store.PHASE_MODEL);

		phaseModelStore.get(id).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Request request) {
				final PhaseModelJS phaseModelJS = request.getResult();
				if (phaseModelJS != null) {
					final PhaseModelDTO phaseModelDTO = phaseModelJS.toDTO();
					transaction.getObjectCache().put(id, phaseModelDTO);

					final RequestManager<PhaseModelDTO> requestManager = new RequestManager<PhaseModelDTO>(phaseModelDTO, callback);

					final JsArrayInteger successors = phaseModelJS.getSuccessors();
					if (successors != null) {
						final ArrayList<PhaseModelDTO> dtos = new ArrayList<PhaseModelDTO>();
						phaseModelDTO.setSuccessors(dtos);

						for (int index = 0; index < successors.length(); index++) {
							get(successors.get(index), new RequestManagerCallback<PhaseModelDTO, PhaseModelDTO>(requestManager) {

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
		return Store.PHASE_MODEL;
	}

}
