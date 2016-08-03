package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class Scope {
	
	private ProjectFundingDTO.LinkedProjectType linkedProjectType;
	private String modelName;

	public Scope() {
	}

	public Scope(ProjectFundingDTO.LinkedProjectType linkedProjectType, String modelName) {
		this.linkedProjectType = linkedProjectType;
		this.modelName = modelName;
	}

	public ProjectFundingDTO.LinkedProjectType getLinkedProjectType() {
		return linkedProjectType;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
}