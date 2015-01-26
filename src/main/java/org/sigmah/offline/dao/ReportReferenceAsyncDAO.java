package org.sigmah.offline.dao;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.ArrayList;
import org.sigmah.offline.indexeddb.Cursor;
import org.sigmah.offline.indexeddb.IDBKeyRange;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.OpenCursorRequest;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ReportReferenceJS;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.report.ReportReference;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ReportReferenceAsyncDAO extends AbstractAsyncDAO<ReportReference> {

	@Override
	public void saveOrUpdate(final ReportReference t, final AsyncCallback<ReportReference> callback, Transaction transaction) {
		saveOrUpdate(t, null, callback, transaction);
	}
	
	public void saveOrUpdate(final ReportReference t, String parentId, final AsyncCallback<ReportReference> callback, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		final ReportReferenceJS reportReferenceJS = ReportReferenceJS.toJavaScript(t);
		reportReferenceJS.setParentId(parentId);
		
		objectStore.put(reportReferenceJS).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                Log.error("Error while saving reminder " + reportReferenceJS.getId() + '.', caught);
				if(callback != null) {
					callback.onFailure(caught);
				}
            }

            @Override
            public void onSuccess(Request result) {
                Log.trace("Report reference " + reportReferenceJS.getId() + " has been successfully saved.");
				if(callback != null) {
					callback.onSuccess(t);
				}
            }
        });
	}
	
	public void saveAll(final ListResult<ReportReference> result, final String parentId) {
		openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				for(final ReportReference reportReference : result.getList()) {
					saveOrUpdate(reportReference, parentId, null, transaction);
				}
			}
		});
	}

	@Override
	public void get(int id, final AsyncCallback<ReportReference> callback, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());

		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(null);
            }

            @Override
            public void onSuccess(Request request) {
                final ReportReferenceJS reportReferenceJS = request.getResult();
				callback.onSuccess(reportReferenceJS != null ? reportReferenceJS.toDTO() : null);
            }
        });
	}
	
	public void getAll(final String parentId, final AsyncCallback<ListResult<ReportReference>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

            @Override
            public void onTransaction(Transaction transaction) {
                final ArrayList<ReportReference> reportReferences = new ArrayList<ReportReference>();
				
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final Index index = objectStore.index("parentId");
				
				final OpenCursorRequest cursorRequest = index.openCursor(IDBKeyRange.only(parentId));
                
                cursorRequest.addCallback(new AsyncCallback<Request>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    @Override
                    public void onSuccess(Request request) {
                        final Cursor cursor = cursorRequest.getResult();
						if(cursor != null) {
							final ReportReferenceJS reportReferenceJS = cursor.getValue();
							reportReferences.add(reportReferenceJS.toDTO());
							cursor.next();
							
						} else {
							callback.onSuccess(new ListResult<ReportReference>(reportReferences));
						}
                    }
                });
            }
        });
	}

	@Override
	public Store getRequiredStore() {
		return Store.REPORT_REFERENCE;
	}
	
}
