package org.sigmah.offline.handler;

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
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProjectDocuments;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.dto.value.ListableValue;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectDocumentsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetProjectDocumentsAsyncHandler implements AsyncCommandHandler<GetProjectDocuments, ListResult<ReportReference>> {

	
	private ValueAsyncDAO valueAsyncDAO;
	
	public GetProjectDocumentsAsyncHandler(ValueAsyncDAO valueAsyncDAO) {
		this.valueAsyncDAO = valueAsyncDAO;
	}



	@Override
	public void execute(GetProjectDocuments command, OfflineExecutionContext executionContext, AsyncCallback<ListResult<ReportReference>> callback) {
		final ArrayList<ReportReference> references = new ArrayList<ReportReference>();
		final RequestManager<ListResult<ReportReference>> requestManager = new RequestManager<ListResult<ReportReference>>(
			new ListResult<ReportReference>(references), callback);
		
		for(final GetProjectDocuments.FilesListElement element : command.getElements()) {
			final GetValue getValue = new GetValue(command.getProjectId(), element.getId(), FilesListElementDTO.ENTITY_NAME);
			valueAsyncDAO.get(getValue, new RequestManagerCallback<ListResult<ReportReference>, ValueResult>(requestManager) {

				@Override
				public void onRequestSuccess(ValueResult result) {
					if(result != null && result.getValuesObject() != null) {
						for(final ListableValue value : result.getValuesObject()) {
							if(value instanceof FileDTO) {
								final FileDTO file = (FileDTO)value;
								
								final FileVersionDTO lastVersion = file.getLastVersion();

								final ReportReference r = new ReportReference(lastVersion);
								r.setId(lastVersion.getId());
								r.setName(lastVersion.getName() + '.' + lastVersion.getExtension());
								r.setLastEditDate(lastVersion.getAddedDate());
								r.setEditorName(lastVersion.getAuthorName());
								r.setPhaseName(element.getPhaseName());
								r.setFlexibleElementLabel(element.getElementLabel());

								references.add(r);
							}
						}
					}
				}
			});
		}
		
		requestManager.ready();
	}
	
}
