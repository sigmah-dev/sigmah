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
import org.sigmah.offline.js.ProjectModelJS;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Arrays;
import org.sigmah.shared.dto.element.ComputationElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Asynchronous DAO for saving and loading <code>ProjectModelDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ProjectModelAsyncDAO extends AbstractUserDatabaseAsyncDAO<ProjectModelDTO, ProjectModelJS> {
	
	@Inject
	private PhaseModelAsyncDAO phaseModelAsyncDAO;
	
	@Inject
	private ComputationAsyncDAO computationAsyncDAO;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(ProjectModelDTO t, AsyncCallback<ProjectModelDTO> callback, Transaction<Store> transaction) {
		super.saveOrUpdate(t, callback, transaction);
		
		for (final PhaseModelDTO phaseModel : t.getPhaseModels()) {
			phaseModelAsyncDAO.saveOrUpdate(phaseModel, null, transaction);
		}
		
		for (final FlexibleElementDTO flexibleElement : t.getAllElements()) {
			if (flexibleElement instanceof ComputationElementDTO) {
				final ComputationElementDTO computationElement = (ComputationElementDTO)flexibleElement;
				computationElement.setProjectModel(t);
				computationAsyncDAO.saveOrUpdate(computationElement, null, transaction);
			}
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int id, final AsyncCallback<ProjectModelDTO> callback, final Transaction<Store> transaction) {
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
				if (projectModelJS != null) {
					final ProjectModelDTO projectModelDTO = projectModelJS.toDTO();

					final RequestManager<ProjectModelDTO> requestManager = new RequestManager<ProjectModelDTO>(projectModelDTO, callback);

					final JsArrayInteger phaseModels = projectModelJS.getPhaseModels();
					if (phaseModels != null) {
						final ArrayList<PhaseModelDTO> dtos = new ArrayList<PhaseModelDTO>();
						projectModelDTO.setPhaseModels(dtos);

						for (int index = 0; index < phaseModels.length(); index++) {
							phaseModelAsyncDAO.get(phaseModels.get(index), new RequestManagerCallback<ProjectModelDTO, PhaseModelDTO>(requestManager) {
								@Override
								public void onRequestSuccess(PhaseModelDTO result) {
									if (result != null) {
										dtos.add(result);
									}
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
		return Store.PROJECT_MODEL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BaseAsyncDAO<Store>> getDependencies() {
		return Arrays.<BaseAsyncDAO<Store>>asList(phaseModelAsyncDAO, computationAsyncDAO);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModelJS toJavaScriptObject(ProjectModelDTO t) {
		return ProjectModelJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectModelDTO toJavaObject(ProjectModelJS js) {
		return js.toDTO();
	}
	
}
