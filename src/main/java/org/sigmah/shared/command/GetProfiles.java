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
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.profile.ProfileDTO;

/**
 * GetProfiles command.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProfiles extends AbstractCommand<ListResult<ProfileDTO>> {

	/**
	 * The mapping mode. Set to {@code null} to ignore.
	 */
	private ProfileDTO.Mode mappingMode;

	protected GetProfiles() {
		// Serialization.
	}

	/**
	 * The type of model of the models for the current user organization (set to <code>null</code> to ignore this filter).
	 * 
	 * @param mappingMode
	 *          The mapping mode, or {@code null} to ignore and process a full mapping.
	 */
	public GetProfiles(final ProfileDTO.Mode mappingMode) {
		this.mappingMode = mappingMode;
	}

	public ProfileDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
