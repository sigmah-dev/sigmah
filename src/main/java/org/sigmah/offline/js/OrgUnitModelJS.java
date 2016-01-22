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
		orgUnitModelJS.setUnderMaintenance(orgUnitModelDTO.isUnderMaintenance());
		
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
		orgUnitModelDTO.setUnderMaintenance(isUnderMaintenance());
		
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
	
	public native boolean isUnderMaintenance() /*-{
		return this.underMaintenance;
	}-*/;
	
	public native void setUnderMaintenance(boolean underMaintenance) /*-{
		this.underMaintenance = underMaintenance;
	}-*/;
}
