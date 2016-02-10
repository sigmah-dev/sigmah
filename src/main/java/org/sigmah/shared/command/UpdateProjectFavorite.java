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
