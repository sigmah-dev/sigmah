package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetPrivacyGroups extends AbstractCommand<ListResult<PrivacyGroupDTO>> {

	public GetPrivacyGroups() {
		// Serialization.
	}
}
