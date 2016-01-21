package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.reminder.MonitoredPoint;
import org.sigmah.server.domain.util.DomainFilters;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.Handlers;
import org.sigmah.shared.command.GetMonitoredPoints;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * Handler for the {@link GetMonitoredPoints} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMonitoredPointsHandler extends AbstractCommandHandler<GetMonitoredPoints, ListResult<MonitoredPointDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<MonitoredPointDTO> execute(final GetMonitoredPoints cmd, final UserExecutionContext context) throws CommandException {

		final List<MonitoredPointDTO> dtos;

		if (cmd.getProjectId() != null) {
			dtos = findProjectPoints(cmd.getProjectId(), cmd.getMappingMode(), context);

		} else {
			dtos = findAllProjectsPoints(cmd.getMappingMode(), context);
		}

		return new ListResult<>(dtos);
	}

	/**
	 * Finds the given {@code projectId} corresponding monitored points.
	 * 
	 * @param projectId
	 *          The project id.
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The monitored points DTOs.
	 */
	private List<MonitoredPointDTO> findProjectPoints(final Integer projectId, final MonitoredPointDTO.Mode mappingMode, final UserExecutionContext context) {

		// Disable the ActivityInfo filter on Userdatabase.
		DomainFilters.disableUserFilter(em());
		
		final Query query = em().createQuery("SELECT p.pointsList.points FROM Project p WHERE p.id = :projectId");
		query.setParameter("projectId", projectId);

		@SuppressWarnings("unchecked")
		final List<MonitoredPoint> monitoredPoints = query.getResultList();

		return new ArrayList<>(mapper().mapCollection(monitoredPoints, MonitoredPointDTO.class, mappingMode));
	}

	/**
	 * Finds the monitored points for all the projects.
	 * 
	 * @param mappingMode
	 *          The mapping mode, may be {@code null}.
	 * @param context
	 *          The user execution context.
	 * @return The monitored points DTOs.
	 */
	private List<MonitoredPointDTO> findAllProjectsPoints(final MonitoredPointDTO.Mode mappingMode, final UserExecutionContext context) {

		final List<MonitoredPointDTO> dtos = new ArrayList<>();

		DomainFilters.disableUserFilter(em());

		// Use a set to be avoid duplicated entries.
		final Set<OrgUnit> units = new HashSet<>();

		// Crawl the org units hierarchy from the user root org unit.
		Handlers.crawlUnits(context.getUser(), units, true);

		// Retrieves all the corresponding org units.
		for (final OrgUnit unit : units) {

			// Builds and executes the query.
			final Query query = em().createQuery("SELECT p.pointsList.points FROM Project p WHERE :unit MEMBER OF p.partners");
			query.setParameter("unit", unit);

			@SuppressWarnings("unchecked")
			final List<MonitoredPoint> monitoredPoints = query.getResultList();

			for (final MonitoredPoint monitoredPoint : monitoredPoints) {

				if (monitoredPoint.getCompletionDate() != null) {
					continue; // Not completed only.
				}

                final TypedQuery<Project> fullNameQuery = em().createQuery("SELECT p FROM Project p WHERE p.pointsList = :pointsList", Project.class);
                
                fullNameQuery.setParameter("pointsList", monitoredPoint.getParentList());
               
                final MonitoredPointDTO monitoredPointDTO = mapper().map(monitoredPoint, new MonitoredPointDTO(), mappingMode);
                
                Project project = fullNameQuery.getSingleResult();
                
                monitoredPointDTO.setProjectId(project.getId());
                monitoredPointDTO.setProjectName(project.getName());
                monitoredPointDTO.setProjectCode(project.getFullName());
                
				dtos.add(monitoredPointDTO);

			}
		}

		return dtos;
	}

}
