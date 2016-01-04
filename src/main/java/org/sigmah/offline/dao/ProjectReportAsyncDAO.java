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
 * Asynchronous DAO for saving and loading <code>ProjectReportDTO</code> objects.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectReportAsyncDAO extends AbstractUserDatabaseAsyncDAO<ProjectReportDTO, ProjectReportJS> {

	public void getByVersionId(final int id, final AsyncCallback<ProjectReportDTO> callback) {
		openTransaction(Transaction.Mode.READ_ONLY, new OpenTransactionHandler<Store>() {

			@Override
			public void onTransaction(Transaction<Store> transaction) {
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Store getRequiredStore() {
		return Store.PROJECT_REPORT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportJS toJavaScriptObject(ProjectReportDTO t) {
		return ProjectReportJS.toJavaScript(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProjectReportDTO toJavaObject(ProjectReportJS js) {
		return js.toDTO();
	}
	
}
