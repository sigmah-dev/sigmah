package org.sigmah.offline.js;

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
