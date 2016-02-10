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
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

/**
 * Retrieves the list of org unit models available to the user.
 * 
 * @author nrebiai (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class GetOrgUnitModels extends AbstractCommand<ListResult<OrgUnitModelDTO>> {

	/**
	 * The mapping mode (may be {@code null}).
	 */
	private OrgUnitModelDTO.Mode mappingMode;

	/**
	 * The filtered status (may be {@code null}).<br>
	 * If {@code null}, a default filter is set.
	 */
	private ProjectModelStatus[] statusFilters;

	protected GetOrgUnitModels() {
		// Serialization.
	}

	/**
	 * <p>
	 * Retrieves the {@link OrgUnitModelDTO} of the authenticated user's organization.
	 * </p>
	 * <p>
	 * If no {@code statusFilters} is set, a default filter will only retrieve OrgUnit models with status
	 * {@link ProjectModelStatus#READY} or {@link ProjectModelStatus#USED}.
	 * </p>
	 * 
	 * @param mappingMode
	 *          The mapping mode. If {@code null}, default mapping is processed.
	 * @param statusFilters
	 *          Only retrieves OrgUnit models which status is included into the given {@code statusFilters}. If
	 *          {@code null} or empty, default filter is set.
	 */
	public GetOrgUnitModels(OrgUnitModelDTO.Mode mappingMode, ProjectModelStatus... statusFilters) {
		this.mappingMode = mappingMode;
		this.statusFilters = statusFilters;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append("mappingMode", mappingMode);
		builder.append("statusFilters", statusFilters);
	}

	public OrgUnitModelDTO.Mode getMappingMode() {
		return mappingMode;
	}

	public ProjectModelStatus[] getStatusFilters() {
		return statusFilters;
	}

}
