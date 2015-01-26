package org.sigmah.offline.dao;

import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.offline.indexeddb.Index;
import org.sigmah.offline.indexeddb.ObjectStore;
import org.sigmah.offline.indexeddb.Request;
import org.sigmah.offline.indexeddb.Store;
import org.sigmah.offline.indexeddb.Transaction;
import org.sigmah.offline.js.ProjectReportJS;
import org.sigmah.shared.dto.report.ProjectReportDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectReportAsyncDAO extends AbstractAsyncDAO<ProjectReportDTO> {

	@Override
	public void saveOrUpdate(final ProjectReportDTO t, final AsyncCallback<ProjectReportDTO> callback, Transaction transaction) {
		final ProjectReportJS projectReportJS = ProjectReportJS.toJavaScript(t);
		
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		objectStore.put(projectReportJS).addCallback(new AsyncCallback<Request>() {

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

	@Override
	public void get(int id, final AsyncCallback<ProjectReportDTO> callback, Transaction transaction) {
		final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
		
		objectStore.get(id).addCallback(new AsyncCallback<Request>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Request result) {
				final ProjectReportJS projectReportJS = result.getResult();
				
				if(projectReportJS != null) {
					callback.onSuccess(projectReportJS.toDTO());
				} else {
					callback.onSuccess(null);
				}
			}
		});
	}
	
	public void getByVersionId(final int id, final AsyncCallback<ProjectReportDTO> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler() {

			@Override
			public void onTransaction(Transaction transaction) {
				final ObjectStore objectStore = transaction.getObjectStore(getRequiredStore());
				final Index index = objectStore.index("versionId");

				index.get(id).addCallback(new AsyncCallback<Request>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Request result) {
						final ProjectReportJS projectReportJS = result.getResult();

						if(projectReportJS != null) {
							callback.onSuccess(projectReportJS.toDTO());
						} else {
							callback.onSuccess(null);
						}
					}
				});
			}
		});
	}
	
	@Override
	public Store getRequiredStore() {
		return Store.PROJECT_REPORT;
	}
	
}
