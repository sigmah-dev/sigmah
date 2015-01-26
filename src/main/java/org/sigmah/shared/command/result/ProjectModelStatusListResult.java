package org.sigmah.shared.command.result;

import org.sigmah.shared.domain.ProjectModelStatus;

public class ProjectModelStatusListResult implements CommandResult {

    private static final long serialVersionUID = 6008332325356165720L;

    private ProjectModelStatus[] status;

    public ProjectModelStatusListResult() {
        // serialization.
    }

    public ProjectModelStatusListResult(ProjectModelStatus... status) {
        this.status = status;
    }

    public void setStatus(ProjectModelStatus[] status) {
        this.status = status;
    }

    public ProjectModelStatus[] getStatus() {
        return status;
    }
}
