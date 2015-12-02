package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ReminderJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderListDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Singleton;
import java.util.List;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ReminderAsyncDAO extends AbstractAsyncDAO<ReminderDTO> {

	@Override
	public void saveOrUpdate(final ReminderDTO t, final AsyncCallback<ReminderDTO> callback, Transaction transaction) {
		final ObjectStore reminderStore = transaction.getObjectStore(getRequiredStore());
		
		final ReminderJS reminderJS = ReminderJS.toJavaScript(t);
		reminderStore.put(reminderJS).addCallback(new AsyncCallback<Request>() {

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
	
	public void saveOrUpdate(final ListResult<ReminderDTO> remindersResultList) {
		if(remindersResultList != null && remindersResultList.getList() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

                @Override
                public void onTransaction(Transaction transaction) {
                    for(final ReminderDTO reminderDTO : remindersResultList.getList()) {
						saveOrUpdate(reminderDTO, null, transaction);
					}
                }
            });
		}
	}
	
	public void saveOrUpdate(final ReminderListDTO reminderListDTO, Transaction transaction) {
		if(reminderListDTO != null && reminderListDTO.getReminders() != null) {
			for(final ReminderDTO reminderDTO : reminderListDTO.getReminders()) {
				saveOrUpdate(reminderDTO, null, transaction);
			}
		}
	}

	@Override
	public void get(int id, final AsyncCallback<ReminderDTO> callback, Transaction transaction) {
		final ObjectStore reminderStore = transaction.getObjectStore(getRequiredStore());

		reminderStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(null);
            }

            @Override
            public void onSuccess(Request request) {
                final ReminderJS reminderJS = request.getResult();
				callback.onSuccess(reminderJS != null ? reminderJS.toDTO() : null);
            }
        });
	}
	
	public void getAll(final AsyncCallback<ListResult<ReminderDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ArrayList<ReminderDTO> reminders = new ArrayList<ReminderDTO>();
				
				final ObjectStore reminderStore = transaction.getObjectStore(getRequiredStore());
				final OpenCursorRequest cursorRequest = reminderStore.openCursor();
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final ReminderJS reminderJS = (ReminderJS) cursor.getValue();
							if (!reminderJS.isDeleted()) {
							reminders.add(reminderJS.toDTO());
							}
							cursor.next();
							
						} else {
							callback.onSuccess(new ListResult<ReminderDTO>(reminders));
						}
                    }
                });
            }
        });
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<ReminderDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				getAllByParentListId(parentListId, callback, transaction);
			}
		});
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<ReminderDTO>> callback, Transaction transaction) {
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
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
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

	@Override
	public Store getRequiredStore() {
		return Store.REMINDER;
	}

}
