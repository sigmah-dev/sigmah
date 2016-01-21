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
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.layout.LayoutDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrgUnitBannerJS extends JavaScriptObject {
	
	protected OrgUnitBannerJS() {
	}
	
	public static OrgUnitBannerJS toJavaScript(OrgUnitBannerDTO orgUnitBannerDTO) {
		final OrgUnitBannerJS orgUnitBannerJS = Values.createJavaScriptObject(OrgUnitBannerJS.class);
		
		orgUnitBannerJS.setId(orgUnitBannerDTO.getId());
		orgUnitBannerJS.setLayout(orgUnitBannerDTO.getLayout());
		orgUnitBannerJS.setOrgUnitModel(orgUnitBannerDTO.getOrgUnitModel());
		
		return orgUnitBannerJS;
	}
	
	public OrgUnitBannerDTO toDTO() {
		final OrgUnitBannerDTO orgUnitBannerDTO = new OrgUnitBannerDTO();
		
		orgUnitBannerDTO.setId(getId());
		if(getLayout() != null) {
			orgUnitBannerDTO.setLayout(getLayout().toDTO());
		}
		
		return orgUnitBannerDTO;
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

	public native int getOrgUnitModel() /*-{
		return this.orgUnitModel;
	}-*/;

	public void setOrgUnitModel(OrgUnitModelDTO orgUnitModel) {
		if(orgUnitModel != null) {
			setOrgUnitModel(orgUnitModel.getId());
		}
	}
	
	public native void setOrgUnitModel(int orgUnitModel) /*-{
		this.orgUnitModel = orgUnitModel;
	}-*/;
}
