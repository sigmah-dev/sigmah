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
