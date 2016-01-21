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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.io.Serializable;
import java.util.ArrayList;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.Values;
import org.sigmah.shared.command.result.ListResult;

/**
 * Implements common operations for AsyncDAO.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public abstract class AbstractUserDatabaseAsyncDAO<T extends Serializable, J extends JavaScriptObject> extends AbstractAsyncDAO<T, Store> {

	/**
	 * Transforms the given object to a JavaScript object.
	 * 
	 * @param t Object to transform.
	 * @return JavaScript version of the given object.
	 */
	public abstract J toJavaScriptObject(T t);
	
	/**
	 * Transforms the given object to a Java object.
	 * 
	 * @param js JavaScript object to transform.
	 * @return Java version of the given object.
	 */
	public abstract T toJavaObject(J js);
	
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
	public void saveOrUpdate(final T t, final AsyncCallback<T> callback, final Transaction<Store> transaction) {
		final J js = toJavaScriptObject(t);
		
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		objectStore.put(js).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Request result) {
				if(callback != null) {
					callback.onSuccess(t);
				}
			}
		});
	}
	
	public void saveAll(final ListResult<T> listResult, final AsyncCallback<Void> callback) {
		if (listResult != null && listResult.getList() != null) {
			saveAll(listResult.getList(), callback);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(final int id, final AsyncCallback<T> callback, final Transaction<Store> transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());

		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
				final J js = request.getResult();
				final T t = js != null ? toJavaObject(js) : null;
				
				// Caching the retrieved object.
				transaction.getObjectCache().put(id, t);
				
				callback.onSuccess(t);
            }
        });
	}
	
	public void getListResult(final AsyncCallback<ListResult<T>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ArrayList<T> ts = new ArrayList<T>();
				
				final ObjectStore countryObjectStore = transaction.getObjectStore(getRequiredStore());
				countryObjectStore.openCursor().addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = result.getResult();
						if (cursor != null) {
							final J js = cursor.getValue();
							if (!Values.isDeleted(js)) {
								ts.add(toJavaObject(js));
							}
							cursor.next();
						} else {
							callback.onSuccess(new ListResult<T>(ts));
						}
					}
				});
			}
		});
	}
	
}
