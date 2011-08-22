/**
 * 
 */
package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.command.result.ProjectListResult;
import org.sigmah.shared.dto.ProjectDTO;

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
	
	
	

}
