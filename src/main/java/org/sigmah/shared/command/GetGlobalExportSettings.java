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

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.dto.GlobalExportSettingsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

/**
 * See {@link #GetGlobalExportSettings(Integer, boolean)} for JavaDoc.
 * 
 * @author sherzod
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetGlobalExportSettings extends AbstractCommand<GlobalExportSettingsDTO> {

	private Integer organizationId;
	private boolean retrieveProjectModels;

	public GetGlobalExportSettings() {
		// Serialization.
	}

	/**
	 * Retrieves the {@code organizationId} corresponding {@link GlobalExportSettingsDTO} configuration.
	 * 
	 * @param organizationId
	 *          The Organization id.
	 * @param retrieveProjectModels
	 *          {@code true} to also retrieve {@code organizationId} corresponding {@link ProjectModelDTO} list (can be
	 *          greedy), {@code false} to only retrieve {@link GlobalExportSettingsDTO} configuration.
	 */
	public GetGlobalExportSettings(final Integer organizationId, final boolean retrieveProjectModels) {
		this.organizationId = organizationId;
		this.retrieveProjectModels = retrieveProjectModels;
	}

	public Integer getOrganizationId() {
		return organizationId;
	}

	public boolean isRetrieveProjectModels() {
		return retrieveProjectModels;
	}

}
