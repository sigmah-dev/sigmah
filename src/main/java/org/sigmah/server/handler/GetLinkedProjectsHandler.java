package org.sigmah.server.handler;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.server.handler.util.ProjectMapper;
import org.sigmah.shared.command.GetLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.base.mapping.IsMappingMode;

import com.google.inject.Inject;

/**
 * Handler for the {@link GetLinkedProjects} command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetLinkedProjectsHandler extends AbstractCommandHandler<GetLinkedProjects, ListResult<ProjectFundingDTO>> {

	/**
	 * Injected project mapper.
	 */
	@Inject
	private ProjectMapper projectMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectFundingDTO> execute(final GetLinkedProjects cmd, final UserExecutionContext context) throws CommandException {

		final Integer projectId = cmd.getProjectId();
		final LinkedProjectType type = cmd.getType();
		final IsMappingMode mappingMode = cmd.getMappingMode();

		if (projectId == null || type == null) {
			throw new CommandException("Invalid command arguments.");
		}

		final String queryTerm;
		switch (type) {
			case FUNDING_PROJECT:
				queryTerm = "funding";
				break;

			case FUNDED_PROJECT:
				queryTerm = "funded";
				break;

			default:
				throw new CommandException("Invalid linked project type.");
		}

		final Query query = em().createQuery("SELECT p." + queryTerm + " FROM Project p WHERE p.id = :projectId");
		query.setParameter("projectId", projectId);

		@SuppressWarnings("unchecked")
		final List<ProjectFunding> results = query.getResultList();

		final List<ProjectFundingDTO> dtos = new ArrayList<ProjectFundingDTO>();
		for (final ProjectFunding pf : results) {

			final ProjectFundingDTO pfDTO = new ProjectFundingDTO();
			pfDTO.setId(pf.getId());
			pfDTO.setPercentage(pf.getPercentage());

			if (mappingMode == ProjectDTO.Mode._USE_PROJECT_MAPPER) {
				pfDTO.setFunding(projectMapper.map(pf.getFunding(), false));
				pfDTO.setFunded(projectMapper.map(pf.getFunded(), false));

			} else {
				pfDTO.setFunding(mapper().map(pf.getFunding(), ProjectDTO.class, mappingMode));
				pfDTO.setFunded(mapper().map(pf.getFunded(), ProjectDTO.class, mappingMode));
			}

			dtos.add(pfDTO);
		}

		return new ListResult<>(dtos);
	}
}
