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
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetProfilesWithDetails extends AbstractCommand<ListResult<ProfileDTO>> {

	/**
	 * Profile identifier
	 */
	private Integer profileId;

	/**
	 * The optional mapping mode.
	 */
	private ProfileDTO.Mode mappingMode;

	protected GetProfilesWithDetails() {
		// Serialization.
	}

	/**
	 * Retrieves the authenticated user's organization profile<u>s</u>.<br>
	 * The command result may contain several profiles.
	 * 
	 * @param mappingMode
	 *          The optional mapping mode. Set to {@code null} to process a default mapping.
	 */
	public GetProfilesWithDetails(ProfileDTO.Mode mappingMode) {
		this(null, mappingMode);
	}

	/**
	 * Retrieves the authenticated user's organization profile corresponding to the given {@code profileId}.<br>
	 * The command result only contains one profile.
	 * 
	 * @param profileId
	 *          The profile id.
	 * @param mappingMode
	 *          The optional mapping mode. Set to {@code null} to process a default mapping.
	 */
	public GetProfilesWithDetails(Integer profileId, ProfileDTO.Mode mappingMode) {
		this.profileId = profileId;
	}

	public Integer getProfileId() {
		return profileId;
	}

	public ProfileDTO.Mode getMappingMode() {
		return mappingMode;
	}

}
