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

import java.util.ArrayList;
import java.util.List;

import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.ProjectBannerDTO;
import org.sigmah.shared.dto.ProjectDetailsDTO;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.ProjectModelVisibilityDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ProjectModelJS extends JavaScriptObject {
	
	protected ProjectModelJS() {
	}
	
	public static ProjectModelJS toJavaScript(ProjectModelDTO projectModelDTO) {
		final ProjectModelJS projectModelJS = Values.createJavaScriptObject(ProjectModelJS.class);
		
		projectModelJS.setId(projectModelDTO.getId());
		projectModelJS.setName(projectModelDTO.getName());
		projectModelJS.setRootPhaseModel(projectModelDTO.getRootPhaseModel());
		projectModelJS.setPhaseModels(projectModelDTO.getPhaseModels());
		projectModelJS.setProjectBanner(projectModelDTO.getProjectBanner());
		projectModelJS.setProjectDetails(projectModelDTO.getProjectDetails());
		projectModelJS.setVisibilities(projectModelDTO.getVisibilities());
		projectModelJS.setLogFrameModel(projectModelDTO.getLogFrameModel());
		projectModelJS.setStatus(projectModelDTO.getStatus());
		projectModelJS.setUnderMaintenance(projectModelDTO.isUnderMaintenance());
		
		return projectModelJS;
	}
	
	public ProjectModelDTO toDTO() {
		final ProjectModelDTO projectModelDTO = new ProjectModelDTO();
		
		projectModelDTO.setId(getId());
		projectModelDTO.setName(getName());
		
		if(getProjectBanner() != null) {
			final ProjectBannerDTO projectBannerDTO = getProjectBanner().toDTO();
			projectBannerDTO.setProjectModelDTO(projectModelDTO);
			projectModelDTO.setProjectBanner(projectBannerDTO);
		}
		
		if(getProjectDetails() != null) {
			final ProjectDetailsDTO projectDetailsDTO = getProjectDetails().toDTO();
			projectDetailsDTO.setProjectModel(projectModelDTO);
			projectModelDTO.setProjectDetails(projectDetailsDTO);
		}
		
		final JsArray<ProjectModelVisibilityJS> visibilities = getVisibilities();
		if(visibilities != null) {
			final ArrayList<ProjectModelVisibilityDTO> dtos = new ArrayList<ProjectModelVisibilityDTO>();
			for(int index = 0; index < visibilities.length(); index++) {
				dtos.add(visibilities.get(index).toDTO());
			}
			projectModelDTO.setVisibilities(dtos);
		}
		
		final String status = getStatus();
		if(status != null) {
			projectModelDTO.setStatus(ProjectModelStatus.valueOf(status));
		}
		
		projectModelDTO.setUnderMaintenance(isUnderMaintenance());
		
		return projectModelDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native String getName() /*-{
		return this.name;
	}-*/;

	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native int getRootPhaseModel() /*-{
		return this.rootPhaseModel;
	}-*/;

	public void setRootPhaseModel(PhaseModelDTO rootPhaseModel) {
		if(rootPhaseModel != null) {
			setRootPhaseModel(rootPhaseModel.getId());
		}
	}
	
	public native void setRootPhaseModel(int rootPhaseModel) /*-{
		this.rootPhaseModel = rootPhaseModel;
	}-*/;

	public native JsArrayInteger getPhaseModels() /*-{
		return this.phaseModels;
	}-*/;

	public void setPhaseModels(List<PhaseModelDTO> phaseModels) {
		if(phaseModels != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();
			
			for(final PhaseModelDTO phaseModel : phaseModels) {
				array.push(phaseModel.getId());
			}
			
			setPhaseModels(array);
		}
	}
	
	public native void setPhaseModels(JsArrayInteger phaseModels) /*-{
		this.phaseModels = phaseModels;
	}-*/;

	public native ProjectBannerJS getProjectBanner() /*-{
		return this.projectBanner;
	}-*/;

	public void setProjectBanner(ProjectBannerDTO projectBanner) {
		if(projectBanner != null) {
			setProjectBanner(ProjectBannerJS.toJavaScript(projectBanner));
		}
	}
	
	public native void setProjectBanner(ProjectBannerJS projectBanner) /*-{
		this.projectBanner = projectBanner;
	}-*/;

	public native ProjectDetailsJS getProjectDetails() /*-{
		return this.projectDetails;
	}-*/;

	public void setProjectDetails(ProjectDetailsDTO projectDetails) {
		if(projectDetails != null) {
			setProjectDetails(ProjectDetailsJS.toJavaScript(projectDetails));
		}
	}
	
	public native void setProjectDetails(ProjectDetailsJS projectDetails) /*-{
		this.projectDetails = projectDetails;
	}-*/;

	public native JsArray<ProjectModelVisibilityJS> getVisibilities() /*-{
		return this.visibilities;
	}-*/;

	public void setVisibilities(List<ProjectModelVisibilityDTO> visibilities) {
		if(visibilities != null) {
			final JsArray<ProjectModelVisibilityJS> array = (JsArray<ProjectModelVisibilityJS>) JavaScriptObject.createArray();
			
			for(final ProjectModelVisibilityDTO visibility : visibilities) {
				array.push(ProjectModelVisibilityJS.toJavaScript(visibility));
			}
			
			setVisibilities(array);
		}
	}
	
	public native void setVisibilities(JsArray<ProjectModelVisibilityJS> visibilities) /*-{
		this.visibilities = visibilities;
	}-*/;

	public Integer getLogFrameModel() {
		return Values.getInteger(this, "logFrameModel");
	}

	public void setLogFrameModel(LogFrameModelDTO logFrameModel) {
		if(logFrameModel != null) {
			Values.setInteger(this, "logFrameModel", logFrameModel.getId());
		}
	}

	public native String getStatus() /*-{
		return this.status;
	}-*/;

	public void setStatus(ProjectModelStatus status) {
		if(status != null) {
			setStatus(status.name());
		}
	}
	
	public native void setStatus(String status) /*-{
		this.status = status;
	}-*/;
	
	public native boolean isUnderMaintenance() /*-{
		return this.underMaintenance;
	}-*/;
	
	public native void setUnderMaintenance(boolean underMaintenance) /*-{
		this.underMaintenance = underMaintenance;
	}-*/;
}
