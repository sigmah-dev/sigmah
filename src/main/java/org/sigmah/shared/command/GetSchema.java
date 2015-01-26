package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.SchemaDTO;

/**
 * Returns a {@link org.sigmah.shared.dto.SchemaDTO} data transfer object that includes the definitions of a databases
 * visible to the authenticated user.
 *
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetSchema extends AbstractCommand<SchemaDTO> {

	public GetSchema() {
		// Serialization.
	}
}
