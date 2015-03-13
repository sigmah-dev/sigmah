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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrgUnitAsyncDAO extends AbstractAsyncDAO<OrgUnitDTO> {

	private final OrgUnitModelAsyncDAO orgUnitModelAsyncDAO;
	private final CountryAsyncDAO countryAsyncDAO;

	@Inject
	public OrgUnitAsyncDAO(OrgUnitModelAsyncDAO orgUnitModelAsyncDAO, CountryAsyncDAO countryAsyncDAO) {
		this.orgUnitModelAsyncDAO = orgUnitModelAsyncDAO;
		this.countryAsyncDAO = countryAsyncDAO;
	}

	@Override
	public void saveOrUpdate(final OrgUnitDTO t, AsyncCallback<OrgUnitDTO> callback, Transaction transaction) {
		// Saving org unit
		final ObjectStore orgUnitStore = transaction.getObjectStore(Store.ORG_UNIT);

		final OrgUnitJS orgUnitJS = OrgUnitJS.toJavaScript(t);
		orgUnitStore.put(orgUnitJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving OrgUnit " + t.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("OrgUnit " + t.getId() + " has been successfully saved.");
            }
        });

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

	@Override
	public void get(final int id, final AsyncCallback<OrgUnitDTO> callback, final Transaction transaction) {
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
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
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

	@Override
	public Store getRequiredStore() {
		return Store.ORG_UNIT;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(orgUnitModelAsyncDAO);
		list.add(countryAsyncDAO);
		return list;
	}
}
