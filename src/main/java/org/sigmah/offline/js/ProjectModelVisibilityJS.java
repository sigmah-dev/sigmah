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

import org.sigmah.shared.dto.ProjectModelVisibilityDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectModelVisibilityJS extends JavaScriptObject {
	
	protected ProjectModelVisibilityJS() {
	}
	
	public static ProjectModelVisibilityJS toJavaScript(ProjectModelVisibilityDTO projectModelVisibilityDTO) {
		final ProjectModelVisibilityJS projectModelVisibilityJS = Values.createJavaScriptObject(ProjectModelVisibilityJS.class);
		
		projectModelVisibilityJS.setId(projectModelVisibilityDTO.getId());
		projectModelVisibilityJS.setType(projectModelVisibilityDTO.getType());
		projectModelVisibilityJS.setOrganizationId(projectModelVisibilityDTO.getOrganizationId());
		
		return projectModelVisibilityJS;
	}
	
	public ProjectModelVisibilityDTO toDTO() {
		final ProjectModelVisibilityDTO projectModelVisibilityDTO = new ProjectModelVisibilityDTO();
		
		projectModelVisibilityDTO.setId(getId());
		if(getType() != null) {
			projectModelVisibilityDTO.setType(ProjectModelType.valueOf(getType()));
		}
		projectModelVisibilityDTO.setOrganizationId(getOrganizationId());
		
		return projectModelVisibilityDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getType() /*-{
		return this.type;
	}-*/;

	public void setType(ProjectModelType type) {
		if(type != null) {
			setType(type.name());
		}
	}
	
	public native void setType(String type) /*-{
		this.type = type;
	}-*/;

	public native int getOrganizationId() /*-{
		return this.organizationId;
	}-*/;

	public native void setOrganizationId(int organizationId) /*-{
		this.organizationId = organizationId;
	}-*/;
}
