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
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.offline.indexeddb.Schema;

/**
 * Implements basic CRUD operations for objects using an int as key.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <T> Type of the objects handled by this DAO.
 * @param <S> Schema where to save objects.
 */
public abstract class AbstractAsyncDAO<T, S extends Enum<S> & Schema> extends BaseAsyncDAO<S> implements AsyncDAO<T, S> {

	/**
	 * Open a transaction and save the given object.
	 * 
	 * @param t Object to save.
	 */
	@Override
	public void saveOrUpdate(T t) {
		saveOrUpdate(t, (AsyncCallback<T>) null);
	}
	
	/**
	 * Open a transaction and save the given object, the given callback will be
	 * called when done (if not null).
	 * 
	 * @param t Object to save.
	 * @param callback Callback to call (may be null).
	 */
	public void saveOrUpdate(final T t, final AsyncCallback<T> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<S>() {
            
            @Override
            public void onTransaction(Transaction<S> transaction) {
                saveOrUpdate(t, callback, transaction);
            }
        });
	}
	
	/**
	 * Open a transaction and save every objects in the given collection, the
	 * given callback will be called when done (if not null).
	 * 
	 * @param ts Objects to save.
	 * @param callback Callback to call (may be null).
	 */
    public void saveAll(final Collection<T> ts, final AsyncCallback<Void> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<S>() {

            @Override
            public void onTransaction(Transaction<S> transaction) {
                saveAll(ts, callback, transaction);
            }
        });
    }
    
    /**
     * Saves the given objects in the given transaction.
     * Override this method to save of most complexe objects.
     * 
     * @param ts Objects to save.
     * @param callback Callback to call (may be null).
     * @param transaction Transaction to use.
     */
    public void saveAll(Collection<T> ts, AsyncCallback<Void> callback, Transaction<S> transaction) {
        final RequestManager<Void> requestManager = new RequestManager<Void>(null, callback);
        
        for(final T t : ts) {
            saveOrUpdate(t, new RequestManagerCallback<Void, T>(requestManager) {

                @Override
                public void onRequestSuccess(T result) {
					// Success.
                }
                
            }, transaction);
        }
        
        requestManager.ready();
    }

	/**
	 * Open a transaction and retrieve the object identified by the given id.
	 * 
	 * @param id Identifier.
	 * @param callback Callback to call when the object is loaded.
	 */
	@Override
	public void get(final int id, final AsyncCallback<T> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<S>() {

            @Override
            public void onTransaction(Transaction<S> transaction) {
                get(id, callback, transaction);
            }
        });
	}
    
	/**
     * Open a new transaction and removes the object identified by <code>id</code>.
     * 
     * @param id Identifier of the object to remove.
     */
    public void remove(final int id) {
        remove(id, null);
    }
    
	/**
     * Open a new transaction and removes the object identified by <code>id</code>.
     * 
     * @param id Identifier of the object to remove.
     * @param callback Called when the removal is done.
     */
    public void remove(final int id, final AsyncCallback<VoidResult> callback) {
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<S>() {

            @Override
            public void onTransaction(Transaction<S> transaction) {
                remove(id, callback, transaction);
            }
        });
	}
    
    /**
     * Removes the object identified by <code>id</code>.
     * Override this method to implements the removal of most complexe objects.
     * 
     * @param id Identifier of the object to remove.
     * @param callback Called when the removal is done.
     * @param transaction Transaction to use.
     */
    public void remove(int id, final AsyncCallback<VoidResult> callback, Transaction<S> transaction) {
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
    
	/**
	 * Remove every entry matching the given identifiers.
	 * 
	 * @param ids Collection of identifier to remove.
	 */
    public void removeAll(final Collection<Integer> ids) {
        removeAll(ids, null);
    }
    
	/**
	 * Remove every entry matching the given identifiers.
	 * 
	 * @param ids Collection of identifier to remove.
	 * @param callback Called when the removal is done.
	 */
    public void removeAll(final Collection<Integer> ids, final AsyncCallback<VoidResult> callback) {
        final RequestManager<VoidResult> requestManager = new RequestManager<VoidResult>(null, callback);
        
        openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<S>() {

            @Override
            public void onTransaction(Transaction<S> transaction) {
                for(final Integer id : ids) {
                    // Remove the current object
                    remove(id, new RequestManagerCallback<VoidResult, VoidResult>(requestManager) {
                        
                        @Override
                        public void onRequestSuccess(VoidResult result) {
                            // Success
                        }
                    }, transaction);
                }
                
                requestManager.ready();
            }
        });
    }
    
    public void count(final AsyncCallback<Integer> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<S>() {

            @Override
            public void onTransaction(Transaction<S> transaction) {
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
    
	/**
	 * Open a new transaction and generates a negative identifier.
	 * 
	 * @param callback Called when the identifier is generated.
	 */
	public void generateNegativeId(final AsyncCallback<Integer> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<S>() {

			@Override
			public void onTransaction(Transaction<S> transaction) {
				generateNegativeId(callback, transaction);
			}
		});
	}
    
	/**
	 * Generates a negative identifier within the given transaction.
	 * 
	 * @param callback Called when the identifier is generated.
	 * @param transaction Transaction to use.
	 */
	protected void generateNegativeId(final AsyncCallback<Integer> callback, Transaction<S> transaction) {
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
	
	/**
	 * Removes every objets whose id is a negative integer.
	 * 
	 * @param callback Called when the removal is done.
	 */
	public void removeTemporaryObjects(final AsyncCallback<VoidResult> callback) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<S>() {

			@Override
			public void onTransaction(final Transaction<S> transaction) {
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
