/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.dto;

import java.util.Map;

import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.result.VoidResult;

/*
 * @author sherzod
 */
public class UpdateGlobalExportSettings implements Command<VoidResult>{

	private int organizationId;
	private Map<Integer,Boolean> fieldsMap;
	private ExportUtils.ExportFormat exportFormat ; 
	private ExportUtils.ExportFormat defaultOrganizationExportFormat ;
 	private Integer autoExportFrequency;  
	private Integer autoDeleteFrequency;  
	private boolean updateDefaultExportFormat;
	
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

	public ExportUtils.ExportFormat getExportFormat() {
		return exportFormat;
	}

	public void setExportFormat(ExportUtils.ExportFormat exportFormat) {
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

	public boolean getUpdateDefaultExportFormat() {
		return updateDefaultExportFormat;
	}

	public void setUpdateDefaultExportFormat(boolean updateDefaultExportFormat) {
		this.updateDefaultExportFormat = updateDefaultExportFormat;
	}

	public ExportUtils.ExportFormat getDefaultOrganizationExportFormat() {
		return defaultOrganizationExportFormat;
	}

	public void setDefaultOrganizationExportFormat(
			ExportUtils.ExportFormat defaultOrganizationExportFormat) {
		this.defaultOrganizationExportFormat = defaultOrganizationExportFormat;
	}
	
	
	
}


