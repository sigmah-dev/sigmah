package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ProjectDTOLightListResult;
import org.sigmah.shared.domain.ProjectModelStatus;

public class GetTestProjects implements Command<ProjectDTOLightListResult> {

    private static final long serialVersionUID = 6533084223987010888L;

	/**
	 * The status of the project set to <code>DRAFT</code> for the test
	 * projects.
	 */
    private ProjectModelStatus projectModelStatus;

    public GetTestProjects() {
        // serialization.
    }
    
    public GetTestProjects(ProjectModelStatus projectModelStatus) {
        this.projectModelStatus = projectModelStatus;
    }
    
    public ProjectModelStatus getProjectModelStatus() {
		return projectModelStatus;
	}

	public void setProjectModelStatus(ProjectModelStatus projectModelStatus) {
		this.projectModelStatus = projectModelStatus;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((projectModelStatus == null) ? 0 : projectModelStatus.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GetTestProjects other = (GetTestProjects) obj;
        if (projectModelStatus != other.projectModelStatus)
            return false;
        return true;
    }
}
