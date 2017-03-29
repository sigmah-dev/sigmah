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
import java.util.ArrayList;
import java.util.List;

import org.sigmah.client.ClientFactory;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetValueHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class GetValueFromLinkedProjectsAsyncHandler implements AsyncCommandHandler<GetValueFromLinkedProjects, ListResult<String>> {

	/**
	 * Injected instance of {@link ProjectAsyncDAO}. 
	 */

	private ProjectAsyncDAO projectAsyncDAO;
	
	/**
	 * Injected instance of {@link ValueAsyncDAO}. 
	 */

	private ValueAsyncDAO valueAsyncDAO;
	
	public GetValueFromLinkedProjectsAsyncHandler(ClientFactory factory) {
		this.projectAsyncDAO = factory.getProjectAsyncDAO();
		this.valueAsyncDAO = factory.getValueAsyncDAO();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final GetValueFromLinkedProjects command, final OfflineExecutionContext executionContext, final AsyncCallback<ListResult<String>> callback) {
		
		projectAsyncDAO.get(command.getProjectId(), new AsyncCallback<ProjectDTO>() {
			
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void onSuccess(final ProjectDTO result) {
				findValuesOfProjectsLinkedToProject(result, command, callback);
			}
		});
	}
	
	/**
	 * Find the values of the given flexible element for every project linked
	 * by the given link type to the given project.
	 * 
	 * @param project
	 *			Parent project.
	 * @param command
	 *			Command object containing the flexible element and the link type.
	 * @param callback 
	 *			Callback to call with the result.
	 */
	private void findValuesOfProjectsLinkedToProject(final ProjectDTO project, final GetValueFromLinkedProjects command, final AsyncCallback<ListResult<String>> callback) {
		
		final List<ProjectDTO> linkedProjects = getLinkedProjectsForLinkType(project, command.getType());
				
		final List<String> values = new ArrayList<String>();
		final RequestManager<ListResult<String>> manager = new RequestManager<ListResult<String>>(new ListResult<String>(values), callback);

		for (final ProjectDTO linkedProject : linkedProjects) {
			final String valueId = ValueJSIdentifierFactory.toIdentifier(command.getElementEntityName(), linkedProject.getId(), command.getElementId(), null);

			valueAsyncDAO.get(valueId, new RequestManagerCallback<ListResult<String>, ValueResult>(manager) {

				/**
				 * {@inheritDoc}
				 */
				@Override
				public void onRequestSuccess(ValueResult result) {
					values.add(result.getValueObject());
				}

			});
		}

		manager.ready();
	}
	
	/**
	 * Creates a list containing every {@link ProjectDTO} associated to the
	 * given project by the given link type.
	 * 
	 * @param project
	 *			Parent project to search.
	 * @param type
	 *			Type of link between the parent project and the result.
	 * @return A list of every {@link ProjectDTO} associated to the given 
	 * project by the given link type.
	 */
	private List<ProjectDTO> getLinkedProjectsForLinkType(final ProjectDTO project, final ProjectFundingDTO.LinkedProjectType type) {
		
		final List<ProjectFundingDTO> projectFundings;
		switch (type) {
			case FUNDED_PROJECT:
				projectFundings = project.getFunded();
				break;
			case FUNDING_PROJECT:
				projectFundings = project.getFunding();
				break;
			default:
				throw new UnsupportedOperationException("Unsupported linked project type: " + type);
		}
		
		final List<ProjectDTO> linkedProjects = new ArrayList<ProjectDTO>();
		for (final ProjectFundingDTO projectFunding : projectFundings) {
			switch (type) {
				case FUNDED_PROJECT:
					linkedProjects.add(projectFunding.getFunded());
					break;
				case FUNDING_PROJECT:
					linkedProjects.add(projectFunding.getFunding());
					break;
				default:
					throw new UnsupportedOperationException("Unsupported linked project type: " + type);
			}
		}
		
		return linkedProjects;
	}
	
}
