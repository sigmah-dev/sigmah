package org.sigmah.offline.js;

import org.sigmah.shared.dto.PhaseDTO;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PhaseJS extends JavaScriptObject {
	
	protected PhaseJS() {
	}
	
	public static PhaseJS toJavaScript(PhaseDTO phaseDTO) {
		final PhaseJS phaseJS = Values.createJavaScriptObject(PhaseJS.class);
		
		phaseJS.setId(phaseDTO.getId());
		phaseJS.setStartDate(Values.toJsDate(phaseDTO.getStartDate()));
		phaseJS.setEndDate(Values.toJsDate(phaseDTO.getEndDate()));
		phaseJS.setParentProject(phaseDTO.getParentProject());
		phaseJS.setPhaseModel(phaseDTO.getPhaseModel());
		
		return phaseJS;
	}
	
	public PhaseDTO toDTO() {
		final PhaseDTO phaseDTO = new PhaseDTO();
		
		phaseDTO.setId(getId());
		phaseDTO.setStartDate(Values.toDate(getStartDate()));
		phaseDTO.setEndDate(Values.toDate(getEndDate()));
		
		return phaseDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native JsDate getStartDate() /*-{
		return this.startDate;
	}-*/;

	public native void setStartDate(JsDate startDate) /*-{
		this.startDate = startDate;
	}-*/;

	public native JsDate getEndDate() /*-{
		return this.endDate;
	}-*/;

	public native void setEndDate(JsDate endDate) /*-{
		this.endDate = endDate;
	}-*/;

	public native int getParentProject() /*-{
		return this.parentProject;
	}-*/;

	public void setParentProject(ProjectDTO parentProject) {
		if(parentProject != null) {
			setParentProject(parentProject.getId());
		}
	}
	
	public native void setParentProject(int parentProject) /*-{
		this.parentProject = parentProject;
	}-*/;

	public native int getPhaseModel() /*-{
		return this.phaseModel;
	}-*/;

	public void setPhaseModel(PhaseModelDTO phaseModel) {
		if(phaseModel != null) {
			setPhaseModel(phaseModel.getId());
		}
	}
	
	public native void setPhaseModel(int phaseModel) /*-{
		this.phaseModel = phaseModel;
	}-*/;
}
