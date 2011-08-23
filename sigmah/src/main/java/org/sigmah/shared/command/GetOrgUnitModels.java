package org.sigmah.shared.command;

import org.sigmah.shared.command.result.OrgUnitModelListResult;
import org.sigmah.shared.domain.ProjectModelStatus;

/**
 * Retrieves the list of org unit models available to the user.
 * 
 * @author nrebiai
 * 
 */
public class GetOrgUnitModels implements Command<OrgUnitModelListResult> {

    private static final long serialVersionUID = 6533084223987010888L;

    private ProjectModelStatus status;

    public GetOrgUnitModels() {
        // serialization.
    }

    public GetOrgUnitModels(ProjectModelStatus status) {
        this.status = status;
    }

    public ProjectModelStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectModelStatus status) {
        this.status = status;
    }

}
