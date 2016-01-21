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
import org.sigmah.offline.js.ReminderJS;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderListDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;

/**
 * Asynchronous DAO for saving and loading <code>ReminderDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ReminderAsyncDAO extends AbstractUserDatabaseAsyncDAO<ReminderDTO, ReminderJS> {

	public void saveOrUpdate(final ReminderListDTO reminderListDTO, Transaction<Store> transaction) {
		if(reminderListDTO != null && reminderListDTO.getReminders() != null) {
			saveAll(reminderListDTO.getReminders(), null, transaction);
		}
	}

	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<ReminderDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				getAllByParentListId(parentListId, callback, transaction);
			}
		});
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<ReminderDTO>> callback, Transaction<Store> transaction) {
		final ArrayList<ReminderDTO> reminders = new ArrayList<ReminderDTO>();

		final ObjectStore reminderStore = transaction.getObjectStore(getRequiredStore());
		final Index parentListIdIndex = reminderStore.index("parentListId");
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
					final ReminderJS reminderJS = (ReminderJS) cursor.getValue();
					if (!reminderJS.isDeleted()) {
						reminders.add(reminderJS.toDTO());
					}
					cursor.next();

				} else {
					callback.onSuccess(reminders);
				}
			}
		});
	}
	
	public void getAllWithoutCompletionDate(final AsyncCallback<List<ReminderDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
				final ArrayList<ReminderDTO> reminders = new ArrayList<ReminderDTO>();

				final ObjectStore reminderStore = transaction.getObjectStore(getRequiredStore());
				final OpenCursorRequest cursorRequest = reminderStore.openCursor();

				cursorRequest.addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final ReminderJS reminderJS = (ReminderJS) cursor.getValue();
							if(reminderJS.getCompletionDate() == null && !reminderJS.isDeleted()) {
								reminders.add(reminderJS.toDTO());
							}
							cursor.next();

						} else {
							callback.onSuccess(reminders);
						}
					}
				});
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.REMINDER;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReminderJS toJavaScriptObject(ReminderDTO t) {
		return ReminderJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReminderDTO toJavaObject(ReminderJS js) {
		return js.toDTO();
	}

}
