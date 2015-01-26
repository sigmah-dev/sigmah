package org.sigmah.offline.handler;

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

	@Inject
	private ValueAsyncDAO valueAsyncDAO;
	
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
