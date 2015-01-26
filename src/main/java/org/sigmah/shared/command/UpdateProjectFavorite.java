package org.sigmah.shared.command;

import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.CreateResult;

/**
 * Command to update the list of favorite users of a projet
 * 
 * @author HUZHE
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UpdateProjectFavorite extends AbstractCommand<CreateResult> {

	public static enum UpdateType {

		// Remove current user from the favorite user list of a project
		REMOVE,

		// Add current user into the favorite user list of a project
		ADD
	}

	private int projectId;
	private UpdateType updateType;

	/**
	 * Serialization.
	 */
	public UpdateProjectFavorite() {
		// Serialization.
	}

	public UpdateProjectFavorite(int projectId, UpdateType updateType) {
		super();
		this.projectId = projectId;
		this.updateType = updateType;
	}

	/**
	 * @return the updateType
	 */
	public UpdateType getUpdateType() {
		return updateType;
	}

	/**
	 * @param updateType
	 *          the updateType to set
	 */
	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	/**
	 * @return the projectId
	 */
	public int getProjectId() {
		return projectId;
	}

	/**
	 * @param projectId
	 *          the projectId to set
	 */
	public void setProjectId(int projectId) {
		this.projectId = projectId;
	}

}
