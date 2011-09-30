package org.sigmah.shared.command;

import org.sigmah.shared.command.result.ProjectModelStatusListResult;
import org.sigmah.shared.domain.ProjectModelStatus;

public class GetAvailableStatusForModel implements Command<ProjectModelStatusListResult> {

    private static final long serialVersionUID = -6721943249220917751L;

    private CheckModelUsage.ModelType modelType;

    private Long ProjectModelId;

    private Integer orgUnitModelId;

    private ProjectModelStatus status;

    public GetAvailableStatusForModel() {
        // serialization.
    }

    public GetAvailableStatusForModel(ProjectModelStatus status) {
        this.status = status;
    }

    public ProjectModelStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectModelStatus status) {
        this.status = status;
    }

    public void setModelType(CheckModelUsage.ModelType modelType) {
        this.modelType = modelType;
    }

    public CheckModelUsage.ModelType getModelType() {
        return modelType;
    }

    public void setProjectModelId(Long projectModelId) {
        ProjectModelId = projectModelId;
    }

    public Long getProjectModelId() {
        return ProjectModelId;
    }

    public void setOrgUnitModelId(Integer orgUnitModelId) {
        this.orgUnitModelId = orgUnitModelId;
    }

    public Integer getOrgUnitModelId() {
        return orgUnitModelId;
    }

}
