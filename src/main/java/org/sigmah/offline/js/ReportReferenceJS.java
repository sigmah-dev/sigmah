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

import com.google.gwt.core.client.JsDate;
import org.sigmah.shared.dto.report.ReportReference;
import org.sigmah.shared.dto.value.FileVersionDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ReportReferenceJS extends ListableValueJS {
	
	protected ReportReferenceJS() {
	}
	
	public static ReportReferenceJS toJavaScript(ReportReference reportReference) {
		final ReportReferenceJS reportReferenceJS = Values.createJavaScriptObject(ReportReferenceJS.class);
		reportReferenceJS.setListableValueType(Type.REPORT_REFERENCE);
		
		reportReferenceJS.setId(reportReference.getId());
		reportReferenceJS.setParentId(null);
		reportReferenceJS.setName(reportReference.getName());
		reportReferenceJS.setPhaseName(reportReference.getPhaseName());
		reportReferenceJS.setFlexibleElementLabel(reportReference.getFlexibleElementLabel());
		reportReferenceJS.setLastEditDate(Values.toJsDate(reportReference.getLastEditDate()));
		reportReferenceJS.setEditorName(reportReference.getEditorName());
		reportReferenceJS.setFileVersionDTO(reportReference.getFileVersion());
		
		return reportReferenceJS;
	}

	public ReportReference toReportReference() {
		final ReportReference reportReference = new ReportReference(getFileVersionDTO());
		
		reportReference.setId(getId());
		reportReference.setName(getName());
		reportReference.setPhaseName(getPhaseName());
		reportReference.setFlexibleElementLabel(getFlexibleElementLabel());
		reportReference.setLastEditDate(Values.toDate(getLastEditDate()));
		reportReference.setEditorName(getEditorName());
		
		return reportReference;
	}
	
	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public native String getParentId() /*-{
		return this.parentId;
	}-*/;
	
	public native void setParentId(String parentId) /*-{
		this.parentId = parentId;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native String getPhaseName() /*-{
		return this.phaseName;
	}-*/;

	public native void setPhaseName(String phaseName) /*-{
		this.phaseName = phaseName;
	}-*/;

	public native String getFlexibleElementLabel() /*-{
		return this.flexibleElementLabel;
	}-*/;

	public native void setFlexibleElementLabel(String flexibleElementLabel) /*-{
		this.flexibleElementLabel = flexibleElementLabel;
	}-*/;

	public native JsDate getLastEditDate() /*-{
		return this.lastEditDate;
	}-*/;

	public native void setLastEditDate(JsDate lastEditDate) /*-{
		this.lastEditDate = lastEditDate;
	}-*/;

	public native String getEditorName() /*-{
		return this.editorName;
	}-*/;

	public native void setEditorName(String editorName) /*-{
		this.editorName = editorName;
	}-*/;
	
	public native FileVersionJS getFileVersion() /*-{
		return this.fileVersion;
	}-*/;
	
	public native void setFileVersion(FileVersionJS fileVersion) /*-{
		this.fileVersion = fileVersion;
	}-*/;
	
	public FileVersionDTO getFileVersionDTO() {
		if(getFileVersion() != null) {
			return getFileVersion().toDTO();
		}
		return null;
	}
	
	public void setFileVersionDTO(FileVersionDTO fileVersion) {
		if(fileVersion != null) {
			setFileVersion(FileVersionJS.toJavaScript(fileVersion));
		}
	}
}
