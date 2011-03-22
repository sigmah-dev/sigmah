package org.sigmah.shared.dto.report;

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

	public int getId() {
        return (Integer) get("id");
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
}
