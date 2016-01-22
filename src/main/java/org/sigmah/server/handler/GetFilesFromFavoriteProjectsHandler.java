package org.sigmah.server.handler;

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

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.offline.presenter.TreeGridFileModel;
import org.sigmah.server.dao.impl.FileHibernateDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.OrgUnitDetails;
import org.sigmah.server.domain.PhaseModel;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectDetails;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.layout.LayoutConstraint;
import org.sigmah.server.domain.layout.LayoutGroup;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.server.file.FileStorageProvider;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetFilesFromFavoriteProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.ValueResultUtils;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class GetFilesFromFavoriteProjectsHandler extends AbstractCommandHandler<GetFilesFromFavoriteProjects, ListResult<TreeGridFileModel>> {

	/**
	 * DAO to search for files in the database.
	 */
	@Inject
	private FileHibernateDAO fileHibernateDAO;
	
	/**
	 * Allow access to the files.
	 */
	@Inject
	private FileStorageProvider fileStorageProvider;
	
	@Override
	protected ListResult<TreeGridFileModel> execute(GetFilesFromFavoriteProjects command, UserDispatch.UserExecutionContext context) throws CommandException {
		
		// Result
		final List<TreeGridFileModel> models = new ArrayList<>();
		
		// Creating queries
		final TypedQuery<Project> projectQuery = em().createQuery("SELECT p FROM Project p WHERE :user MEMBER OF p.favoriteUsers", Project.class);
		final TypedQuery<String> valueQuery = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :containerId AND v.element.id = :elementId", String.class);
		
		// Searching for favorites projects of the current user
		projectQuery.setParameter("user", context.getUser());
		final List<Project> projects = projectQuery.getResultList();
		
		// Searching every FilesListElement of the found projects
		for(final Project project : projects) {
			final List<Integer> fileIds = new ArrayList<>();
			
			for(final PhaseModel phaseModel : project.getProjectModel().getPhaseModels()) {
				getFilesFromGroups(phaseModel.getLayout().getGroups(), valueQuery, project.getId(), fileIds);
			}
			
			final ProjectDetails projectDetails = project.getProjectModel().getProjectDetails();
			if(projectDetails != null) {
				getFilesFromGroups(projectDetails.getLayout().getGroups(), valueQuery, project.getId(), fileIds);
			}
			
			if(!fileIds.isEmpty()) {
				final List<FileVersionDTO> versions = getLastVersions(fileIds);
				
				if(!versions.isEmpty()) {
					final ProjectDTO projectDTO = mapper().map(project, new ProjectDTO());
					
					final TreeGridFileModel model = new TreeGridFileModel(projectDTO);
					model.setChildren(versions);
					
					models.add(model);
				}
			}
		}
		
		// BUGFIX #785: Searching files from org units
		final Set<OrgUnit> units = new HashSet<>();

		// Crawl the org units hierarchy from the user root org unit.
		Handlers.crawlUnits(context.getUser().getOrgUnitWithProfiles().getOrgUnit(), units, true);
		
		for(final OrgUnit unit : units) {
			final List<Integer> fileIds = new ArrayList<>();
			
			final OrgUnitDetails orgUnitDetails = unit.getOrgUnitModel().getDetails();
			if(orgUnitDetails != null) {
				getFilesFromGroups(orgUnitDetails.getLayout().getGroups(), valueQuery, unit.getId(), fileIds);
			}
			
			if(!fileIds.isEmpty()) {
				final List<FileVersionDTO> versions = getLastVersions(fileIds);
				
				if(!versions.isEmpty()) {
					final OrgUnitDTO orgUnitDTO = mapper().map(unit, new OrgUnitDTO());
					
					final TreeGridFileModel model = new TreeGridFileModel(orgUnitDTO);
					model.setChildren(versions);
					
					models.add(model);
				}
			}
		}
		
		return new ListResult<>(models);
	}

	/**
	 * Creates a list from the last versions of the given files.
	 * <p/>
	 * File versions will only be added if they exists on the server.
	 * 
	 * @param fileIds Files to search.
	 * @return A list of FileVersionDTO.
	 */
	private List<FileVersionDTO> getLastVersions(final List<Integer> fileIds) {
		final List<FileVersion> result = fileHibernateDAO.findVersions(fileIds, FileDTO.LoadingScope.LAST_VERSION_FROM_NOT_DELETED_FILES);
		
		final List<FileVersionDTO> versions = new ArrayList<>(mapper().mapCollection(result, FileVersionDTO.class));
		
		final Iterator<FileVersionDTO> iterator = versions.iterator();
		while(iterator.hasNext()) {
			final FileVersionDTO version = iterator.next();
			version.setAvailable(fileStorageProvider.exists(version.getPath()));
			
			if(!version.isAvailable()) {
				iterator.remove();
			}
		}
		
		return versions;
	}

	/**
	 * Adds every file ids for the given groups in the given container.
	 * 
	 * @param groups Layout groups.
	 * @param valueQuery Request to fill.
	 * @param containerId Identifier of the parent (project id or org unit id).
	 * @param fileIds List where to add results.
	 */
	private void getFilesFromGroups(final List<LayoutGroup> groups, final TypedQuery<String> valueQuery, int containerId, final List<Integer> fileIds) {
		for(final LayoutGroup group : groups) {
			for(final LayoutConstraint constraint : group.getConstraints()) {
				final FlexibleElement flexibleElement = constraint.getElement();
				
				if(flexibleElement instanceof FilesListElement) {
					// Retrieving the file ids
					valueQuery.setParameter("containerId", containerId);
					valueQuery.setParameter("elementId", flexibleElement.getId());
					
					try {
						final String value = valueQuery.getSingleResult();
						if(value != null) {
							fileIds.addAll(ValueResultUtils.splitValuesAsInteger(value));
						}
						
					} catch (NoResultException | ClassCastException e) {
						// No value
					}
				}
			}
		}
	}
	
}
