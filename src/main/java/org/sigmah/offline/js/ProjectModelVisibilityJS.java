package org.sigmah.offline.js;

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
