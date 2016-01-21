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
import java.util.Collection;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PhaseJS;
import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.PhaseModelDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Asynchronous DAO for saving and loading <code>PhaseDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PhaseAsyncDAO extends AbstractUserDatabaseAsyncDAO<PhaseDTO, PhaseJS> {
	
	private final PhaseModelAsyncDAO phaseModelAsyncDAO;

	/**
	 * {@inheritDoc}
	 */
	@Inject
	public PhaseAsyncDAO(PhaseModelAsyncDAO phaseModelAsyncDAO) {
		this.phaseModelAsyncDAO = phaseModelAsyncDAO;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(PhaseDTO t, AsyncCallback<PhaseDTO> callback, Transaction<Store> transaction) {
		super.saveOrUpdate(t, callback, transaction);
		
		// Saving the phase model
		phaseModelAsyncDAO.saveOrUpdate(t.getPhaseModel(), null, transaction);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int id, final AsyncCallback<PhaseDTO> callback, final Transaction<Store> transaction) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.PHASE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BaseAsyncDAO<Store>> getDependencies() {
		final ArrayList<BaseAsyncDAO<Store>> list = new ArrayList<BaseAsyncDAO<Store>>();
		list.add(phaseModelAsyncDAO);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhaseJS toJavaScriptObject(PhaseDTO t) {
		return PhaseJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PhaseDTO toJavaObject(PhaseJS js) {
		return js.toDTO();
	}
	
}
