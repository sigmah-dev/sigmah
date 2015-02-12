package org.sigmah.offline.js;

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
