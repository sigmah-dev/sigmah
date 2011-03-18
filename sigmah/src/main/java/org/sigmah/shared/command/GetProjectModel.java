package org.sigmah.shared.command;

import org.sigmah.shared.dto.ProjectModelDTO;

public class GetProjectModel implements Command<ProjectModelDTO> {

	private static final long serialVersionUID = 5341195938784834326L;
	
	private int id;

	public GetProjectModel() {
        // serialization.
    }
	
	public GetProjectModel(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
