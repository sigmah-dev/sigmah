package org.sigmah.offline.dao;

import org.sigmah.client.page.Page;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.PageAccessJS;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PageAccessAsyncDAO extends BaseAsyncDAO {
	
	public void saveOrUpdate(final PageAccessJS pageAccessJS) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				saveOrUpdate(pageAccessJS, transaction);
			}
		});
	}
	
	public void saveOrUpdate(final PageAccessJS pageAccessJS, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(Store.PAGE_ACCESS);
		
		objectStore.put(pageAccessJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving access right for page " + pageAccessJS.getPage() + ".", caught);
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Access right for page  " + pageAccessJS.getPage() + " has been successfully saved.");
            }
        });
	}

	public void get(final Page page, final AsyncCallback<PageAccessJS> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				get(page, callback, transaction);
			}
		});
	}
	
	public void get(final Page page, final AsyncCallback<PageAccessJS> callback, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(Store.PAGE_ACCESS);
		
		objectStore.get(page.name()).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final PageAccessJS pageAccessJS = request.getResult();
				
				if(pageAccessJS != null) {
					callback.onSuccess(pageAccessJS);
				} else {
					Log.warn("No access right was saved locally for page " + page);
					callback.onSuccess(PageAccessJS.createPageAccessJS(page, false));
				}
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.PAGE_ACCESS;
	}
}
