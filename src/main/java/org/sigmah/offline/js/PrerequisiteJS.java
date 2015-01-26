package org.sigmah.offline.js;

import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.PrerequisiteDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PrerequisiteJS extends JavaScriptObject {
	
	protected PrerequisiteJS() {
	}
	
	public static PrerequisiteJS toJavaScript(PrerequisiteDTO prerequisiteDTO) {
		final PrerequisiteJS prerequisiteJS = Values.createJavaScriptObject(PrerequisiteJS.class);
		
		prerequisiteJS.setId(prerequisiteDTO.getId());
		prerequisiteJS.setCode(prerequisiteDTO.getCode());
		prerequisiteJS.setPosition(prerequisiteDTO.getPosition());
		prerequisiteJS.setContent(prerequisiteDTO.getContent());
		prerequisiteJS.setParentLogFrame(prerequisiteDTO.getParentLogFrame());
		prerequisiteJS.setGroup(prerequisiteDTO.getGroup());
		prerequisiteJS.setLabel(prerequisiteDTO.getLabel());
		
		return prerequisiteJS;
	}
	
	public PrerequisiteDTO toDTO() {
		final PrerequisiteDTO prerequisiteDTO = new PrerequisiteDTO();
		
		prerequisiteDTO.setId(getId());
		prerequisiteDTO.setCode(getCode());
		prerequisiteDTO.setPosition(getPosition());
		prerequisiteDTO.setLabel(getLabel());
		
		return prerequisiteDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public Integer getCode() {
		return Values.getInteger(this, "code");
	}

	public void setCode(Integer code) {
		Values.setInteger(this, "code", code);
	}

	public Integer getPosition() {
		return Values.getInteger(this, "position");
	}

	public void setPosition(Integer position) {
		Values.setInteger(this, "position", position);
	}

	public native final String getContent() /*-{
		return this.content;
	}-*/;

	public native final void setContent(String content) /*-{
		this.content = content;
	}-*/;
	
	public native int getParentLogFrame() /*-{
		return this.parentLogFrame;
	}-*/;

	public native void setParentLogFrame(int parentLogFrame) /*-{
		this.parentLogFrame = parentLogFrame;
	}-*/;

	public void setParentLogFrame(LogFrameDTO parentLogFrame) {
		if(parentLogFrame != null) {
			setParentLogFrame(parentLogFrame.getId());
		}
	}
	
	public native final boolean hasGroup() /*-{
		return typeof this.group != 'undefined';
	}-*/;
	
	public native final int getGroup() /*-{
		return this.group;
	}-*/;

	public native final void setGroup(int group) /*-{
		this.group = group;
	}-*/;
	
	public void setGroup(LogFrameGroupDTO logFrameGroupDTO) {
		if(logFrameGroupDTO != null) {
			setGroup(logFrameGroupDTO.getId());
		}
	}
	
	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;
}
