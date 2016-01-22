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
import java.util.Map;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.FileDataJS;

/**
 * Save and load file data into IndexedDB.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class FileDataAsyncDAO extends AbstractAsyncDAO<FileDataJS, Store> {

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
	public void saveOrUpdate(final FileDataJS t, final AsyncCallback<FileDataJS> callback, final Transaction<Store> transaction) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<FileDataJS> callback, final Transaction<Store> transaction) {
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
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(Transaction<Store> transaction) {
                getByFileVersionId(fileVersionId, callback, transaction);
            }
        });
	}
	
	public void getByFileVersionId(final int fileVersionId, final AsyncCallback<FileDataJS> callback, final Transaction<Store> transaction) {
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
	
	public void replaceId(final Map.Entry<Integer, Integer> entry, final Transaction<Store> transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.index("fileVersionId").get(entry.getKey().intValue()).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while changing the id of '" + entry.getKey() + "' to '" + entry.getValue() + "'.", caught);
			}

			@Override
			public void onSuccess(Request request) {
				final FileDataJS fileDataJS = request.getResult();
				
				if(fileDataJS != null) {
					// Updating the entry.
					fileDataJS.getFileVersion().setId(entry.getValue());
					saveOrUpdate(fileDataJS, null, transaction);
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
		return Store.FILE_DATA;
	}
	
}
