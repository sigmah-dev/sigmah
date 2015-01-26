package org.sigmah.offline.js;

import org.sigmah.shared.command.UpdateLogFrame;
import org.sigmah.shared.dto.logframe.LogFrameDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateLogFrameJS extends CommandJS {
	
	protected UpdateLogFrameJS() {
	}
	
	public static UpdateLogFrameJS toJavaScript(UpdateLogFrame updateLogFrame) {
		final UpdateLogFrameJS updateLogFrameJS = Values.createJavaScriptObject(UpdateLogFrameJS.class);
		
		updateLogFrameJS.setProjectId(updateLogFrame.getProjectId());
		updateLogFrameJS.setLogFrame(updateLogFrame.getLogFrame());
		
		return updateLogFrameJS;
	}
	
	public UpdateLogFrame toUpdateLogFrame() {
		return new UpdateLogFrame(getLogFrameDTO(), getProjectId());
	}
	
	public Integer getProjectId() {
		return Values.getInteger(this, "projectId");
	}
	
	public void setProjectId(Integer projectId) {
		Values.setInteger(this, "projectId", projectId);
	}
	
	public native LogFrameJS getLogFrame() /*-{
		return this.logFrame;
	}-*/;
	
	public native void setLogFrame(LogFrameJS logFrame) /*-{
		this.logFrame = logFrame;
	}-*/;
	
	public LogFrameDTO getLogFrameDTO() {
		if(getLogFrame() != null) {
			return getLogFrame().toDTO();
		}
		return null;
	}
	
	public void setLogFrame(LogFrameDTO logFrameDTO) {
		if(logFrameDTO != null) {
			setLogFrame(LogFrameJS.toJavaScript(logFrameDTO));
		}
	}
}
