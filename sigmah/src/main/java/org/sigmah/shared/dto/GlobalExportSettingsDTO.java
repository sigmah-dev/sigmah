/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */
package org.sigmah.shared.dto;

import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

/*
 * @author sherzod
 */
public class GlobalExportSettingsDTO extends BaseModelData implements EntityDTO{

	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
		return id != null ? id : -1;
	}

	public void setId(int id) {
		set("id", id);
	}
	
	public ExportUtils.ExportFormat getExportFormat() {
		return get("exportFormat");
	}

	public void setExportFormat(
			ExportUtils.ExportFormat exportFormat) {
		set("exportFormat", exportFormat);
	} 
	
	
	public ExportUtils.ExportFormat getDefaultOrganizationExportFormat() {
		return get("defaultOrganizationExportFormat");
	}

	public void setDefaultOrganizationExportFormat(
			ExportUtils.ExportFormat defaultOrganizationExportFormat) {
		set("defaultOrganizationExportFormat", defaultOrganizationExportFormat);
	}
	
	public Integer getAutoExportFrequency() {
 		return get("autoExportFrequency");
	}

	public void setAutoExportFrequency(Integer autoExportFrequency) {
 		set("autoExportFrequency", autoExportFrequency);
	}

 	public Integer getAutoDeleteFrequency() {
 		return get("autoDeleteFrequency");
	}

	public void setAutoDeleteFrequency(Integer autoDeleteFrequency) {
 		set("autoDeleteFrequency", autoDeleteFrequency);
	}
	
	//project model list
	  public List<ProjectModelDTO> getProjectModelsDTO() {
	        return get("projectModelsDTO");
	    }

	    public void setProjectModelsDTO(List<ProjectModelDTO> models) {
	        set("projectModelsDTO", models);
	    }

	@Override
	public String getEntityName() {
		return "GlobalExportSettings";
	}

}
