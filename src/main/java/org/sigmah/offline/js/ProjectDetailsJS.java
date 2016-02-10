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

import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectDetailsJS extends JavaScriptObject {
	
	protected ProjectDetailsJS() {
	}
	
	public static ProjectDetailsJS toJavaScript(ProjectDetailsDTO projectDetailsDTO) {
		final ProjectDetailsJS projectDetailsJS = Values.createJavaScriptObject(ProjectDetailsJS.class);
		
		projectDetailsJS.setId(projectDetailsDTO.getId());
		projectDetailsJS.setLayout(projectDetailsDTO.getLayout());
		projectDetailsJS.setProjectModel(projectDetailsDTO.getProjectModel());
		
		return projectDetailsJS;
	}
	
	public ProjectDetailsDTO toDTO() {
		final ProjectDetailsDTO projectDetailsDTO = new ProjectDetailsDTO();
		
		projectDetailsDTO.setId(getId());
		if(getLayout() != null) {
			projectDetailsDTO.setLayout(getLayout().toDTO());
		}
		
		return projectDetailsDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native LayoutJS getLayout() /*-{
		return this.layout;
	}-*/;

	public void setLayout(LayoutDTO layout) {
		if(layout != null) {
			setLayout(LayoutJS.toJavaScript(layout));
		}
	}
	
	public native void setLayout(LayoutJS layout) /*-{
		this.layout = layout;
	}-*/;

	public native int getProjectModel() /*-{
		return this.projectModel;
	}-*/;

	public void setProjectModel(ProjectModelDTO projectModel) {
		if(projectModel != null) {
			setProjectModel(projectModel.getId());
		}
	}
	
	public native void setProjectModel(int projectModel) /*-{
		this.projectModel = projectModel;
	}-*/;
}
