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

import org.sigmah.shared.dto.AmendmentDTO;
import org.sigmah.shared.dto.logframe.LogFrameDTO;
import org.sigmah.shared.dto.referential.AmendmentState;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 * JavaScript version of <code>AmendmentDTO</code>.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class AmendmentJS extends JavaScriptObject {
	
	protected AmendmentJS() {
	}
	
	public static AmendmentJS toJavaScript(AmendmentDTO amendmentDTO) {
		final AmendmentJS amendmentJS = Values.createJavaScriptObject(AmendmentJS.class);
		
		amendmentJS.setId(amendmentDTO.getId());
		amendmentJS.setVersion(amendmentDTO.getVersion());
		amendmentJS.setRevision(amendmentDTO.getRevision());
		amendmentJS.setState(amendmentDTO.getState());
		amendmentJS.setLogFrame(amendmentDTO.getLogFrame());
		amendmentJS.setDate(Values.toJsDate(amendmentDTO.getDate()));
		
		return amendmentJS;
	}
	
	public AmendmentDTO toDTO() {
		final AmendmentDTO amendmentDTO = new AmendmentDTO();
		
		amendmentDTO.setId(getId());
		if(hasVersion()) {
			amendmentDTO.setVersion(getVersion());
		}
		if(hasRevision()) {
			amendmentDTO.setRevision(getRevision());
		}
		if(getState() != null) {
			amendmentDTO.setState(AmendmentState.valueOf(getState()));
		}
		if(getLogFrame() != null) {
			amendmentDTO.setLogFrame(getLogFrame().toDTO());
		}
		amendmentDTO.setDate(Values.toDate(getDate()));
		
		return amendmentDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native boolean hasVersion() /*-{
		return typeof this.version != 'undefined';
	}-*/;
	
	public native int getVersion() /*-{
		return this.version;
	}-*/;

	public void setVersion(Integer version) {
		if(version != null) {
			setVersion(version.intValue());
		}
	}
	
	public native void setVersion(int version) /*-{
		this.version = version;
	}-*/;

	public native boolean hasRevision() /*-{
		return typeof this.revision != 'undefined';
	}-*/;
	
	public native int getRevision() /*-{
		return this.revision;
	}-*/;

	public void setRevision(Integer revision) {
		if(revision != null) {
			setRevision(revision.intValue());
		}
	}
	
	public native void setRevision(int revision) /*-{
		this.revision = revision;
	}-*/;

	public native String getState() /*-{
		return this.state;
	}-*/;

	public void setState(AmendmentState state) {
		if(state != null) {
			setState(state.name());
		}
	}
	
	public native void setState(String state) /*-{
		this.state = state;
	}-*/;

	public native LogFrameJS getLogFrame() /*-{
		return this.logFrame;
	}-*/;

	public void setLogFrame(LogFrameDTO logFrame) {
		if(logFrame != null) {
			setLogFrame(LogFrameJS.toJavaScript(logFrame));
		}
	}
	
	public native void setLogFrame(LogFrameJS logFrame) /*-{
		this.logFrame = logFrame;
	}-*/;
	
	public native JsDate getDate() /*-{
		return this.date;
		}-*/;
	
	public native void setDate(JsDate date) /*-{
		this.date = date;
	}-*/;
}
