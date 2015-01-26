package org.sigmah.server.handler;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dispatch.impl.UserDispatch.UserExecutionContext;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetProjectsByModel;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Handler for {@link GetProjectsByModel} command.
 * 
 * @author HUZHE (v1.3)
 * @author Maxime Lombard (mlombard@ideia.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetProjectsByModelHandler extends AbstractCommandHandler<GetProjectsByModel, ListResult<ProjectDTO>> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListResult<ProjectDTO> execute(final GetProjectsByModel cmd, final UserExecutionContext context) throws CommandException {

		if (cmd == null || cmd.getProjectModelId() == null) {
			throw new CommandException("Invalid command arguments");
		}

		final TypedQuery<Project> query = em().createQuery("SELECT p FROM Project p WHERE p.projectModel.id = :projectId", Project.class);
		query.setParameter("projectId", cmd.getProjectModelId());

		final List<Project> projects = query.getResultList();

		return new ListResult<>(mapper().mapCollection(projects, ProjectDTO.class, cmd.getMappingMode()));
	}

}
