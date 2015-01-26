/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.endpoint.gwtrpc.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ejb.HibernateEntityManager;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.handler.CommandHandler;
import org.sigmah.shared.command.result.CommandResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.Project;
import org.sigmah.shared.domain.ProjectModelType;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.dto.ProjectDTOLight;
import org.sigmah.shared.exception.CommandException;

import com.google.inject.Inject;

public class GetProjectsHandler implements CommandHandler<GetProjects> {

	private final static Log LOG = LogFactory.getLog(GetProjectsHandler.class);

	private final EntityManager em;
	private final ProjectMapper mapper;

	@Inject
	public GetProjectsHandler(EntityManager em, ProjectMapper mapper) {
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
	public CommandResult execute(GetProjects cmd, User user) throws CommandException {

		// Disable the ActivityInfo filter on Userdatabase.
		org.hibernate.Session session = ((HibernateEntityManager) em).getSession();
		session.disableFilter("userVisible");

		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Gets projects: " + cmd + ".");
		}

		// Retrieves command parameters.
		final HashSet<Project> projects = new HashSet<Project>();
		final ProjectModelType modelType = cmd.getModelType();

		// ---------------
		// Projects which I own or I manage.
		// ---------------

		if (cmd.getViewOwnOrManage()) {
			final Query ownerManagerQuery = em
			                .createQuery("SELECT p FROM Project p WHERE p.owner = :ouser OR p.manager = :muser");
			ownerManagerQuery.setParameter("ouser", user);
			ownerManagerQuery.setParameter("muser", user);
			for (final Project p : (List<Project>) ownerManagerQuery.getResultList()) {
				projects.add(p);
			}
		}
		// ---------------
		// Projects in my visible organization units.
		// ---------------

		final List<Integer> ids = cmd.getOrgUnitsIds();

		// Use a set to be avoid duplicated entries.
		final HashSet<OrgUnit> units = new HashSet<OrgUnit>();

		// Checks if there is at least one org unit id specified.
		if (ids == null) {

			if (LOG.isDebugEnabled()) {
				LOG.debug("[execute] No org unit specified, gets all projects for the user org unit.");
			}

			// Crawl the org units hierarchy from the user root org unit.
			GetProjectHandler.crawlUnits(user.getOrgUnitWithProfiles().getOrgUnit(), units, true);
		} else {

			// Crawl the org units hierarchy from each specified org unit.
			OrgUnit unit;
			for (final Integer id : ids) {
				if ((unit = em.find(OrgUnit.class, id)) != null) {
					GetProjectHandler.crawlUnits(unit, units, true);
				}
			}
		}

		// Retrieves all the corresponding org units.
		for (final OrgUnit unit : units) {

			// Builds and executes the query.
			final Query query = em.createQuery("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners");
			query.setParameter("unit", unit);

			int count = 0;
			final List<Project> listResults = (List<Project>) query.getResultList();
			for (final Project p : listResults) {

				if (modelType == null) {
					projects.add(p);
					count++;
				}
				// Filters by model type.
				else {
					if (p.getProjectModel().getVisibility(user.getOrganization()) == modelType) {
						projects.add(p);
						count++;
					}
				}
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("[execute] Found " + count + "/" + listResults.size() + " projects for org unit #"
				                + unit.getName() + ".");
			}
		}

		// ---------------
		// Mapping and return.
		// ---------------

		final ProjectListResult result = new ProjectListResult();

		switch (cmd.getReturnType()) {
		case PROJECT:
			// Not implemented.
			break;
		case ID:

			final ArrayList<Integer> projectsIds = new ArrayList<Integer>();
			for (final Project project : projects) {
				projectsIds.add(project.getId());
			}

			result.setListProjectsIds(projectsIds);
			break;

		case PROJECT_LIGHT:
		default:

			// Mapping into DTO objects
			final ArrayList<ProjectDTOLight> projectDTOList = new ArrayList<ProjectDTOLight>();
			for (final Project project : projects) {
				final ProjectDTOLight pLight = mapper.map(project, true);
				projectDTOList.add(pLight);
			}

			result.setListProjectsLightDTO(projectDTOList);
			break;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("[execute] Found " + projects.size() + " project(s).");
		}

		return result;
	}
}
