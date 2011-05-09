package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ProjectModelListResult;
import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.domain.ProjectModelType;

/**
 * Retrieves the list of project models available to the user.
 * 
 * @author tmi
 * 
 */
public class GetProjectModels implements Command<ProjectModelListResult> {

    private static final long serialVersionUID = 6533084223987010888L;

    /**
     * The type of model of the models for the current user organization (set to
     * <code>null</code> to ignore this filter).
     */
    private ProjectModelType modelType;
    
	/**
	 * The status of the project set to <code>DRAFT</code> for the test
	 * projects.
	 */
    private ProjectModelStatus projectModelStatus;
    
    private Boolean allProjectModelStatus = false;

    public GetProjectModels() {
        // serialization.
    }
    
    public GetProjectModels(ProjectModelStatus projectModelStatus) {
    	//Test projects
        this.projectModelStatus = projectModelStatus;
    }

    public GetProjectModels(ProjectModelType modelType) {
        this.modelType = modelType;
    }

    public ProjectModelType getModelType() {
        return modelType;
    }

    public void setModelType(ProjectModelType modelType) {
        this.modelType = modelType;
    }

    public ProjectModelStatus getProjectModelStatus() {
		return projectModelStatus;
	}

	public void setProjectModelStatus(ProjectModelStatus projectModelStatus) {
		this.projectModelStatus = projectModelStatus;
	}
	
	public Boolean getAllProjectModelStatus() {
		return allProjectModelStatus;
	}

	public void allProjectModelStatus() {
		this.allProjectModelStatus = true;
	}

	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modelType == null) ? 0 : modelType.hashCode());
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
        GetProjectModels other = (GetProjectModels) obj;
        if (modelType != other.modelType)
            return false;
        return true;
    }
}
