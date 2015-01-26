package org.sigmah.offline.js;

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
