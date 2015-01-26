package org.sigmah.offline.dao;

import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.OrgUnitModelJS;
import org.sigmah.shared.dto.OrgUnitModelDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class OrgUnitModelAsyncDAO extends AbstractAsyncDAO<OrgUnitModelDTO> {

	@Override
	public void saveOrUpdate(final OrgUnitModelDTO t, AsyncCallback<OrgUnitModelDTO> callback, Transaction transaction) {
		// Saving org unit
		final ObjectStore orgUnitModelStore = transaction.getObjectStore(Store.ORG_UNIT_MODEL);

		final OrgUnitModelJS orgUnitModelJS = OrgUnitModelJS.toJavaScript(t);
		orgUnitModelStore.put(orgUnitModelJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving OrgUnitModel " + t.getId() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("OrgUnitModel " + t.getId() + " has been successfully saved.");
            }
        });
	}

	@Override
	public void get(final int id, final AsyncCallback<OrgUnitModelDTO> callback, final Transaction transaction) {
		if(transaction.useObjectFromCache(OrgUnitModelDTO.class, id, callback)) {
			return;
		}
		
		final ObjectStore orgUnitModelStore = transaction.getObjectStore(Store.ORG_UNIT_MODEL);
		orgUnitModelStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final OrgUnitModelJS orgUnitModelJS = request.getResult();
				final OrgUnitModelDTO orgUnitModelDTO = orgUnitModelJS != null ? orgUnitModelJS.toDTO() : null;
				
				transaction.getObjectCache().put(id, orgUnitModelDTO);
				callback.onSuccess(orgUnitModelDTO);
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.ORG_UNIT_MODEL;
	}

}
