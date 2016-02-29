package org.sigmah.client.util.profiler;

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
import java.util.ArrayList;
import java.util.List;
import org.sigmah.offline.dao.AbstractAsyncDAO;
import org.sigmah.offline.dao.OpenTransactionHandler;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.NoopDatabaseRequest;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.sync.SuccessCallback;
/**
 * Save and load executions in an IndexedDB database.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ExecutionAsyncDAO extends AbstractAsyncDAO<Execution, ProfilerStore> {
	
	private final IndexedDB indexedDB;

	public ExecutionAsyncDAO() {
		if (IndexedDB.isSupported()) {
			this.indexedDB = new IndexedDB();
		} else {
			this.indexedDB = null;
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public OpenDatabaseRequest<ProfilerStore> openDatabase() {
		if (indexedDB != null) {
			return indexedDB.open(Profiler.DATABASE_NAME, ProfilerStore.class);
		} else {
			return new NoopDatabaseRequest<ProfilerStore>();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProfilerStore getRequiredStore() {
		return ProfilerStore.EXECUTION;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<ProfilerStore> getSchema() {
		return ProfilerStore.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(final Execution t, final AsyncCallback<Execution> callback, Transaction<ProfilerStore> transaction) {
		transaction.getObjectStore(getRequiredStore()).add(t).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Request result) {
				if (callback != null) {
					callback.onSuccess(t);
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void get(int id, final AsyncCallback<Execution> callback, Transaction<ProfilerStore> transaction) {
		transaction.getObjectStore(ProfilerStore.EXECUTION).get(id).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				if (callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Request result) {
				if (callback != null) {
					callback.onSuccess(result.<Execution>getResult());
				}
			}
		});
	}
	
	public void getExecutionsByScenario(final String indexName,final AsyncCallback<List<Execution>> callback){
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<ProfilerStore>() {
			@Override
			public void onTransaction(Transaction<ProfilerStore> transaction) {
				Log.info("##############################onTransaction :");
				final ObjectStore objectStore = transaction.getObjectStore(ProfilerStore.EXECUTION);
				final Index index = objectStore.index("scenario");
				
				final List<Execution> executions = new ArrayList<Execution>();
				
				index.openCursor(IDBKeyRange.only(indexName)).addCallback(new SuccessCallback<Request>() {		
					@Override
					public void onSuccess(Request result) {
						Log.info("##############################onSuccess :");
						final Cursor cursor = result.getResult();
						if(cursor != null) {
							final Execution execution = cursor.getValue();
							executions.add(execution);
							cursor.next();
						} else {
							callback.onSuccess(executions);
						}
					}
				});
			}
		});
	}
	
	public void getAllExecutions(final AsyncCallback<List<Execution>> callback){
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<ProfilerStore>() {
			@Override
			public void onTransaction(Transaction<ProfilerStore> transaction) {
				Log.info("##############################onTransaction :");
				final ObjectStore objectStore = transaction.getObjectStore(ProfilerStore.EXECUTION);
				final Index index = objectStore.index("scenario");
				
				final List<Execution> executions = new ArrayList<Execution>();
								
				index.openCursor(IDBKeyRange.lowerBound("",true)).addCallback(new SuccessCallback<Request>() {	
					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = result.getResult();
						if(cursor != null) {
							final Execution execution = cursor.getValue();
							executions.add(execution);
							cursor.next();
						} else {
							callback.onSuccess(executions);
						}
					}
				});
			}
		});
	}
	
	public void forEach(final AsyncCallback<Execution> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<ProfilerStore>() {

			@Override
			public void onTransaction(Transaction<ProfilerStore> transaction) {
				transaction.getObjectStore(ProfilerStore.EXECUTION).openCursor().addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = result.getResult();
						if(cursor != null) {
							callback.onSuccess(cursor.<Execution>getValue());
							cursor.next();
						} else {
							callback.onSuccess(null);
						}
					}
				});
			}
			
		});
	}
	/**
	 * clear Data base.
	 * @param profiler 
	 */
	public void removeDataBase(final ProfilerStore profiler){
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<ProfilerStore>() {
			@Override
			public void onTransaction(Transaction<ProfilerStore> transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(profiler);
				objectStore.clear();				
			}
		});
	}
	
}
