package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.ProjectDTOLight;

public class ProjectDTOLightListResult implements CommandResult {

    private static final long serialVersionUID = 7244042578208218094L;

    private List<ProjectDTOLight> list;

    public ProjectDTOLightListResult() {
    }

    public ProjectDTOLightListResult(List<ProjectDTOLight> list) {
        this.list = list;
    }

    public List<ProjectDTOLight> getList() {
        return list;
    }

    public void setList(List<ProjectDTOLight> list) {
        this.list = list;
    }
}
