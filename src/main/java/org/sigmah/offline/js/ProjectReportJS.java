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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.report.ProjectReportSectionDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectReportJS extends JavaScriptObject {
	
	protected ProjectReportJS() {
	}
	
	public static ProjectReportJS toJavaScript(ProjectReportDTO projectReportDTO) {
		final ProjectReportJS projectReportJS = Values.createJavaScriptObject(ProjectReportJS.class);
		
		projectReportJS.setId(projectReportDTO.getId());
		projectReportJS.setVersionId(projectReportDTO.getVersionId());
		projectReportJS.setOrgUnitId(projectReportDTO.getOrgUnitId());
		projectReportJS.setName(projectReportDTO.getName());
		projectReportJS.setPhaseName(projectReportDTO.getPhaseName());
		projectReportJS.setSections(projectReportDTO.getSections());
		projectReportJS.setDraft(projectReportDTO.isDraft());
		projectReportJS.setLastEditDate(projectReportDTO.getLastEditDate());
		projectReportJS.setEditorName(projectReportDTO.getEditorName());
		
		return projectReportJS;
	}
	
	public ProjectReportDTO toDTO() {
		final ProjectReportDTO projectReportDTO = new ProjectReportDTO();
		
		projectReportDTO.setId(getId());
		projectReportDTO.setVersionId(getVersionId());
		projectReportDTO.setOrgUnitId(getOrgUnitId());
		projectReportDTO.setName(getName());
		projectReportDTO.setPhaseName(getPhaseName());
		projectReportDTO.setSections(getSectionDTOs());
		projectReportDTO.setDraft(isDraft());
		projectReportDTO.setLastEditDate(getLastEditDate());
		projectReportDTO.setEditorName(getEditorName());
		
		return projectReportDTO;
	}
	
	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public Integer getVersionId() {
		return Values.getInteger(this, "versionId");
	}

	public void setVersionId(Integer versionId) {
		Values.setInteger(this, "versionId", versionId);
	}

	public Integer getProjectId() {
		return Values.getInteger(this, "projectId");
	}

	public void setProjectId(Integer projectId) {
		Values.setInteger(this, "projectId", projectId);
	}

	public Integer getOrgUnitId() {
		return Values.getInteger(this, "orgUnitId");
	}

	public void setOrgUnitId(Integer orgUnitId) {
		Values.setInteger(this, "orgUnitId", orgUnitId);
	}

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

	public native JsArray<ProjectReportSectionJS> getSections() /*-{
		return this.sections;
	}-*/;

	public native void setSections(JsArray<ProjectReportSectionJS> sections) /*-{
		this.sections = sections;
	}-*/;

	public List<ProjectReportSectionDTO> getSectionDTOs() {
		if(getSections() != null) {
			final JsArray<ProjectReportSectionJS> sections = getSections();
			final ArrayList<ProjectReportSectionDTO> list = new ArrayList<ProjectReportSectionDTO>();

			for(int index = 0; index < sections.length(); index++) {
				list.add(sections.get(index).createDTO());
			}
			
			return list;
		}
		return null;
	}

	public void setSections(List<ProjectReportSectionDTO> sections) {
		if(sections != null) {
			final JsArray<ProjectReportSectionJS> array = Values.createTypedJavaScriptArray(ProjectReportSectionJS.class);
			
			for(final ProjectReportSectionDTO section : sections) {
				array.push(ProjectReportSectionJS.toJavaScript(section));
			}
			
			setSections(array);
		}
	}

	public native boolean isDraft() /*-{
		return this.draft;
	}-*/;

	public native void setDraft(boolean draft) /*-{
		this.draft = draft;
	}-*/;

	public Date getLastEditDate() {
		return Values.getDate(this, "lastEditDate");
	}

	public void setLastEditDate(Date lastEditDate) {
		Values.setDate(this, "lastEditDate", lastEditDate);
	}

	public native String getEditorName() /*-{
		return this.editorName;
	}-*/;

	public native void setEditorName(String editorName) /*-{
		this.editorName = editorName;
	}-*/;
}
