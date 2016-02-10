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

	public final ProjectReportSectionDTO createDTO() {
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
			final JsArray<ProjectReportContentJS> array = Values.createTypedJavaScriptArray(ProjectReportContentJS.class);
			
			for(final ProjectReportContent child : children) {
				array.push(ProjectReportContentJS.toJavaScript(child));
			}
			
			setChildren(array);
		}
	}
}
