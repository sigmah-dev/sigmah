package org.sigmah.shared.command;

import org.sigmah.shared.dto.ProjectModelDTOLight;

/**
 * Retrieves a project model by ID.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr)
 */
public class GetProjectModelCopy implements Command<ProjectModelDTOLight>{
	
	private static final long serialVersionUID = 7408913560700433111L;
	
	private long projectModelId;
	private String newModelName;
	
	public GetProjectModelCopy(long projectModelId){
		this.projectModelId = projectModelId;
	}
	
	public GetProjectModelCopy(){
		// serialization.
	}

	public long getProjectModelId() {
		return projectModelId;
	}

	public void setProjectModelId(long projectModelId) {
		this.projectModelId = projectModelId;
	}

	/**
	 * @return the newModelName
	 */
	public String getNewModelName() {
		return newModelName;
	}

	/**
	 * @param newModelName the newModelName to set
	 */
	public void setNewModelName(String newModelName) {
		this.newModelName = newModelName;
	}
	
	
	
}
