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
