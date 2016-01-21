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

import java.util.List;

import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.ProjectDTO;

/**
 * Retrieves the given id(s) corresponding project(s) list.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProjectsFromId extends AbstractCommand<ListResult<ProjectDTO>> {

	/**
	 * List of the projects ids.
	 */
	private List<Integer> ids;

	/**
	 * The mapping mode specifying the scope of data to retrieve.
	 */
	private ProjectDTO.Mode mappingMode;

	protected GetProjectsFromId() {
		// Serialization.
	}

	public GetProjectsFromId(List<Integer> ids, ProjectDTO.Mode mappingMode) {
		this.ids = ids;
		this.mappingMode = mappingMode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("ids", ids);
		builder.append("mappingMode", mappingMode);
	}

	public List<Integer> getIds() {
		return ids;
	}

	public ProjectDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
