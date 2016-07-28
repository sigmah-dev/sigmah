package org.sigmah.server.handler.util;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.Phase;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that centralize search for conflicts.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Conflicts extends EntityManagerProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Conflicts.class);
	
	@Inject
	private I18nServer i18nServer;
	
	
	/**
	 * Identify if the phase containing the given element is closed.
	 * @param elementId Identifier of a flexible element.
	 * @param projectId Identifier of the project.
	 * @return <code>true</code> if the parent phase is closed,
	 * <code>false</code> if the parent phase is opened.
	 */
	public boolean isParentPhaseClosed(int elementId, int projectId) {
		// Retrieves the parent phase only if it exists and has been closed.
		final TypedQuery<Phase> query = em().createQuery("SELECT p FROM "
			+ "Phase p "
			+ "JOIN p.phaseModel.layout.groups as lg "
			+ "JOIN lg.constraints as lc "
			+ "WHERE p.endDate is not null "
			+ "AND :projectId = p.parentProject.id "
			+ "AND :elementId = lc.element.id", Phase.class);

		query.setParameter("projectId", projectId);
		query.setParameter("elementId", elementId);
		
		return !query.getResultList().isEmpty();
	}
	
	// File conflicts
	
	/**
	 * Find if a conflict will happen when adding a file.
	 * 
	 * @param properties Properties of the file flexible element.
	 * @param language Language of the message.
	 * @param user User.
	 * @throws UpdateConflictException If a conflict has been detected.
	 */
	public void searchForFileAddConflicts(final Map<String, String> properties, final Language language, final User user) throws UpdateConflictException {
		// Element.
		final int elementId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT), -1);
		final FilesListElement filesListElement = elementId != -1 ? em().find(FilesListElement.class, elementId) : null;
		
		// Project.
		final int projectId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_PROJECT), 0);
		final Project project = em().find(Project.class, projectId);
		
		if(project != null && !Handlers.isGranted(user.getOrgUnitWithProfiles(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
			if(project.getCloseDate() != null) {
				final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
				throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToAClosedProject", fileName, filesListElement.getLabel()));
			}

			if(isParentPhaseClosed(elementId, projectId)) {
				final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
				throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToAClosedPhase", fileName, filesListElement.getLabel()));
			}

			if(filesListElement != null && filesListElement.isAmendable() && project.getAmendmentState() == AmendmentState.LOCKED) {
				final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
				throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToALockedField", fileName, filesListElement.getLabel()));
			}
		}
		
		if(filesListElement != null && filesListElement.getLimit() != null) {
			// Retrieving the current value
			final TypedQuery<Value> valueQuery = em().createQuery("SELECT v FROM Value v WHERE v.containerId = :projectId and v.element.id = :elementId", Value.class);
			valueQuery.setParameter("projectId", projectId);
			valueQuery.setParameter("elementId", elementId);

			Value currentValue = null;

			try {
				currentValue = valueQuery.getSingleResult();
			} catch (NoResultException nre) {
				// No current value
			}

			if(currentValue != null) {
				final TypedQuery<File> fileQuery = em().createQuery("SELECT f FROM File f WHERE f.id IN (:idsList)", File.class);
				fileQuery.setParameter("idsList", ValueResultUtils.splitValuesAsInteger(currentValue.getValue()));

				final List<File> files = fileQuery.getResultList();
				if(files.size() >= filesListElement.getLimit()) {
					final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
					throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToAFullFileField", fileName, filesListElement.getLabel()));
				}
			}
		}
	}
	
	/**
	 * Find if a conflict will happen when deleting a file.
	 * 
	 * @param file File to delete.
	 * @param language Language of the message.
	 * @param user User.
	 * @throws UpdateConflictException If a conflict has been detected.
	 */
	public void searchForFileDeleteConflicts(final File file, final Language language, final User user) throws UpdateConflictException {
		if (Handlers.isGranted(user.getOrgUnitWithProfiles(), GlobalPermissionEnum.MODIFY_LOCKED_CONTENT)) {
			return;
		}
		
		final Project project = getParentProjectOfFile(file);
		final FilesListElement filesListElement = getParentFilesListElement(file);

		if(filesListElement != null && project != null) {
			if(project.getCloseDate() != null) {
				throw new UpdateConflictException(project.toContainerInformation(), i18nServer.t(language, "conflictRemovingFileFromAClosedProject", filesListElement.getLabel()));

			} else if(isParentPhaseClosed(filesListElement.getId(), project.getId())) {
				throw new UpdateConflictException(project.toContainerInformation(), i18nServer.t(language, "conflictRemovingFileFromAClosedPhase", filesListElement.getLabel()));

			} else if(project.getAmendmentState() == AmendmentState.LOCKED && filesListElement.isAmendable()) {
				throw new UpdateConflictException(project.toContainerInformation(), i18nServer.t(language, "conflictRemovingFileFromALockedField", filesListElement.getLabel()));
			}
		}
	}
 	
	// File search method.
	
	/**
	 * Find the project containing the given file.
	 * 
	 * @param file File contained in a file flexible element.
	 * @return The parent project.
	 */
	public Project getParentProjectOfFile(final File file) {
		final TypedQuery<Project> phaseQuery = em().createQuery("SELECT p FROM "
				+ "Value v, "
				+ "Project p "
				+ "WHERE v.containerId = p.id "
				+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight OR v.value like :fileIdCenter)", Project.class);
		
		final TypedQuery<Project> detailsQuery = em().createQuery("SELECT p FROM "
				+ "Value v, "
				+ "Project p "
				+ "WHERE v.containerId = p.id "
				+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight OR v.value like :fileIdCenter)", Project.class);

		setFileQueryParameters(phaseQuery, file.getId());
		setFileQueryParameters(detailsQuery, file.getId());

		final ArrayList<Project> projects = new ArrayList<>();
		projects.addAll(phaseQuery.getResultList());
		projects.addAll(detailsQuery.getResultList());

		if(!projects.isEmpty()) {
			if(projects.size() > 1) {
				LOGGER.warn("{} projects have been found while searching for the parent of file {}.", projects.size(), file.getId());
			}
			return projects.get(0);
		}
		
		return null;
	}
	
	/**
	 * Find the FilesListElement containing the given file.
	 * 
	 * @param file File contained in a file flexible element.
	 * @return The parent flexible element.
	 */
	public FilesListElement getParentFilesListElement(final File file) {
		final TypedQuery<FilesListElement> query = em().createQuery("SELECT fle FROM "
			+ "Value v, "
			+ "FilesListElement fle "
			+ "WHERE v.element = fle "
			+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight OR v.value like :fileIdCenter)", FilesListElement.class);
		
		setFileQueryParameters(query, file.getId());
		
		final List<FilesListElement> elements = query.getResultList();
		if(!elements.isEmpty()) {
			if(elements.size() > 1) {
				LOGGER.warn("{} elements have been found while searching for the parent of file {}.", elements.size(), file.getId());
			}
			
			return elements.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * Affect the parameters of the given query to search for the given file.
	 * 
	 * @param query Query to configure.
	 * @param fileId Identifier of the file.
	 */
	private void setFileQueryParameters(TypedQuery<?> query, Integer fileId) {
		query.setParameter("fileId", fileId.toString());
		query.setParameter("fileIdLeft", '%' + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + fileId);
		query.setParameter("fileIdRight", fileId + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + '%');
		query.setParameter("fileIdCenter", '%' + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + fileId + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + '%');
	}
	
}
