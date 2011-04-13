package org.sigmah.shared.command;

import org.sigmah.shared.dto.OrgUnitModelDTO;

/**
 * Retrieves a organizational unit model by ID.
 * 
 * @author Kristela Macaj (kmacaj@ideia.fr)
 */
public class GetOrgUnitModelCopy implements Command<OrgUnitModelDTO>{
	
	private static final long serialVersionUID = 3848729293364156448L;
	
	private long orgUnitModelId;
	
	public GetOrgUnitModelCopy(long orgUnitModelId){
		this.orgUnitModelId = orgUnitModelId;
	}
	
	public GetOrgUnitModelCopy(){
		// serialization.
	}

	public long getOrgUnitModelId() {
		return orgUnitModelId;
	}

	public void setOrgUnitModelId(long orgUnitModelId) {
		this.orgUnitModelId = orgUnitModelId;
	}

}
