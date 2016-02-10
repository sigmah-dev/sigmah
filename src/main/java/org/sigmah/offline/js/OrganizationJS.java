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

import org.sigmah.shared.dto.organization.OrganizationDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrganizationJS extends JavaScriptObject {

	protected OrganizationJS() {
	}

	public static OrganizationJS toJavaScript(OrganizationDTO organizationDTO) {
		final OrganizationJS organizationJS = Values.createJavaScriptObject(OrganizationJS.class);

		organizationJS.setId(organizationDTO.getId());
		organizationJS.setName(organizationDTO.getName());
		organizationJS.setLogo(organizationDTO.getLogo());
		organizationJS.setRoot(organizationDTO.getRoot());

		return organizationJS;
	}

	public OrganizationDTO toDTO() {
		final OrganizationDTO organizationDTO = new OrganizationDTO();

		organizationDTO.setId(getId());
		organizationDTO.setName(getName());
		organizationDTO.setLogo(getLogo());

		return organizationDTO;
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

	public native String getLogo() /*-{
		return this.logo;
	}-*/;

	public native void setLogo(String logo) /*-{
		this.logo = logo;
	}-*/;

	public native int getRoot() /*-{
		return this.root;
	}-*/;

	public void setRoot(OrgUnitDTO orgUnitDTO) {
		if (orgUnitDTO != null) {
			setRoot(orgUnitDTO.getId());
		}
	}

	public native void setRoot(int root) /*-{
		this.root = root;
	}-*/;
}
