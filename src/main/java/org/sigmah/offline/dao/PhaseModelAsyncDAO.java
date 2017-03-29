package org.sigmah.offline.dao;

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
