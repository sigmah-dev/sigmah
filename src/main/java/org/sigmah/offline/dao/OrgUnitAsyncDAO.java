package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.OrgUnitJS;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.shared.command.result.ListResult;

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
				if(orgUnitJS != null) {
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

					if (orgUnitJS.getChildren() != null) {
						orgUnitDTO.setChildrenOrgUnits(new HashSet<OrgUnitDTO>());

						final JsArrayInteger children = orgUnitJS.getChildren();
						final int length = children.length();

						for (int index = 0; index < length; index++) {
							final int child = children.get(index);
							get(child, new RequestManagerCallback<OrgUnitDTO, OrgUnitDTO>(requestManager) {

								@Override
								public void onRequestSuccess(OrgUnitDTO result) {
									result.setParent(orgUnitDTO);
									orgUnitDTO.getChildrenOrgUnits().add(result);
									orgUnitDTO.getChildren().add(result);
								}
							}, transaction);
						}
					}

					if (orgUnitJS.hasCountry()) {
						countryAsyncDAO.get(orgUnitJS.getOfficeLocationCountry(), new RequestManagerCallback<OrgUnitDTO, CountryDTO>(requestManager) {

							@Override
							public void onRequestSuccess(CountryDTO result) {
								orgUnitDTO.setOfficeLocationCountry(result);
							}
						}, transaction);
					}

					requestManager.ready();
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
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
