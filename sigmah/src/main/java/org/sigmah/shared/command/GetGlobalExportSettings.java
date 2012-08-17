package org.sigmah.shared.command;

import org.sigmah.shared.dto.GlobalExportSettingsDTO;

public class GetGlobalExportSettings implements Command<GlobalExportSettingsDTO>  {
		
	private int organizationId;
	
	public GetGlobalExportSettings(){}
	
	public GetGlobalExportSettings(int organizationId){
		this.organizationId=organizationId;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	
	

}
