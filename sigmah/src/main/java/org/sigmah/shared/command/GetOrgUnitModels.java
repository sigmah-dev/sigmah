package org.sigmah.shared.command;

import org.sigmah.shared.command.result.OrgUnitModelListResult;

/**
 * Retrieves the list of org unit models available to the user.
 * 
 * @author nrebiai
 * 
 */
public class GetOrgUnitModels implements Command<OrgUnitModelListResult> {

    private static final long serialVersionUID = 6533084223987010888L;

    public GetOrgUnitModels() {
        // serialization.
    }
}
