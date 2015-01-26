package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.UserDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * Retrieves the organization's users list with their {@link ProfileDTO}(s) and {@link OrgUnitDTO}.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetUsersWithProfiles extends AbstractCommand<ListResult<UserDTO>> {

}
