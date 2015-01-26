package org.sigmah.offline.js;

import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.referential.LogFrameGroupType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LogFrameGroupJS extends JavaScriptObject {
	
	protected LogFrameGroupJS() {
	}
	
	public static LogFrameGroupJS toJavaScript(LogFrameGroupDTO logFrameGroupDTO) {
		final LogFrameGroupJS logFrameGroupJS = Values.createJavaScriptObject(LogFrameGroupJS.class);
		
		logFrameGroupJS.setId(logFrameGroupDTO.getId());
		logFrameGroupJS.setLabel(logFrameGroupDTO.getLabel());
		logFrameGroupJS.setType(logFrameGroupDTO.getType());
		logFrameGroupJS.setParentLogFrame(logFrameGroupDTO.getParentLogFrame());
		
		return logFrameGroupJS;
	}
	
	public LogFrameGroupDTO toDTO() {
		final LogFrameGroupDTO logFrameGroupDTO = new LogFrameGroupDTO();
		
		logFrameGroupDTO.setId(getId());
		logFrameGroupDTO.setLabel(getLabel());
		logFrameGroupDTO.setType(getType());
		
		return logFrameGroupDTO;
	}

	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public LogFrameGroupType getType() {
		return Values.getEnum(this, "type", LogFrameGroupType.class);
	}

	public void setType(LogFrameGroupType type) {
		Values.setEnum(this, "type", type);
	}

	public boolean hasParentLogFrame() {
		return Values.isDefined(this, "parentLogFrame");
	}
	
	public native int getParentLogFrame() /*-{
		return this.parentLogFrame;
	}-*/;

	public native void setParentLogFrame(int parentLogFrame) /*-{
		this.parentLogFrame = parentLogFrame;
	}-*/;
	
	public void setParentLogFrame(LogFrameDTO logFrameDTO) {
		if(logFrameDTO != null) {
			setParentLogFrame(logFrameDTO.getId());
		}
	}
}
