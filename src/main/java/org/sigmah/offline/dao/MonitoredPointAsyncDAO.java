package org.sigmah.offline.dao;

import java.util.ArrayList;

import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.MonitoredPointJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointListDTO;

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
public class MonitoredPointAsyncDAO extends AbstractAsyncDAO<MonitoredPointDTO> {

	@Override
	public void saveOrUpdate(final MonitoredPointDTO t, final AsyncCallback<MonitoredPointDTO> callback, Transaction transaction) {
		final ObjectStore monitoredPointStore = transaction.getObjectStore(getRequiredStore());
		
		final MonitoredPointJS monitoredPointJS = MonitoredPointJS.toJavaScript(t);
		monitoredPointStore.put(monitoredPointJS).addCallback(new AsyncCallback<Request>() {

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
	
	public void saveOrUpdate(final ListResult<MonitoredPointDTO> monitoredPointsResultList) {
		if(monitoredPointsResultList != null && monitoredPointsResultList.getList() != null) {
            openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

                @Override
                public void onTransaction(Transaction transaction) {
                    for(final MonitoredPointDTO monitoredPointDTO : monitoredPointsResultList.getList()) {
						saveOrUpdate(monitoredPointDTO, null, transaction);
					}
                }
            });
		}
	}
	
	public void saveOrUpdate(final MonitoredPointListDTO monitoredPointListDTO, Transaction transaction) {
		if(monitoredPointListDTO != null && monitoredPointListDTO.getPoints() != null) {
			for(final MonitoredPointDTO monitoredPointDTO : monitoredPointListDTO.getPoints()) {
				saveOrUpdate(monitoredPointDTO, null, transaction);
			}
		}
	}

	@Override
	public void get(int id, final AsyncCallback<MonitoredPointDTO> callback, Transaction transaction) {
		final ObjectStore monitoredPointStore = transaction.getObjectStore(getRequiredStore());

		monitoredPointStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Request request) {
                final MonitoredPointJS monitoredPointJS = request.getResult();
				final MonitoredPointDTO monitoredPointDTO = monitoredPointJS != null ? monitoredPointJS.toDTO() : null;
				
				callback.onSuccess(monitoredPointDTO);
            }
        });
	}
	
	public void getAll(final AsyncCallback<ListResult<MonitoredPointDTO>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
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
							monitoredPoints.add(monitoredPointJS.toDTO());
							cursor.next();
							
						} else {
							callback.onSuccess(new ListResult<MonitoredPointDTO>(monitoredPoints));
						}
                    }
                });
            }
        });
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<MonitoredPointDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				getAllByParentListId(parentListId, callback, transaction);
			}
		});
	}
	
	public void getAllByParentListId(final int parentListId, final AsyncCallback<List<MonitoredPointDTO>> callback, Transaction transaction) {
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
					monitoredPoints.add(monitoredPointJS.toDTO());
					cursor.next();

				} else {
					callback.onSuccess(monitoredPoints);
				}
			}
		});
	}
	
	public void getAllWithoutCompletionDate(final AsyncCallback<List<MonitoredPointDTO>> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
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
							if(monitoredPointJS.getCompletionDate() == null) {
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

}
