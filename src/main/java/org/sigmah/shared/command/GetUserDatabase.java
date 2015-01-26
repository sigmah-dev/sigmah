package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDatabaseDTO;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUserDatabase extends AbstractCommand<ListResult<UserDatabaseDTO>> {

	/**
	 * The type of model of the models for the current user organization (set to <code>null</code> to ignore this filter).
	 */
	public GetUserDatabase() {
		// Serialization.
	}
}
