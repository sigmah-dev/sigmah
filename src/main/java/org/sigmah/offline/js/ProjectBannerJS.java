package org.sigmah.offline.js;

import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectBannerJS extends JavaScriptObject {
	
	protected ProjectBannerJS() {
	}
	
	public static ProjectBannerJS toJavaScript(ProjectBannerDTO projectBannerDTO) {
		final ProjectBannerJS projectBannerJS = Values.createJavaScriptObject(ProjectBannerJS.class);
		
		projectBannerJS.setId(projectBannerDTO.getId());
		projectBannerJS.setLayout(projectBannerDTO.getLayout());
		projectBannerJS.setProjectModel(projectBannerDTO.getProjectModelDTO());
		
		return projectBannerJS;
	}
	
	public ProjectBannerDTO toDTO() {
		final ProjectBannerDTO projectBannerDTO = new ProjectBannerDTO();
		
		projectBannerDTO.setId(getId());
		
		if(getLayout() != null) {
			projectBannerDTO.setLayout(getLayout().toDTO());
		}
		
		return projectBannerDTO;
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

	public native boolean hasProjectModel() /*-{
		return typeof this.projectModel != 'undefined';
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
