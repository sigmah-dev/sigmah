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
 * Asynchronous DAO for saving and loading <code>ReportReference</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ReportReferenceAsyncDAO extends AbstractUserDatabaseAsyncDAO<ReportReference, ReportReferenceJS> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void saveOrUpdate(final ReportReference t, final AsyncCallback<ReportReference> callback, Transaction<Store> transaction) {
		saveOrUpdate(t, null, callback, transaction);
	}
	
	public void saveOrUpdate(final ReportReference t, String parentId, final AsyncCallback<ReportReference> callback, Transaction<Store> transaction) {
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
		if(result != null && result.getList() != null) {
			openTransaction(Transaction.Mode.READ_WRITE, new OpenTransactionHandler<Store>() {

				@Override
				public void onTransaction(Transaction<Store> transaction) {
					for(final ReportReference reportReference : result.getList()) {
						saveOrUpdate(reportReference, parentId, null, transaction);
					}
				}
			});
		}
	}

	public void getAll(final String parentId, final AsyncCallback<ListResult<ReportReference>> callback) {
        openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

            @Override
            public void onTransaction(Transaction<Store> transaction) {
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
							reportReferences.add(reportReferenceJS.toReportReference());
							cursor.next();
						} else {
							callback.onSuccess(new ListResult<ReportReference>(reportReferences));
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
		return Store.REPORT_REFERENCE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportReferenceJS toJavaScriptObject(ReportReference t) {
		return ReportReferenceJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ReportReference toJavaObject(ReportReferenceJS js) {
		return js.toReportReference();
	}
	
}
