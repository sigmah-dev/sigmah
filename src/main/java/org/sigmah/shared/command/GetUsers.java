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

import org.sigmah.shared.command.result.UserResult;

/**
 * <p>
 * Queries the list of users authorized to access a given {@link org.sigmah.server.domain.UserDatabase}.
 * </p>
 * <p>
 * The resulting {@link org.sigmah.shared.dto.UserPermissionDTO} are a projection of the
 * {@link org.sigmah.server.domain.User}, {@link org.sigmah.server.domain.UserPermission}, and
 * {@link org.sigmah.server.domain.OrgUnit} entities.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Deprecated
public class GetUsers extends PagingGetCommand<UserResult> {

	private int databaseId;

	protected GetUsers() {
		// Serialization.
	}

	public GetUsers(int databaseId) {
		super();
		this.databaseId = databaseId;
	}

	/**
	 * Gets the id of the database for which to query the list of UserPermissions.
	 *
	 * @return the id of the database for which to query the list of authorized users.
	 */
	public int getDatabaseId() {
		return databaseId;
	}

	/**
	 * Sets the id of the database for which toquery the list of UserPermissions.
	 * 
	 * @param databaseId
	 */
	public void setDatabaseId(int databaseId) {
		this.databaseId = databaseId;
	}
}
