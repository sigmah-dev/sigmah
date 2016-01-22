package org.sigmah.offline.js;

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

import org.sigmah.shared.command.UpdateProjectFavorite;

/**
 * JavaScript version of the {@link UpdateProjectFavorite} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateProjectFavoriteJS extends CommandJS {

	protected UpdateProjectFavoriteJS() {
	}
	
	public static UpdateProjectFavoriteJS toJavaScript(UpdateProjectFavorite updateProjectFavorite) {
		final UpdateProjectFavoriteJS updateProjectFavoriteJS = Values.createJavaScriptObject(UpdateProjectFavoriteJS.class);
		
		updateProjectFavoriteJS.setProjectId(updateProjectFavorite.getProjectId());
		updateProjectFavoriteJS.setUpdateType(updateProjectFavorite.getUpdateType());
		
		return updateProjectFavoriteJS;
	}
	
	public UpdateProjectFavorite toUpdateProjectFavorite() {
		return new UpdateProjectFavorite(getProjectId(), getUpdateType());
	}
	
	public native int getProjectId() /*-{
		return this.projectId;
	}-*/;

	public native void setProjectId(int projectId) /*-{
		this.projectId = projectId;
	}-*/;

	public UpdateProjectFavorite.UpdateType getUpdateType() {
		return Values.getEnum(this, "updateType", UpdateProjectFavorite.UpdateType.class);
	}

	public void setUpdateType(UpdateProjectFavorite.UpdateType updateType) {
		Values.setEnum(this, "updateType", updateType);
	}
	
}
