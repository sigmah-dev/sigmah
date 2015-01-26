package org.sigmah.offline.js;

import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.dto.report.ProjectReportContent;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectReportSectionJS extends ProjectReportContentJS {
	
	protected ProjectReportSectionJS() {
	}
	
	public static ProjectReportSectionJS toJavaScript(ProjectReportSectionDTO projectReportSectionDTO) {
		final ProjectReportSectionJS projectReportSectionJS = Values.createJavaScriptObject(ProjectReportSectionJS.class);
		
		projectReportSectionJS.setId(projectReportSectionDTO.getId());
		projectReportSectionJS.setName(projectReportSectionDTO.getName());
		projectReportSectionJS.setChildren(projectReportSectionDTO.getChildren());
		
		return projectReportSectionJS;
	}

	@Override
	public ProjectReportSectionDTO createDTO() {
		final ProjectReportSectionDTO projectReportSectionDTO = new ProjectReportSectionDTO();
		
		projectReportSectionDTO.setId(getId());
		projectReportSectionDTO.setName(getName());
		projectReportSectionDTO.setChildren(getChildrenContent());
		
		return projectReportSectionDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;
	
	public native JsArray<ProjectReportContentJS> getChildren() /*-{
		return this.children;
	}-*/;

	public native void setChildren(JsArray<ProjectReportContentJS> children) /*-{
		this.children = children;
	}-*/;
	
	public List<ProjectReportContent> getChildrenContent() {
		if(getChildren() != null) {
			final JsArray<ProjectReportContentJS> children = getChildren();
			final ArrayList<ProjectReportContent> list = new ArrayList<ProjectReportContent>();

			for(int index = 0; index < children.length(); index++) {
				list.add(children.get(index).toDTO());
			}
			
			return list;
		}
		return null;
	}

	public void setChildren(List<ProjectReportContent> children) {
		if(children != null) {
			final JsArray<ProjectReportContentJS> array = Values.createJavaScriptArray(JsArray.class);
			
			for(final ProjectReportContent child : children) {
				array.push(ProjectReportContentJS.toJavaScript(child));
			}
			
			setChildren(array);
		}
	}
}
