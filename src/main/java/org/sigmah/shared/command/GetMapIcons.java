package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.MapIconDTO;

/**
 * Returns a list of available {@code org.sigmah.shared.report.model.MapIcon}s on the server
 *
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetMapIcons extends GetListCommand<ListResult<MapIconDTO>> {

	public GetMapIcons() {
		// Serialization.
	}

}
