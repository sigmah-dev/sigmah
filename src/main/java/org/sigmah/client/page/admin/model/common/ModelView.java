package org.sigmah.client.page.admin.model.common;

import org.sigmah.shared.domain.ProjectModelStatus;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.ProjectModelDTO;

import com.extjs.gxt.ui.client.widget.ContentPanel;

public abstract class ModelView extends ContentPanel {
	
	protected ProjectModelDTO projectModel;
	protected OrgUnitModelDTO orgUnitModel;

	public void setProjectModel(ProjectModelDTO model) {
		this.projectModel = model;
		if(projectModel != null && ProjectModelStatus.DRAFT.equals(projectModel.getStatus()))
			enableToolBar();
	}


	public ProjectModelDTO getProjectModel() {
		return projectModel;
	}
	
	public void setOrgUnitModel(OrgUnitModelDTO model) {
		this.orgUnitModel = model;
		if(orgUnitModel != null && ProjectModelStatus.DRAFT.equals(orgUnitModel.getStatus()))
			enableToolBar();
	}


	public OrgUnitModelDTO getOrgUnitModel() {
		return orgUnitModel;
	}
	
	public abstract void enableToolBar();
	
	public void refreshProjectModel(ProjectModelDTO model) {
		this.projectModel = model;
	}
	
	public void refreshOrgUnitModel(OrgUnitModelDTO model) {
		this.orgUnitModel = model;
	}
}
