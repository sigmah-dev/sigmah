package org.sigmah.offline.dao;

import java.util.ArrayList;
import java.util.Collection;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.OrganizationJS;
import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrganizationAsyncDAO extends AbstractAsyncDAO<OrganizationDTO> {
	
	private final OrgUnitAsyncDAO orgUnitDAO;

	@Inject
	public OrganizationAsyncDAO(OrgUnitAsyncDAO orgUnitDAO) {
		this.orgUnitDAO = orgUnitDAO;
	}
	
	public static interface Factory {
		OrganizationAsyncDAO create(OrgUnitAsyncDAO orgUnitAsyncDAO);
	}
	
	@Override
	public void saveOrUpdate(final OrganizationDTO t, AsyncCallback<OrganizationDTO> callback, Transaction transaction) {
		// Saving organization
		final ObjectStore organizationStore = transaction.getObjectStore(Store.ORGANIZATION);

		final OrganizationJS organizationJS = OrganizationJS.toJavaScript(t);
		organizationStore.put(organizationJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving organization " + t.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Organization " + t.getId() + " has been successfully saved.");
            }
        });

		// Saving its root
		orgUnitDAO.saveOrUpdate(t.getRoot(), null, transaction);
	}
	
	@Override
	public void get(final int id, final AsyncCallback<OrganizationDTO> callback, final Transaction transaction) {
		if(transaction.useObjectFromCache(OrganizationDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore organizationStore = transaction.getObjectStore(Store.ORGANIZATION);

		organizationStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final OrganizationJS organizationJS = request.getResult();
				if(organizationJS != null) {
					final OrganizationDTO organizationDTO = organizationJS.toDTO();
					transaction.getObjectCache().put(id, organizationDTO);

					orgUnitDAO.get(organizationJS.getRoot(), new AsyncCallback<OrgUnitDTO>() {
						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(OrgUnitDTO result) {
							organizationDTO.setRoot(result);
							callback.onSuccess(organizationDTO);
						}
					}, transaction);
					
				} else {
					callback.onSuccess(null);
				}
            }
        });
	}
	
	@Override
	public Store getRequiredStore() {
		return Store.ORGANIZATION;
	}

	@Override
	public Collection<BaseAsyncDAO> getDependencies() {
		final ArrayList<BaseAsyncDAO> list = new ArrayList<BaseAsyncDAO>();
		list.add(orgUnitDAO);
		return list;
	}
}
