package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Retrieves the <b>active</b> (not deleted) <b>draft</b> projects owned by the authenticated user.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetTestProjects extends AbstractCommand<ListResult<ProjectDTO>> {

}
