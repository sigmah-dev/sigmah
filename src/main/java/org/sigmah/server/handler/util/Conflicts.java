package org.sigmah.server.handler.util;

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
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.Value;
import org.sigmah.server.i18n.I18nServer;
import org.sigmah.shared.Language;
import org.sigmah.shared.dispatch.UpdateConflictException;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Conflicts extends EntityManagerProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Conflicts.class);
	
	@Inject
	private I18nServer i18nServer;
	
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
	
	public void searchForFileAddConflicts(Map<String, String> properties, Language language) throws UpdateConflictException {
		// Element.
		final int elementId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT), 0);
		final FilesListElement filesListElement = em().find(FilesListElement.class, elementId);
		
		// Project.
		final int projectId = ClientUtils.asInt(properties.get(FileUploadUtils.DOCUMENT_PROJECT), 0);
		final Project project = em().find(Project.class, projectId);
		
		if(project.getCloseDate() != null) {
			final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
			throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToAClosedProject", fileName, filesListElement.getLabel()));
		}
		
		if(isParentPhaseClosed(elementId, projectId)) {
			final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
			throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToAClosedPhase", fileName, filesListElement.getLabel()));
		}
		
		if(filesListElement.isAmendable() && project.getAmendmentState() == AmendmentState.LOCKED) {
			final String fileName = ValueResultUtils.normalizeFileName(properties.get(FileUploadUtils.DOCUMENT_NAME));
			throw new UpdateConflictException(project.toContainerInformation(), true, i18nServer.t(language, "conflictAddingFileToALockedField", fileName, filesListElement.getLabel()));
		}
		
		if(filesListElement.getLimit() != null) {
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
	
	// File search method.
	
	public Project getParentProjectOfFile(File file) {
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
	
	public FilesListElement getParentFilesListElement(File file) {
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
	
	private void setFileQueryParameters(TypedQuery<?> query, Integer fileId) {
		query.setParameter("fileId", fileId.toString());
		query.setParameter("fileIdLeft", '%' + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + fileId);
		query.setParameter("fileIdRight", fileId + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + '%');
		query.setParameter("fileIdCenter", '%' + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + fileId + ValueResultUtils.DEFAULT_VALUE_SEPARATOR + '%');
	}
}
