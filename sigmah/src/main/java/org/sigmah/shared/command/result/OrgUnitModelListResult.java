package org.sigmah.shared.command.result;

import java.util.List;

import org.sigmah.shared.dto.OrgUnitModelDTO;

/**
 * List of org unit models.
 * 
 * @author nrebiai
 * 
 */
public class OrgUnitModelListResult implements CommandResult {

    private static final long serialVersionUID = 7244042578208218094L;

    private List<OrgUnitModelDTO> list;

    public OrgUnitModelListResult() {
    }

    public OrgUnitModelListResult(List<OrgUnitModelDTO> list) {
        this.list = list;
    }

    public List<OrgUnitModelDTO> getList() {
        return list;
    }

    public void setList(List<OrgUnitModelDTO> list) {
        this.list = list;
    }
}