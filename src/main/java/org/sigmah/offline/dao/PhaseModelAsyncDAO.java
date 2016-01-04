package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PhaseModelJS;
import org.sigmah.shared.dto.PhaseModelDTO;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>PhaseModelDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PhaseModelAsyncDAO extends AbstractUserDatabaseAsyncDAO<PhaseModelDTO, PhaseModelJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<PhaseModelDTO> callback, final Transaction<Store> transaction) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.PHASE_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhaseModelJS toJavaScriptObject(PhaseModelDTO t) {
		return PhaseModelJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhaseModelDTO toJavaObject(PhaseModelJS js) {
		return js.toDTO();
	}

}
