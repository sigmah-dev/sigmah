package org.sigmah.server.handler;

import org.sigmah.server.dispatch.impl.UserDispatch;
import org.sigmah.server.domain.Project;
import org.sigmah.server.handler.base.AbstractCommandHandler;
import org.sigmah.shared.command.GetMainSite;
import org.sigmah.shared.dispatch.CommandException;
import org.sigmah.shared.dto.SiteDTO;

/**
 * Handler for the {@link GetMainSiteHandler} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GetMainSiteHandler extends AbstractCommandHandler<GetMainSite, SiteDTO> {

	@Override
	protected SiteDTO execute(GetMainSite command, UserDispatch.UserExecutionContext context) throws CommandException {
		final Project project = em().find(Project.class, command.getProjectId());
		
		if(project == null) {
			throw new IllegalArgumentException("Project '" + command.getProjectId() + "' was not been found.");
		}
		
		return mapper().map(project.getMainSite(), new SiteDTO());
	}
	
}
