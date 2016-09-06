package org.sigmah.shared.command;

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

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectFundingDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Retrieves the value of a flexible element for every projects linked to a given project id.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class GetValueFromLinkedProjects extends AbstractCommand<ListResult<String>> {
	
	/**
	 * The current project id which retrieved linked projects are related to.
	 */
	private Integer projectId;

	/**
	 * The type of linked projects to retrieve.
	 */
	private ProjectFundingDTO.LinkedProjectType type;
	
	/**
	 * The identifier of the flexible element to get the value from.
	 */
	private Integer elementId;

	/**
	 * Entity name of the flexible element.
	 * 
	 * Do not use the getClass().getName() on client side to identify a flexible element type.
	 * Always use the getEntityName() !
	 */
	private String elementEntityName;

	/**
	 * Creates an empty instance of this command.
	 * The empty constructor is required to serialize this object.
	 */
	public GetValueFromLinkedProjects() {
		// Empty constructor.
	}

	/**
	 * Creates a new instance of this command.
	 * 
	 * @param projectId
	 *			Identifier of the project.
	 * @param type
	 *			Relation to linked projects.
	 * @param element 
	 *			Flexible element to query.
	 */
	public GetValueFromLinkedProjects(final Integer projectId, final ProjectFundingDTO.LinkedProjectType type, final FlexibleElementDTO element) {
		this.projectId = projectId;
		this.type = type;
		this.elementId = element.getId();
		this.elementEntityName = element.getEntityName();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("projectId", projectId);
		builder.append("type", type);
		builder.append("elementId", elementId);
		builder.append("elementEntityName", elementEntityName);
	}

	public Integer getProjectId() {
		return projectId;
	}

	public ProjectFundingDTO.LinkedProjectType getType() {
		return type;
	}

	public Integer getElementId() {
		return elementId;
	}

	public String getElementEntityName() {
		return elementEntityName;
	}
	
}
