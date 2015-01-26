package org.sigmah.shared.command;

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
