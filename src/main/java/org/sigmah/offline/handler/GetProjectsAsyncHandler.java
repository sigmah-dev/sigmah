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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.sigmah.offline.dao.OrgUnitAsyncDAO;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetProjectsHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class GetProjectsAsyncHandler implements AsyncCommandHandler<GetProjects, ListResult<ProjectDTO>>, DispatchListener<GetProjects, ListResult<ProjectDTO>> {

	private final Authentication authentication;
	private final ProjectAsyncDAO projectAsyncDAO;
	private final OrgUnitAsyncDAO orgUnitAsyncDAO;

	@Inject
	public GetProjectsAsyncHandler(Authentication authentication, ProjectAsyncDAO projectAsyncDAO, OrgUnitAsyncDAO orgUnitAsyncDAO) {
		this.authentication = authentication;
		this.projectAsyncDAO = projectAsyncDAO;
		this.orgUnitAsyncDAO = orgUnitAsyncDAO;
	}

	@Override
	public void execute(final GetProjects command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ProjectDTO>> callback) {
		final HashSet<Integer> orgUnits = new HashSet<Integer>();

		if (command.getOrgUnitsIds() == null) {
			command.setOrgUnitsIds(new ArrayList<Integer>(authentication.getOrgUnitIds()));
		}
		
		// Adding the org unit 0 to also retrieve draft projects.
		orgUnits.add(0);

        final RequestManager<Set<Integer>> requestManager = new RequestManager<Set<Integer>>(orgUnits, new AsyncCallback<Set<Integer>>() {

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess(Set<Integer> result) {
                executeCommand(orgUnits, command, callback);
            }
        });

        // Searching for sub-orgUnits.
		for (final Integer orgUnitId : command.getOrgUnitsIds()) {
			orgUnitAsyncDAO.get(orgUnitId, new RequestManagerCallback<Set<Integer>, OrgUnitDTO>(requestManager) {
                
                @Override
                public void onRequestSuccess(OrgUnitDTO result) {
                    crawlUnits(result, orgUnits);
                }
            });
		}
        
        requestManager.ready();
	}

	private void executeCommand(final Collection<Integer> orgUnits, final GetProjects command, final AsyncCallback<ListResult<ProjectDTO>> callback) {
		if (command.getOrgUnitsIds() != null) {
			projectAsyncDAO.getProjectsByOrgUnits(orgUnits, callback);
			
		} else {
			throw new UnsupportedOperationException("Projects cannot be retrieved without orgUnits ids.");
		}
	}

	private void crawlUnits(OrgUnitDTO root, Collection<Integer> units) {
		units.add(root.getId());

		final Set<OrgUnitDTO> children = root.getChildrenOrgUnits();
		if (children != null) {
			for (OrgUnitDTO child : children) {
				crawlUnits(child, units);
			}
		}
	}

    @Override
    public void onSuccess(GetProjects command, ListResult<ProjectDTO> result, Authentication authentication) {
        if(command.getMappingMode() == null && result != null && result.getList() != null) {
            projectAsyncDAO.saveAll(result.getList(), null);
        }
    }
}
