package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.MonitoredPointJS;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointListDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;

/**
 * Asynchronous DAO for saving and loading <code>MonitoredPointDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class MonitoredPointAsyncDAO extends AbstractUserDatabaseAsyncDAO<MonitoredPointDTO, MonitoredPointJS> {

	public void saveOrUpdate(final MonitoredPointListDTO monitoredPointListDTO, Transaction<Store> transaction) {
		if (monitoredPointListDTO != null && monitoredPointListDTO.getPoints() != null) {
			saveAll(monitoredPointListDTO.getPoints(), null, transaction);
		}
	}

	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<MonitoredPointDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				getAllByParentListId(parentListId, callback, transaction);
			}
		});
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<MonitoredPointDTO>> callback, Transaction<Store> transaction) {
		final ArrayList<MonitoredPointDTO> monitoredPoints = new ArrayList<MonitoredPointDTO>();

		final ObjectStore monitoredPointStore = transaction.getObjectStore(getRequiredStore());
		final Index parentListIdIndex = monitoredPointStore.index("parentListId");
		final OpenCursorRequest cursorRequest = parentListIdIndex.openCursor(IDBKeyRange.only(parentListId));

		cursorRequest.addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Request result) {
				final Cursor cursor = cursorRequest.getResult();
				if(cursor != null) {
					final MonitoredPointJS monitoredPointJS = (MonitoredPointJS) cursor.getValue();
					if (!monitoredPointJS.isDeleted()) {
						monitoredPoints.add(monitoredPointJS.toDTO());
					}
					cursor.next();

				} else {
					callback.onSuccess(monitoredPoints);
				}
			}
		});
	}
	
	public void getAllWithoutCompletionDate(final AsyncCallback<List<MonitoredPointDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ArrayList<MonitoredPointDTO> monitoredPoints = new ArrayList<MonitoredPointDTO>();

				final ObjectStore monitoredPointStore = transaction.getObjectStore(getRequiredStore());
				final OpenCursorRequest cursorRequest = monitoredPointStore.openCursor();

				cursorRequest.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final MonitoredPointJS monitoredPointJS = (MonitoredPointJS) cursor.getValue();
							if(monitoredPointJS.getCompletionDate() == null && !monitoredPointJS.isDeleted()) {
								monitoredPoints.add(monitoredPointJS.toDTO());
							}
							cursor.next();

						} else {
							callback.onSuccess(monitoredPoints);
						}
					}
				});
			}
		});
	}

	@Override
	public Store getRequiredStore() {
		return Store.MONITORED_POINT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitoredPointJS toJavaScriptObject(MonitoredPointDTO t) {
		return MonitoredPointJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonitoredPointDTO toJavaObject(MonitoredPointJS js) {
		return js.toDTO();
	}

}
