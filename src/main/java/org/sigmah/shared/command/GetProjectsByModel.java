/**
 * 
 */
package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ProjectListResult;

/**
 * 
 * Get all draft projects created from a specified project model
 * 
 * @author HUZHE
 *
 */
public class GetProjectsByModel implements Command<ProjectListResult>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2714073618142882511L;
	
	
	private Long projectModelId;
	private Boolean asProjectDTOs;

	
	/**
	 * @return the projectId
	 */
	public Long getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param projectId the projectId to set
	 */
	public void setProjectModelId(Long projectModelId) {
		this.projectModelId = projectModelId;
	}

	public GetProjectsByModel() {
		
	}

	/**
	 * @param projectId
	 */
	public GetProjectsByModel(Long projectModelId) {
		super();
		this.projectModelId = projectModelId;
	}

	/**
	 * @return the asProjectDTOs
	 */
    public Boolean getAsProjectDTOs() {
	    return asProjectDTOs;
    }

	/**
	 * @param asProjectDTOs the asProjectDTOs to set
	 */
    public void setAsProjectDTOs(Boolean asProjectDTOs) {
	    this.asProjectDTOs = asProjectDTOs;
    }
	
	
	

}
