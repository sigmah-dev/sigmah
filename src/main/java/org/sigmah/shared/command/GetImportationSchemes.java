package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ImportationSchemeListResult;

public class GetImportationSchemes implements Command<ImportationSchemeListResult>{

	/**
	 * 
	 */
    private static final long serialVersionUID = 6141873433871091010L;
    
    private Long schemaId;
    
    private Long projectModelId;
    
    private Long orgUnitModelId;
    
    private Boolean excludeExistent = false;
    

    public GetImportationSchemes() {
    	//required
    }

	public Long getSchemaId() {
	    return schemaId;
    }

	public void setSchemaId(Long schemaId) {
	    this.schemaId = schemaId;
    }

	/**
	 * @return the excludeExistent
	 */
	public Boolean getExcludeExistent() {
		return excludeExistent;
	}

	/**
	 * @param excludeExistent the excludeExistent to set
	 */
	public void setExcludeExistent(Boolean excludeExistent) {
		this.excludeExistent = excludeExistent;
	}

	/**
	 * @return the modelId
	 */
	public Long getProjectModelId() {
		return projectModelId;
	}

	/**
	 * @param modelId the modelId to set
	 */
	public void setProjectModelId(Long modelId) {
		this.projectModelId = modelId;
	}

	/**
	 * @return the orgUnitModelId
	 */
	public Long getOrgUnitModelId() {
		return orgUnitModelId;
	}

	/**
	 * @param orgUnitModelId the orgUnitModelId to set
	 */
	public void setOrgUnitModelId(Long orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}
    
    
}
