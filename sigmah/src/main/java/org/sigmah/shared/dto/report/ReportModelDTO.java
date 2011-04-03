package org.sigmah.shared.dto.report;

import java.util.List;

import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 * DTO mapping class for entity report.ProjectReportModel
 * 
 * @author nrebiai
 * 
 */
public class ReportModelDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = 3300196624126690838L;

	@Override
    public String getEntityName() {
        return "report.ProjectReportModel";
    }

	@Override
	public int getId() {
		final Integer id = (Integer) get("id");
        return (id != null) ? id : -1;
    }
    public void setId(Integer id) {
        this.set("id", id);
    }

    public String getName() {
        return get("name");
    }
    public void setName(String name) {
        this.set("name", name);
    }
    
    public List<ProjectReportModelSectionDTO> getSectionsDTO() {
        return get("sections");
    }

    public void setSectionsDTO(List<ProjectReportModelSectionDTO> sections) {
        this.set("sections", sections);
    }
}
