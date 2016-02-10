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

import java.util.Collection;
import java.util.List;

import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.DeleteResult;
import org.sigmah.shared.dto.profile.PrivacyGroupDTO;

/**
 * <p>
 * Command to delete privacy group(s).
 * </p>
 * <p>
 * If the returned map contains values, it means that errors have been detected and some privacy groups have not been
 * deleted.<br>
 * Otherwise, no error was detected and all the privacy groups have been deleted.
 * </p>
 * 
 * @author gjb (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class DeletePrivacyGroups extends AbstractCommand<DeleteResult<PrivacyGroupDTO>> {

	private List<Integer> privacyGroupIds;

	protected DeletePrivacyGroups() {
		// Serialization.
	}

	/**
	 * Delete the given {@code privacyGroups}.
	 * 
	 * @param privacyGroups
	 *          The privacy group(s).
	 */
	public DeletePrivacyGroups(final Collection<PrivacyGroupDTO> privacyGroups) {
		this(ClientUtils.getEntityIds(privacyGroups));
	}

	/**
	 * Delete the given {@code privacyGroupIds}.
	 * 
	 * @param privacyGroupIds
	 *          The privacy group id(s).
	 */
	public DeletePrivacyGroups(final List<Integer> privacyGroupIds) {
		this.privacyGroupIds = privacyGroupIds;
	}

	/**
	 * @return The privacy group id(s) list.
	 */
	public List<Integer> getPrivacyGroupIds() {
		return privacyGroupIds;
	}

}
