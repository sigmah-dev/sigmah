package org.sigmah.server.dao;

import java.util.Collection;
import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectModel;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.Project} domain object.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ProjectDAO extends DAO<Project, Integer> {

	/**
	 * Retrieves the {@link Project} list related to the given {@code pmodels}.
	 * 
	 * @param pmodels
	 *          The {@link ProjectModel} collection.
	 * @return The {@link Project} list related to the given {@code pmodels}.
	 */
	List<Project> getProjects(Collection<ProjectModel> pmodels);

	/**
	 * Retrieves the <b>active</b> (not deleted) <b>draft</b> {@link Project}s related to the given {@code ownerId}.<br>
	 * Draft projects have a project model with {@link ProjectModelStatus#DRAFT} status.
	 * 
	 * @param ownerId
	 *          The user (owner) id.
	 * @return The <b>active</b> (not deleted) <b>draft</b> {@link Project}s related to the given {@code ownerId} sorted
	 *         by fullname.
	 */
	List<Project> findDraftProjects(Integer ownerId);

}
