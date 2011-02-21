/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dozer.Mapper;
import org.hibernate.ejb.HibernateEntityManager;
import org.sigmah.shared.command.GetProjectsFromId;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetProjectsFromIdHandler implements CommandHandler<GetProjectsFromId> {

    private final EntityManager em;
    private final Mapper mapper;

    private final static Log LOG = LogFactory.getLog(GetProjectsFromIdHandler.class);

    @Inject
    public GetProjectsFromIdHandler(EntityManager em, Mapper mapper) {
        this.em = em;
        this.mapper = mapper;
    }

    /**
     * Gets the projects list from the database.
     * 
     * @param cmd
     * @param user
     * 
     * @return a {@link CommandResult} object containing the
     *         {@link ProjectListResult} object.
     */
    @Override
    @SuppressWarnings("unchecked")
    public CommandResult execute(GetProjectsFromId cmd, User user) throws CommandException {

        // Disable the ActivityInfo filter on Userdatabase.
        org.hibernate.Session session = ((HibernateEntityManager) em).getSession();
        session.disableFilter("userVisible");

        if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Gets projects: " + cmd + ".");
        }

        final Query query = em.createQuery("SELECT p FROM Project p WHERE p.id IN (:ids)");
        query.setParameter("ids", cmd.getIds());

        final List<Project> projects = (List<Project>) query.getResultList();

        // ---------------
        // Mapping and return.
        // ---------------

        final ProjectListResult result = new ProjectListResult();

        // Mapping into DTO objects
        final ArrayList<ProjectDTOLight> projectDTOList = new ArrayList<ProjectDTOLight>();
        for (final Project project : projects) {
            final ProjectDTOLight pLight = GetProjectsHandler.mapProject(em, mapper, user, project, true);
            projectDTOList.add(pLight);
        }

        result.setListProjectsLightDTO(projectDTOList);

        if (LOG.isDebugEnabled()) {
            LOG.debug("[execute] Found " + projects.size() + " project(s).");
        }

        return result;
    }
}
