/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.shared.dto.report;

import java.util.List;
import org.sigmah.shared.dto.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;

/**
 *
 * @author nrebiai
 */
public class ProjectReportModelSectionDTO extends BaseModelData implements EntityDTO {

	private static final long serialVersionUID = 3100003531351081230L;

	@Override
    public String getEntityName() {
        return "report.ProjectReportModelSection";
    }
    
	@Override
    public int getId() {
		if(get("id") != null){
			final Integer id = (Integer) get("id");
			return id != null ? id : -1;
		}
    	return -1;        
    }
    public void setId(Integer id) {
        this.set("id", id);
    }

    public Integer getParentSectionModelId() {
        return (Integer) get("parentSectionModelId");
    }

    public void setParentSectionModelId(Integer parentSectionModelId) {
        this.set("parentSectionModelId", parentSectionModelId);
    }
    
    public String getParentSectionModelName() {
        return (String) get("parentSectionModelName");
    }

    public void setParentSectionModelName(String parentSectionModelName) {
        this.set("parentSectionModelName", parentSectionModelName);
    }

    public Integer getProjectModelId() {
        return (Integer) get("projectModelId");
    }

    public void setProjectModelId(Integer projectModelId) {
        this.set("projectModelId", projectModelId);
    }
    
    public String getReportModelName() {
        return (String) get("reportModelName");
    }

    public void setReportModelName(String name) {
        this.set("reportModelName", name);
    }

    public String getName() {
        return (String) get("name");
    }

    public void setName(String name) {
        this.set("name", name);
    }

    public Integer getIndex() {
        return (Integer) get("index");
    }

    public void setIndex(Integer index) {
        this.set("index", index);
    }
    
    public Integer getRow() {
        return (Integer) get("row");
    }

    public void setRow(Integer row) {
        this.set("row", row);
    }

    public Integer getNumberOfTextarea() {
        return (Integer) get("numberOfTextarea");
    }

    public void setNumberOfTextarea(Integer numberOfTextarea) {
        this.set("numberOfTextarea", numberOfTextarea);
    }

    public List<ProjectReportModelSectionDTO> getSubSectionsDTO() {
        return (List<ProjectReportModelSectionDTO>) get("subSections");
    }

    public void setSubSectionsDTO(List<ProjectReportModelSectionDTO> subSections) {
        this.set("subSections", subSections);
    }
}
