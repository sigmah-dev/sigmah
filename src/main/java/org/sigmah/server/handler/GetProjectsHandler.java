package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.GetProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;
import org.sigmah.shared.dto.referential.ProjectModelType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import java.util.HashMap;

/**
 * Handler for {@link GetProjects} command.
 * 
 * @author Maxime Lombard (mlombard@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsHandler extends AbstractCommandHandler<GetProjects, ListResult<ProjectDTO>> {

	/**
	 * Logger.
	 */
	private final static Logger LOG = LoggerFactory.getLogger(GetProjectsHandler.class);

	/**
	 * Injected project mapper.
	 */
	private final ProjectMapper projectMapper;

	@Inject
	public GetProjectsHandler(final ProjectMapper projectMapper) {
		this.projectMapper = projectMapper;
	}

	/**
	 * <p>
	 * {@inheritDoc}
	 * </p>
	 * <p>
	 * Gets the projects list from the database.
	 * </p>
	 * 
	 * @return A {@link ListResult} containing the projects list.
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetProjects cmd, final UserExecutionContext context) throws CommandException {

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());

		LOG.debug("Gets projects for following command: {}", cmd);

		// Retrieves command parameters.
		final Set<Project> projects = new HashSet<Project>();
		final ProjectModelType modelType = cmd.getModelType();

		// ---------------
		// Projects which I own or I manage.
		// ---------------

		if (cmd.getViewOwnOrManage()) {
			final TypedQuery<Project> ownerManagerQuery = em().createQuery("SELECT p FROM Project p WHERE p.owner = :ouser OR p.manager = :muser", Project.class);
			ownerManagerQuery.setParameter("ouser", context.getUser());
			ownerManagerQuery.setParameter("muser", context.getUser());
			projects.addAll(ownerManagerQuery.getResultList());
		}
		
		// ---------------
		// Favorites projects.
		// ---------------
		
		if(cmd.isFavoritesOnly()) {
			final TypedQuery<Project> favoritesQuery = em().createQuery("FROM Project p WHERE :user MEMBER OF p.favoriteUsers", Project.class);
			favoritesQuery.setParameter("user", context.getUser());
			projects.addAll(favoritesQuery.getResultList());
		}
		
		// ---------------
		// Projects in my visible organization units.
		// ---------------

		final List<Integer> ids = cmd.getOrgUnitsIds();

		// Use a set to be avoid duplicated entries.
		final Set<OrgUnit> units = new HashSet<OrgUnit>();

		// Checks if there is at least one org unit id specified.
		if (ids == null) {
			LOG.debug("No org unit specified, gets all projects for the user org unit.");

			// Crawl the org units hierarchy from the user root org unit.
			Handlers.crawlUnits(context.getUser().getOrgUnitWithProfiles().getOrgUnit(), units, true);

		} else {
			// Crawl the org units hierarchy from each specified org unit.
			OrgUnit unit;
			for (final Integer id : ids) {
				if ((unit = em().find(OrgUnit.class, id)) != null) {
					Handlers.crawlUnits(unit, units, true);
				}
			}
		}
        
        // Keep a link between projects and orgUnits.
        final HashMap<Integer, Integer> projectIdToOrgUnitId = new HashMap<Integer, Integer>();
        
        // Creating the query to retrieve projects
        final TypedQuery<Project> query = buildQuery(cmd);

		// Retrieves all the corresponding org units.
		for (final OrgUnit unit : units) {

			// Builds and executes the query.
            fillQuery(query, cmd, context, unit);

			int count = 0;
			final List<Project> listResults = query.getResultList();
			for (final Project p : listResults) {
                projectIdToOrgUnitId.put(p.getId(), unit.getId());

				if (modelType == null) {
					projects.add(p);
					count++;
				}
				// Filters by model type.
				else {
					if (p.getProjectModel().getVisibility(context.getUser().getOrganization()) == modelType) {
						projects.add(p);
						count++;
					}
				}
			}

			LOG.debug("Found {}/{} projects for org unit #{}.", count, listResults.size(), unit.getName());
		}

		// ---------------
		// Mapping and return.
		// ---------------

		final List<ProjectDTO> projectsDTO = new ArrayList<>();
		final IsMappingMode mappingMode = cmd.getMappingMode(); // May be null.

		if (mappingMode == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
			// Using custom project mapper.
			for (final Project project : projects) {
				projectsDTO.add(projectMapper.map(project, true));
			}

		} else {
			// Using provided mapping mode.
            for(final Project project : projects) {
                final ProjectDTO projectDTO = mapper().map(project, new ProjectDTO(), cmd.getMappingMode());
                // Filling the orgUnitId using the map made when querying by OrgUnits.
                projectDTO.setOrgUnitId(projectIdToOrgUnitId.get(project.getId()));
                projectsDTO.add(projectDTO);
            }
		}

		LOG.debug("Found {} project(s).", projects.size());

		return new ListResult<>(projectsDTO);
	}
    
    private TypedQuery<Project> buildQuery(GetProjects getProjects) {
        final StringBuilder stringBuilder = new StringBuilder("SELECT p FROM Project p WHERE :unit MEMBER OF p.partners");
        
        if(getProjects.isFavoritesOnly()) {
            stringBuilder.append(" AND :user MEMBER OF p.favoriteUsers");
        }
        
        return em().createQuery(stringBuilder.toString(), Project.class); 
    }
    
    private void fillQuery(TypedQuery<Project> query, GetProjects getProjects, UserExecutionContext context, OrgUnit unit) {
        query.setParameter("unit", unit);
        if(getProjects.isFavoritesOnly()) {
            query.setParameter("user", context.getUser());
        }
    }
}
