package org.sigmah.shared.computation.dependency;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.sigmah.shared.dto.ProjectFundingDTO;

/**
 * Scope of a dependency.
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
	
	public String getLinkedProjectTypeName() {
		if (linkedProjectType != null) switch (linkedProjectType) {
			case FUNDED_PROJECT:
				return "fundedProjects";
			case FUNDING_PROJECT:
				return "fundingSources";
		}
		return null;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
}
