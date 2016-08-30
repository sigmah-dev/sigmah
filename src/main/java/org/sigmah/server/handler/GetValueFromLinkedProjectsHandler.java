package org.sigmah.server.handler;

import com.google.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.ProjectFundingDAO;
import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetValueFromLinkedProjects;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dispatch.CommandException;

/**
 * Handler for {@link GetValueFromLinkedProjects} command.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class GetValueFromLinkedProjectsHandler extends AbstractCommandHandler<GetValueFromLinkedProjects, ListResult<String>> {
	
	/**
	 * Injected project project funding DAO.
	 */
	@Inject
	private ProjectFundingDAO projectFundingDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ListResult<String> execute(GetValueFromLinkedProjects command, UserDispatch.UserExecutionContext context) throws CommandException {
		final List<ProjectFunding> fundings = projectFundingDAO.getLinkedProjects(command.getProjectId(), command.getType());
		
		final List<Integer> containerIds = new ArrayList<>();
		for (final ProjectFunding funding : fundings) {
			final Project project;
			switch (command.getType()) {
				case FUNDED_PROJECT:
					project = funding.getFunded();
					break;
				case FUNDING_PROJECT:
					project = funding.getFunding();
					break;
				default:
					throw new CommandException("Unsupported linked project type: " + command.getType());
			}
			containerIds.add(project.getId());
		}

		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId IN :containerIds AND v.element.id = :elementId", String.class);
		query.setParameter("containerIds", containerIds);
		query.setParameter("elementId", command.getElementId());

		return new ListResult<>(new ArrayList<>(query.getResultList()));
	}
	
}
