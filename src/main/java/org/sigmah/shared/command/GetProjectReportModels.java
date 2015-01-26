package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ModelReference;

/**
 * Retrieves every report model available to the user.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectReportModels extends AbstractCommand<ListResult<ModelReference>> {

	public GetProjectReportModels() {
		// Serialization.
	}

}
