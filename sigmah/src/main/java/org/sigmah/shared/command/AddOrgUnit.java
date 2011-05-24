package org.sigmah.shared.command;

import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.OrgUnitDTOLight;

public class AddOrgUnit implements Command<CreateResult> {

    private static final long serialVersionUID = 1L;

    private int parentId;
    private int modelId;
    private String calendarName;
    private OrgUnitDTOLight unit;

    public AddOrgUnit() {
        // serialization.
    }

    public AddOrgUnit(int parentId, int modelId, String calendarName, OrgUnitDTOLight unit) {
        super();
        this.parentId = parentId;
        this.modelId = modelId;
        this.calendarName = calendarName;
        this.unit = unit;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public OrgUnitDTOLight getUnit() {
        return unit;
    }

    public void setUnit(OrgUnitDTOLight unit) {
        this.unit = unit;
    }

    public void setModelId(int modelId) {
        this.modelId = modelId;
    }

    public int getModelId() {
        return modelId;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public String getCalendarName() {
        return calendarName;
    }
}
