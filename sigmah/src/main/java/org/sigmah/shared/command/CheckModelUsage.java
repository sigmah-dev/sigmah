/**
 * 
 */
package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ModelCheckResult;

/**
 * 
 * Command to check if a project model or orgunit model has been ever used.
 * 
 * @author HUZHE (zhe.hu32@gmail.com)
 *
 */





public class CheckModelUsage implements Command<ModelCheckResult>{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static enum ModelType {
		
		ProjectModel,OrgUnitModel
	}

	private Long ProjectModelId;
	
	private Integer orgUnitModelId;
	
	private CheckModelUsage.ModelType modelType;
	
	public CheckModelUsage()
	{
		
	}

	public Long getProjectModelId() {
		return ProjectModelId;
	}

	public void setProjectModelId(Long projectModelId) {
		ProjectModelId = projectModelId;
	}

	public Integer getOrgUnitModelId() {
		return orgUnitModelId;
	}

	public void setOrgUnitModelId(Integer orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

	public CheckModelUsage.ModelType getModelType() {
		return modelType;
	}

	public void setModelType(CheckModelUsage.ModelType modelType) {
		this.modelType = modelType;
	}
	
	

}
