package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.ProjectModelDTOLight;

/**
 * List of project models.
 * 
 * @author tmi
 * 
 */
public class ProjectModelListResult implements CommandResult {

    private static final long serialVersionUID = 7244042578208218094L;

    private List<ProjectModelDTOLight> list;
    
    private List<ProjectModelDTO>fullVersionModelList;

    public ProjectModelListResult() {
    }

    public ProjectModelListResult(List<ProjectModelDTOLight> list) {
        this.list = list;
    }
  

	/**
	 * @param list
	 * @param fullVersionModelList
	 */
	public ProjectModelListResult(List<ProjectModelDTOLight> list,
			List<ProjectModelDTO> fullVersionModelList) {
		super();
		this.list = list;
		this.fullVersionModelList = fullVersionModelList;
	}

	/**
	 * @return the fullVersionModelList
	 */
	public List<ProjectModelDTO> getFullVersionModelList() {
		return fullVersionModelList;
	}

	/**
	 * @param fullVersionModelList the fullVersionModelList to set
	 */
	public void setFullVersionModelList(List<ProjectModelDTO> fullVersionModelList) {
		this.fullVersionModelList = fullVersionModelList;
	}

	public List<ProjectModelDTOLight> getList() {
        return list;
    }

    public void setList(List<ProjectModelDTOLight> list) {
        this.list = list;
    }
}