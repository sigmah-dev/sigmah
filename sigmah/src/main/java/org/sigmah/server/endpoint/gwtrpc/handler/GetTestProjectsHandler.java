package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.sigmah.shared.command.GetTestProjects;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectDTOLightListResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModel;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetTestProjectsHandler implements CommandHandler<GetTestProjects> {

    private static final Log log = LogFactory.getLog(GetTestProjectsHandler.class);

    private final EntityManager em;
    private final Mapper mapper;

    @Inject
    public GetTestProjectsHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    @Override
    public CommandResult execute(GetTestProjects cmd, User user) throws CommandException {

        if (log.isDebugEnabled()) {
            log.debug("[execute] Retrieving project with status '" + cmd.getProjectModelStatus() + "'.");
        }

        final ArrayList<ProjectDTOLight> projectDTOList = new ArrayList<ProjectDTOLight>();

        // Creates selection query.
        final Query query = em.createQuery("SELECT p FROM Project p where p.owner.id = "+user.getId()+" ORDER BY p.fullName");

        // Gets all project models entities.
        @SuppressWarnings("unchecked")
        final List<Project> projects = (List<Project>) query.getResultList();

        // Mapping (entity -> dto).
        if (projects != null) {
            for (final Project project : projects) {
            	      	
				final ProjectModel model = project.getProjectModel();
				// Filters with the project status DRAFT.
				if (model.getStatus() != null && ProjectModelStatus.DRAFT.equals(model.getStatus())) {
					projectDTOList.add(mapper.map(project, ProjectDTOLight.class));
				}
            }
		}

        if (log.isDebugEnabled()) {
            log.debug("[execute] Found " + projectDTOList.size() + " project models.");
        }

        return new ProjectDTOLightListResult(projectDTOList);
    }

}
