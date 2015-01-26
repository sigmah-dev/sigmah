package org.sigmah.offline.dao;

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.indexeddb.CountRequest;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Order;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.js.HasId;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <T>
 */
public abstract class AbstractAsyncDAO<T> extends BaseAsyncDAO implements AsyncDAO<T> {

	@Override
	public void saveOrUpdate(T t) {
		saveOrUpdate(t, (AsyncCallback<T>) null);
	}
	
	public void saveOrUpdate(final T t, final AsyncCallback<T> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {
            
            @Override
            public void onTransaction(Transaction transaction) {
                saveOrUpdate(t, callback, transaction);
            }
        });
	}
    
    public void saveAll(final Collection<T> ts, final AsyncCallback<Void> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                saveAll(ts, callback, transaction);
            }
        });
    }
    
    /**
     * Saves the given objects in the given transaction.
     * Override this method to implements the saving of most complexe objects.
     * 
     * @param ts
     * @param callback
     * @param transaction 
     */
    public void saveAll(Collection<T> ts, AsyncCallback<Void> callback, Transaction transaction) {
        final RequestManager<Void> requestManager = new RequestManager<Void>(null, callback);
        
        for(final T t : ts) {
            saveOrUpdate(t, new RequestManagerCallback<Void, T>(requestManager) {

                @Override
                public void onRequestSuccess(T result) {
                }
                
            }, transaction);
        }
        
        requestManager.ready();
    }

	@Override
	public void get(final int id, final AsyncCallback<T> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                get(id, callback, transaction);
            }
        });
	}
    
    public void remove(final int id) {
        remove(id, null);
    }
    
    public void remove(final int id, final AsyncCallback<Void> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                remove(id, callback, transaction);
            }
        });
	}
    
    /**
     * Removes the object identified by <code>id</code>.
     * Override this method to implements the removal of most complexe objects.
     * 
     * @param id
     * @param callback
     * @param transaction 
     */
    public void remove(int id, final AsyncCallback<Void> callback, Transaction transaction) {
        final ObjectStore commandObjectStore = transaction.getObjectStore(getRequiredStore());
        commandObjectStore.delete(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                if(callback != null) {
                    callback.onFailure(caught);
                }
            }

            @Override
            public void onSuccess(Request result) {
                if(callback != null) {
                    callback.onSuccess(null);
                }
            }
        });
    }
    
    public void removeAll(final Collection<Integer> ids) {
        removeAll(ids, null);
    }
    
    public void removeAll(final Collection<Integer> ids, final AsyncCallback<Void> callback) {
        final RequestManager<Void> requestManager = new RequestManager<Void>(null, callback);
        
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                for(final Integer id : ids) {
                    // Remove the current object
                    remove(id, new RequestManagerCallback<Void, Void>(requestManager) {
                        
                        @Override
                        public void onRequestSuccess(Void result) {
                            // Success
                        }
                    }, transaction);
                }
                
                requestManager.ready();
            }
        });
    }
    
    public void count(final AsyncCallback<Integer> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final CountRequest countRequest = objectStore.count();
				
                countRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        callback.onSuccess(countRequest.getCount());
                    }
                });
            }
        });
	}
    
	public void generateNegativeId(final AsyncCallback<Integer> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				
				final OpenCursorRequest request = objectStore.openCursor(IDBKeyRange.upperBound(0, false), Order.ASCENDING);
				request.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = request.getResult();
						if(cursor != null) {
							final HasId hasId = cursor.getValue();
							callback.onSuccess(hasId.getId() - 1);
							
						} else {
							callback.onSuccess(-1);
						}
					}
				});
			}
		});
	}
	
	/**
	 * Removes every objets whose id is a negative integer.
	 * @param callback
	 */
	public void removeTemporaryObjects(final AsyncCallback<Void> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(final Transaction transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				
				final OpenCursorRequest request = objectStore.openCursor(IDBKeyRange.upperBound(0, false), Order.ASCENDING);
				request.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = request.getResult();
						if(cursor != null) {
							final HasId hasId = cursor.getValue();
							remove(hasId.getId(), callback, transaction);
							cursor.next();
							
						} else {
							callback.onSuccess(null);
						}
					}
				});
			}
		});
	}
}
