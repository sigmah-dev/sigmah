package org.sigmah.shared.dto;

import java.util.List;

import org.sigmah.shared.domain.export.GlobalExportFormat;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class GlobalExportSettingsDTO extends BaseModelData implements EntityDTO{

	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
		return id != null ? id : -1;
	}

	public void setId(int id) {
		set("id", id);
	}
	
	public GlobalExportFormat getExportFormat() {
		return get("exportFormat");
	}

	public void setExportFormat(
			GlobalExportFormat exportFormat) {
		set("exportFormat", exportFormat);
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
