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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.shared.file.TransfertType;

/**
 * Keep information about upload and download progresses.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */

public class TransfertAsyncDAO extends AbstractAsyncDAO<TransfertJS, Store> {

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(final TransfertJS t, final AsyncCallback<TransfertJS> callback, final Transaction<Store> transaction) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<TransfertJS> callback, final Transaction<Store> transaction) {
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
	
	public void getByFileVersionId(final int fileVersionId, final AsyncCallback<TransfertJS> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				
				objectStore.index("fileVersionId").get(fileVersionId).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request request) {
						callback.onSuccess(request.<TransfertJS>getResult());
					}
				});
			}
		});
	}
	
	public void getAll(final TransfertType type, final AsyncCallback<List<TransfertJS>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final Index typeIndex = objectStore.index("type");
				final OpenCursorRequest openCursorRequest = typeIndex.openCursor(IDBKeyRange.only(type.name()));
				
				final ArrayList<TransfertJS> transfers = new ArrayList<TransfertJS>();
				
                openCursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = openCursorRequest.getResult();
						if(cursor != null) {
							final TransfertJS transfert = cursor.getValue();
							if(transfert != null) {
								transfers.add(transfert);
							}
							cursor.next();
							
						} else {
							// Done
							callback.onSuccess(transfers);
						}
                    }
                });
			}
		});
	}
	
	public void replaceId(final Map.Entry<Integer, Integer> entry, final Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.index("fileVersionId").get(entry.getKey().intValue()).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while changing the id of '" + entry.getKey() + "' to '" + entry.getValue() + "'.", caught);
			}

			@Override
			public void onSuccess(Request request) {
				final TransfertJS transfertJS = request.getResult();
				
				if(transfertJS != null) {
					// Updating the entry.
					transfertJS.getFileVersion().setId(entry.getValue());
					saveOrUpdate(transfertJS, null, transaction);
				}
			}
		});
	}
	
	public void replaceIds(final Map<Integer, Integer> ids) {
		if(ids == null || ids.isEmpty()) {
			return;
		}
		
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				for(Map.Entry<Integer, Integer> entry : ids.entrySet()) {
					replaceId(entry, transaction);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.TRANSFERT;
	}
	
}
