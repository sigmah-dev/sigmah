package org.sigmah.shared.command;

import org.sigmah.shared.dto.OrgUnitModelDTO;

public class GetOrgUnitModel implements Command<OrgUnitModelDTO> {

	private static final long serialVersionUID = 5356854477192668731L;
	
	private int id;

	public GetOrgUnitModel() {
        // serialization.
    }
	
	public GetOrgUnitModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
