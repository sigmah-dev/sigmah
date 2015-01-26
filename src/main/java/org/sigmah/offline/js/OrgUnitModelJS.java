package org.sigmah.offline.js;

import org.sigmah.shared.dto.OrgUnitBannerDTO;
import org.sigmah.shared.dto.OrgUnitDetailsDTO;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrgUnitModelJS extends JavaScriptObject {
	
	protected OrgUnitModelJS() {
	}
	
	public static OrgUnitModelJS toJavaScript(OrgUnitModelDTO orgUnitModelDTO) {
		final OrgUnitModelJS orgUnitModelJS = Values.createJavaScriptObject(OrgUnitModelJS.class);
		
		orgUnitModelJS.setId(orgUnitModelDTO.getId());
		orgUnitModelJS.setName(orgUnitModelDTO.getName());
		orgUnitModelJS.setTitle(orgUnitModelDTO.getTitle());
		orgUnitModelJS.setBanner(orgUnitModelDTO.getBanner());
		orgUnitModelJS.setDetails(orgUnitModelDTO.getDetails());
		orgUnitModelJS.setHasBudget(orgUnitModelDTO.getHasBudget());
		orgUnitModelJS.setCanContainProjects(orgUnitModelDTO.getCanContainProjects());
		orgUnitModelJS.setTopOrgUnitModel(orgUnitModelDTO.isTopOrgUnitModel());
		
		return orgUnitModelJS;
	}
	
	public OrgUnitModelDTO toDTO() {
		final OrgUnitModelDTO orgUnitModelDTO = new OrgUnitModelDTO();
		
		orgUnitModelDTO.setId(getId());
		orgUnitModelDTO.setName(getName());
		orgUnitModelDTO.setTitle(getTitle());
		
		if(getBanner() != null) {
			final OrgUnitBannerDTO orgUnitBannerDTO = getBanner().toDTO();
			orgUnitBannerDTO.setOrgUnitModel(orgUnitModelDTO);
			orgUnitModelDTO.setBanner(orgUnitBannerDTO);
		}
		
		if(getDetails() != null) {
			final OrgUnitDetailsDTO orgUnitDetailsDTO = getDetails().toDTO();
			orgUnitDetailsDTO.setOrgUnitModel(orgUnitModelDTO);
			orgUnitModelDTO.setDetails(orgUnitDetailsDTO);
		}
		
		if(hasHasBudget()) {
			orgUnitModelDTO.setHasBudget(isHasBudget());
		}
		
		if(hasCanContainProjects()) {
			orgUnitModelDTO.setCanContainProjects(isCanContainProjects());
		}
		
		orgUnitModelDTO.setTopOrgUnitModel(isTopOrgUnitModel());
		
		return orgUnitModelDTO;
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
	
	public native String getTitle() /*-{
		return this.title;
	}-*/;
			
	public native void setTitle(String title) /*-{
		this.title = title;
	}-*/;

	public native OrgUnitBannerJS getBanner() /*-{
		return this.banner;
	}-*/;

	public void setBanner(OrgUnitBannerDTO banner) {
		if(banner != null) {
			setBanner(OrgUnitBannerJS.toJavaScript(banner));
		}
	}

	public native void setBanner(OrgUnitBannerJS banner) /*-{
		this.banner = banner;
	}-*/;

	public native OrgUnitDetailsJS getDetails() /*-{
		return this.details;
	}-*/;

	public void setDetails(OrgUnitDetailsDTO details) {
		if(details != null) {
			setDetails(OrgUnitDetailsJS.toJavaScript(details));
		}
	}

	public native void setDetails(OrgUnitDetailsJS details) /*-{
		this.details = details;
	}-*/;

	public native boolean hasHasBudget() /*-{
		return typeof this.hasBudget != 'undefined';
	}-*/;
	
	public native boolean isHasBudget() /*-{
		return this.hasBudget;
	}-*/;

	public void setHasBudget(Boolean hasBudget) {
		if(hasBudget != null) {
			setHasBudget(hasBudget.booleanValue());
		}
	}

	public native void setHasBudget(boolean hasBudget) /*-{
		this.hasBudget = hasBudget;
	}-*/;

	public native boolean hasCanContainProjects() /*-{
		return typeof this.canContainProjects != 'undefined';
	}-*/;
	
	public native boolean isCanContainProjects() /*-{
		return this.canContainProjects;
	}-*/;

	public void setCanContainProjects(Boolean canContainProjects) {
		if(canContainProjects != null) {
			setCanContainProjects(canContainProjects.booleanValue());
		}
	}
	
	public native void setCanContainProjects(boolean canContainProjects) /*-{
		this.canContainProjects = canContainProjects;
	}-*/;

	public native ProjectModelStatus getStatus() /*-{
		return this.status;
	}-*/;

	public native void setStatus(ProjectModelStatus status) /*-{
		this.status = status;
	}-*/;

	public native boolean isTopOrgUnitModel() /*-{
		return this.topOrgUnitModel;
	}-*/;

	public native void setTopOrgUnitModel(boolean topOrgUnitModel) /*-{
		this.topOrgUnitModel = topOrgUnitModel;
	}-*/;
}
