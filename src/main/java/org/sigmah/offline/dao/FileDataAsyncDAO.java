package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.FileDataJS;

/**
 * Save and load file data into IndexedDB.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class FileDataAsyncDAO extends AbstractAsyncDAO<FileDataJS> {

	@Override
	public void saveOrUpdate(final FileDataJS t, final AsyncCallback<FileDataJS> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.put(t).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving data of file " + t.getId() + ".");
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request request) {
                Log.trace("Data of file " + t.getId()  + " has been successfully saved.");
				
				// Updating the identifier with the generated key.
				t.setId(request.getResultAsInteger());
				
				if(callback != null) {
					callback.onSuccess(t);
				}
            }
        });
	}

	@Override
	public void get(final int id, final AsyncCallback<FileDataJS> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                callback.onSuccess((FileDataJS) request.getResult());
            }
        });
	}

	public void getByFileVersionId(final int fileVersionId, final AsyncCallback<FileDataJS> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                getByFileVersionId(fileVersionId, callback, transaction);
            }
        });
	}
	
	public void getByFileVersionId(final int fileVersionId, final AsyncCallback<FileDataJS> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		final Index fileVersionIdIndex = objectStore.index("fileVersionId");
		fileVersionIdIndex.get(fileVersionId).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                callback.onSuccess((FileDataJS) request.getResult());
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.FILE_DATA;
	}
	
}
