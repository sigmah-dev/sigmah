package org.sigmah.client.util.profiler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.dao.AbstractAsyncDAO;
import org.sigmah.offline.dao.OpenTransactionHandler;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IndexedDB;
import org.sigmah.offline.indexeddb.NoopDatabaseRequest;
import org.sigmah.offline.indexeddb.OpenDatabaseRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Transaction;

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
	
}
