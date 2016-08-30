package org.sigmah.server.dao;

import java.util.List;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 * Data Access Object for the {@link ProjectFunding} domain object.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface ProjectFundingDAO {
	
	/**
	 * Retrieves the projects related to the given project.
	 * 
	 * @param projectId
	 *			Identifier of the project.
	 * @param linkedProjectType
	 *			Type of link with the given project.
	 * @return The {@link ProjectFunding} list related to the given project.
	 */
	List<ProjectFunding> getLinkedProjects(Integer projectId, ProjectFundingDTO.LinkedProjectType linkedProjectType);
	
}
