package org.sigmah.shared.dto;

import java.util.Map;

import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.domain.export.GlobalExportFormat;

public class UpdateGlobalExportSettings implements Command<VoidResult>{

	private int organizationId;
	private Map<Integer,Boolean> fieldsMap;
	private GlobalExportFormat exportFormat ; 
 	private Integer autoExportFrequency;  
	private Integer autoDeleteFrequency;  
	
	public UpdateGlobalExportSettings(){}
	
	public UpdateGlobalExportSettings(Map<Integer,Boolean> fieldsMap){
		this.fieldsMap=fieldsMap;
	}

	public Map<Integer, Boolean> getFieldsMap() {
		return fieldsMap;
	}

	public void setFieldsMap(Map<Integer, Boolean> fieldsMap) {
		this.fieldsMap = fieldsMap;
	}

	public GlobalExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(GlobalExportFormat exportFormat) {
		this.exportFormat = exportFormat;
	}

	public Integer getAutoExportFrequency() {
		return autoExportFrequency;
	}

	public void setAutoExportFrequency(Integer autoExportFrequency) {
		this.autoExportFrequency = autoExportFrequency;
	}

	public Integer getAutoDeleteFrequency() {
		return autoDeleteFrequency;
	}

	public void setAutoDeleteFrequency(Integer autoDeleteFrequency) {
		this.autoDeleteFrequency = autoDeleteFrequency;
	}

	public int getOrganizationId() {
		return organizationId;
	}

	public void setOrganizationId(int organizationId) {
		this.organizationId = organizationId;
	}
	
	
	
}


