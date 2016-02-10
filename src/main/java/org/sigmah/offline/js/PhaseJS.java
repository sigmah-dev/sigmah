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
