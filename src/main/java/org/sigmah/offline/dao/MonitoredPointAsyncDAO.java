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
