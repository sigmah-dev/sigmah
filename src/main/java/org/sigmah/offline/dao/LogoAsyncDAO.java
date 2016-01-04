package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;

/**
 * Asynchronous DAO for saving and loading the logo of the organization.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LogoAsyncDAO extends BaseAsyncDAO<Store> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OpenDatabaseRequest<Store> openDatabase() {
		return IndexedDB.openUserDatabase(getAuthentication());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<Store> getSchema() {
		return Store.class;
	}

	public void saveOrUpdate(final int organizationId, final String logo) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore store = transaction.getObjectStore(getRequiredStore());
				
				store.put(boxLogo(organizationId, logo)).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						Log.error("Error while saving logo for organization " + organizationId + ".", caught);
					}

					@Override
					public void onSuccess(Request result) {
						// Success.
					}
				});
			}
		});
	}
	
	public void get(final int organizationId, final AsyncCallback<String> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore store = transaction.getObjectStore(getRequiredStore());
				
				store.get(organizationId).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request request) {
						final String result;
						
						final JavaScriptObject box = request.getResult();
						
						if(box != null) {
							result = unboxLogo(box);
						} else {
							result = null;
						}
						
						callback.onSuccess(result);
					}
				});
			}
		});
	}
	
	private native JavaScriptObject boxLogo(int organizationId, String logo) /*-{
		return {
			"id": organizationId,
			"logo": logo
		};
	}-*/;
	
	private native String unboxLogo(JavaScriptObject box) /*-{
		return box.logo;
	}-*/;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.LOGO;
	}
	
}
