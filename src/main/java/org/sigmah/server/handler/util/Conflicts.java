package org.sigmah.server.handler.util;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.Phase;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.element.FilesListElement;
import org.sigmah.server.domain.value.File;
import org.sigmah.server.domain.value.FileVersion;
import org.sigmah.shared.util.ValueResultUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Conflicts extends EntityManagerProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Conflicts.class);
	
	public boolean isParentPhaseClosed(int elementId, int projectId) {
		// Retrieves the parent phase only if it exists and has been closed.
		final TypedQuery<Phase> query = em().createQuery("SELECT p FROM Phase p "
			+ "JOIN p.phaseModel.layout.groups as lg "
			+ "JOIN lg.constraints as lc "
			+ "WHERE p.endDate is not null "
			+ "AND :projectId = p.parentProject.id "
			+ "AND :elementId = lc.element.id", Phase.class);

		query.setParameter("projectId", projectId);
		query.setParameter("elementId", elementId);
		
		return !query.getResultList().isEmpty();
	}
	
	// File search method.
	
	public Project getParentProjectOfFileVersion(FileVersion version) {
		return getParentProjectOfFile(version.getParentFile());
	}
	
	public Project getParentProjectOfFile(File file) {
		final TypedQuery<Project> phaseQuery = em().createQuery("SELECT p FROM "
				+ "Value v, "
				+ "Project p "
				+ "JOIN p.projectModel.phaseModels pm "
				+ "JOIN pm.layout.groups as lg "
				+ "JOIN lg.constraints as lc "
				+ "WHERE v.element = lc.element "
				+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight)", Project.class);

		final TypedQuery<Project> detailsQuery = em().createQuery("SELECT p FROM "
				+ "Value v, "
				+ "Project p "
				+ "JOIN p.projectModel.projectDetails.layout.groups as lg "
				+ "JOIN lg.constraints as lc "
				+ "WHERE v.element = lc.element "
				+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight)", Project.class);

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
		final TypedQuery<FilesListElement> query = em().createQuery("SELECT p FROM "
			+ "Value v, "
			+ "FilesListElement fle "
			+ "WHERE v.element = fle "
			+ "AND (v.value = :fileId OR v.value like :fileIdLeft OR v.value like :fileIdRight)", FilesListElement.class);
		
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
		query.setParameter("fileIdLeft", ValueResultUtils.DEFAULT_VALUE_SEPARATOR + fileId);
		query.setParameter("fileIdRight", fileId + ValueResultUtils.DEFAULT_VALUE_SEPARATOR);
	}
}
