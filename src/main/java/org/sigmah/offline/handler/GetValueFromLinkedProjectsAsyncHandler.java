package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import java.util.Collections;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetValueHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class GetValueFromLinkedProjectsAsyncHandler implements AsyncCommandHandler<GetValueFromLinkedProjects, ListResult<String>> {

	@Inject
	private ProjectAsyncDAO projectAsyncDAO;
	
	@Inject
	private ValueAsyncDAO valueAsyncDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final GetValueFromLinkedProjects command, final OfflineExecutionContext executionContext, final AsyncCallback<ListResult<String>> callback) {
		// TODO: À implémenter.
		projectAsyncDAO.get(command.getProjectId(), new AsyncCallback<ProjectDTO>() {
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ProjectDTO result) {
				
			}
		});
		
		valueAsyncDAO.get(ValueJSIdentifierFactory.toIdentifier(command.getElementEntityName(), command.getProjectId(), command.getElementId(), null), new AsyncCallback<ValueResult>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(ValueResult result) {
				result.getValueObject();
			}
		});
		
		callback.onSuccess(new ListResult<String>(Collections.<String>emptyList()));
	}
	
}
