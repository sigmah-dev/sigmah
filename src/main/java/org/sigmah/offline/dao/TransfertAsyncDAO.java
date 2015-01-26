package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.shared.file.TransfertType;

/**
 * Keep information about upload and download progresses.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class TransfertAsyncDAO extends AbstractAsyncDAO<TransfertJS> {

	@Override
	public void saveOrUpdate(final TransfertJS t, final AsyncCallback<TransfertJS> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.put(t).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving transfert progress " + t.getId() + ".", caught);
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request request) {
                Log.trace("Transfert " + t.getId()  + " has been successfully saved.");
                
				// Updating the identifier with the generated key.
				t.setId(request.getResultAsInteger());
				
				if(callback != null) {
					callback.onSuccess(t);
				}
            }
        });
	}

	@Override
	public void get(final int id, final AsyncCallback<TransfertJS> callback, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final TransfertJS transfertJS = request.getResult();
				callback.onSuccess(transfertJS);
            }
        });
	}
	
	public void getAll(final TransfertType type, final AsyncCallback<TransfertJS> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final Index typeIndex = objectStore.index("type");
				final OpenCursorRequest openCursorRequest = typeIndex.openCursor(IDBKeyRange.only(type.name()));
				
                openCursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = openCursorRequest.getResult();
						if(cursor != null) {
							final TransfertJS transfertJS = cursor.getValue();
							if(transfertJS != null) {
								callback.onSuccess(transfertJS);
							}
							cursor.next();
						}
						// else DONE
                    }
                });
			}
		});
	}

	@Override
	public Store getRequiredStore() {
		return Store.TRANSFERT;
	}
	
}
