package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ImportationSchemeModelListResult;

public class GetImportationSchemeModels implements Command< ImportationSchemeModelListResult>{

	/**
	 * 
	 */
    private static final long serialVersionUID = 2094678616370006095L;
    
    private Long importationSchemeId;
    private Long projectModelId;
    private Long orgUnitModelId;
    
    
    public GetImportationSchemeModels() {
    	
    }


	/**
	 * @return the schemaId
	 */
	public Long getImportationSchemeId() {
		return importationSchemeId;
	}


	/**
	 * @param schemaId the schemaId to set
	 */
	public void setImportationSchemeId(Long importationSchemeId) {
		this.importationSchemeId = importationSchemeId;
	}


	/**
	 * @return the projectModelId
	 */
	public Long getProjectModelId() {
		return projectModelId;
	}


	/**
	 * @param projectModelId the projectModelId to set
	 */
	public void setProjectModelId(Long projectModelId) {
		this.projectModelId = projectModelId;
	}


	public Long getOrgUnitModelId() {
	    return orgUnitModelId;
    }


	public void setOrgUnitModelId(Long orgUnitModelId) {
	    this.orgUnitModelId = orgUnitModelId;
    }


}
