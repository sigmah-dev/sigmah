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
import java.util.Collections;
import java.util.HashSet;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.OrgUnitJS;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.extjs.gxt.ui.client.data.ModelData;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.shared.command.result.ListResult;

import com.allen_sauer.gwt.log.client.Log;

/**
 * Asynchronous DAO for saving and loading <code>OrgUnitDTO</code> objects.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrgUnitAsyncDAO extends AbstractUserDatabaseAsyncDAO<OrgUnitDTO, OrgUnitJS> {

	private final OrgUnitModelAsyncDAO orgUnitModelAsyncDAO;
	private final CountryAsyncDAO countryAsyncDAO;

	@Inject
	public OrgUnitAsyncDAO(OrgUnitModelAsyncDAO orgUnitModelAsyncDAO, CountryAsyncDAO countryAsyncDAO) {
		this.orgUnitModelAsyncDAO = orgUnitModelAsyncDAO;
		this.countryAsyncDAO = countryAsyncDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(final OrgUnitDTO t, AsyncCallback<OrgUnitDTO> callback, Transaction<Store> transaction) {
		// Saving org unit
		super.saveOrUpdate(t, callback, transaction);

		// REM: parent is not saved to avoid stack overflows.

		// Saving the org unit model
		if (t.getOrgUnitModel() != null) {
			orgUnitModelAsyncDAO.saveOrUpdate(t.getOrgUnitModel(), null, transaction);
		}

		// Saving children org units
		if (t.getChildrenOrgUnits() != null) {
			for (final OrgUnitDTO child : t.getChildrenOrgUnits()) {
				saveOrUpdate(child, null, transaction);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<OrgUnitDTO> callback, final Transaction<Store> transaction) {
		if (transaction.useObjectFromCache(OrgUnitDTO.class, id, callback)) {
			return;
		}

		final ObjectStore orgUnitStore = transaction.getObjectStore(Store.ORG_UNIT);
		orgUnitStore.get(id).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Request request) {
				final OrgUnitJS orgUnitJS = request.getResult();
				if (orgUnitJS == null) {
					callback.onSuccess(null);
					return;
				}

				final OrgUnitDTO orgUnitDTO = orgUnitJS.toDTO();

				transaction.getObjectCache().put(id, orgUnitDTO);

				final RequestManager<OrgUnitDTO> requestManager = new RequestManager<OrgUnitDTO>(orgUnitDTO, callback);

				if (orgUnitJS.hasOrgUnitModel()) {
					orgUnitModelAsyncDAO.get(orgUnitJS.getOrgUnitModel(), new RequestManagerCallback<OrgUnitDTO, OrgUnitModelDTO>(requestManager) {

						@Override
						public void onRequestSuccess(OrgUnitModelDTO result) {
							orgUnitDTO.setOrgUnitModel(result);
						}
					}, transaction);
				}

				fillParent(orgUnitJS, orgUnitDTO, requestManager, transaction);
				fillChildren(orgUnitJS, orgUnitDTO, requestManager, transaction);
				fillCountry(orgUnitJS, orgUnitDTO, requestManager, transaction);

				requestManager.ready();
			}
		});
	}

	public void fillParent(final OrgUnitJS childJS, final OrgUnitDTO childDTO,
												 final RequestManager<OrgUnitDTO> requestManager, final Transaction<Store> transaction) {
		if (!childJS.hasParent()) {
			return;
		}

		final ObjectStore orgUnitObjectStore = transaction.getObjectStore(getRequiredStore());

		orgUnitObjectStore.get(childJS.getParent()).addCallback(new RequestManagerCallback<OrgUnitDTO, Request>(requestManager) {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while getting org unit " + childJS.getParent());
			}

			@Override
			public void onRequestSuccess(Request request) {
				if (request.getResult() == null) {
					return;
				}


				OrgUnitJS orgUnitJS = request.getResult();
				final OrgUnitDTO orgUnitDTO = orgUnitJS.toDTO();
				childDTO.setParentOrgUnit(orgUnitDTO);
				orgUnitDTO.setChildrenOrgUnits(Collections.singleton(childDTO));
				orgUnitDTO.setChildren(Collections.<ModelData>singletonList(childDTO));

				fillParent(orgUnitJS, orgUnitDTO, requestManager, transaction);
				fillCountry(orgUnitJS, orgUnitDTO, requestManager, transaction);
			}
		});
	}

	public void fillChildren(final OrgUnitJS parentJS, final OrgUnitDTO parentDTO,
													 final RequestManager<OrgUnitDTO> requestManager, final Transaction<Store> transaction) {
		final ObjectStore orgUnitObjectStore = transaction.getObjectStore(getRequiredStore());
		parentDTO.setChildrenOrgUnits(new HashSet<OrgUnitDTO>());
		parentDTO.setChildren(new ArrayList<ModelData>());

		for (int i = 0; i < parentJS.getChildren().length(); i++) {
			final int childId = i;
			orgUnitObjectStore.get(parentJS.getChildren().get(i)).addCallback(new RequestManagerCallback<OrgUnitDTO, Request>(requestManager) {

				@Override
				public void onFailure(Throwable caught) {
					Log.error("Error while getting org unit " + parentJS.getChildren().get(childId));
				}

				@Override
				public void onRequestSuccess(Request request) {
					if (request.getResult() == null) {
						return;
					}

					OrgUnitJS orgUnitJS = request.getResult();
					final OrgUnitDTO orgUnitDTO = orgUnitJS.toDTO();
					orgUnitDTO.setParentOrgUnit(parentDTO);
					orgUnitDTO.getChildrenOrgUnits().add(orgUnitDTO);
					orgUnitDTO.getChildren().add(orgUnitDTO);

					fillChildren(orgUnitJS, orgUnitDTO, requestManager, transaction);
					fillCountry(orgUnitJS, orgUnitDTO, requestManager, transaction);
				}
			});
		}
	}

	public void fillCountry(final OrgUnitJS orgUnitJS, final OrgUnitDTO orgUnitDTO,
													final RequestManager<OrgUnitDTO> requestManager, Transaction<Store> transaction) {
		countryAsyncDAO.get(orgUnitJS.getOfficeLocationCountry(), new RequestManagerCallback<OrgUnitDTO, CountryDTO>(requestManager) {
			@Override
			public void onRequestSuccess(CountryDTO countryDTO) {
				orgUnitDTO.setOfficeLocationCountry(countryDTO);
			}

			@Override
			public void onFailure(Throwable throwable) {
				Log.error("Error while getting country " + orgUnitJS.getOfficeLocationCountry());
			}
		}, transaction);
	}

	public void getWithoutDependencies(final int id, final AsyncCallback<OrgUnitDTO> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore orgUnitObjectStore = transaction.getObjectStore(getRequiredStore());

				orgUnitObjectStore.get(id).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request request) {
						callback.onSuccess(request.getResult() != null ? request.<OrgUnitJS>getResult().toDTO() : null);
					}
				});
			}
		});
	}

	public void getAll(final AsyncCallback<ListResult<OrgUnitDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ArrayList<OrgUnitDTO> units = new ArrayList<OrgUnitDTO>();

				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final OpenCursorRequest cursorRequest = objectStore.openCursor();

				cursorRequest.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final OrgUnitJS orgUnitJS = (OrgUnitJS) cursor.getValue();
							units.add(orgUnitJS.toDTO());
							cursor.next();

						} else {
							callback.onSuccess(new ListResult<OrgUnitDTO>(units));
						}
					}
				});
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.ORG_UNIT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Collection<BaseAsyncDAO<Store>> getDependencies() {
		final ArrayList<BaseAsyncDAO<Store>> list = new ArrayList<BaseAsyncDAO<Store>>();
		list.add(orgUnitModelAsyncDAO);
		list.add(countryAsyncDAO);
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitJS toJavaScriptObject(OrgUnitDTO t) {
		return OrgUnitJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitDTO toJavaObject(OrgUnitJS js) {
		return js.toDTO();
	}
}
