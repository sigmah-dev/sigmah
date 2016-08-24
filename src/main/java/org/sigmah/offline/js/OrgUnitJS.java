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

import java.util.Date;
import java.util.Set;

import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.orgunit.OrgUnitDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayInteger;
import java.util.HashSet;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OrgUnitJS extends JavaScriptObject {

	protected OrgUnitJS() {
	}

	public static OrgUnitJS toJavaScript(OrgUnitDTO orgUnitDTO) {
		final OrgUnitJS orgUnitJS = Values.createJavaScriptObject(OrgUnitJS.class);

		orgUnitJS.setId(orgUnitDTO.getId());
		orgUnitJS.setName(orgUnitDTO.getName());
		orgUnitJS.setFullName(orgUnitDTO.getFullName());
		orgUnitJS.setOrgUnitModel(orgUnitDTO.getOrgUnitModel());
		orgUnitJS.setPlannedBudget(orgUnitDTO.getPlannedBudget());
		orgUnitJS.setSpendBudget(orgUnitDTO.getSpendBudget());
		orgUnitJS.setReceivedBudget(orgUnitDTO.getReceivedBudget());
		orgUnitJS.setParent(orgUnitDTO.getParentOrgUnit());
		orgUnitJS.setChildren(toArray(orgUnitDTO.getChildrenOrgUnits()));
		orgUnitJS.setCalendarId(orgUnitDTO.getCalendarId());
		orgUnitJS.setDeleted(orgUnitDTO.getDeleted());
		orgUnitJS.setOfficeLocationCountry(orgUnitDTO.getOfficeLocationCountry());

		return orgUnitJS;
	}

	public OrgUnitDTO toDTO() {
		final OrgUnitDTO orgUnitDTO = new OrgUnitDTO();

		orgUnitDTO.setId(getId());
		orgUnitDTO.setName(getName());
		orgUnitDTO.setFullName(getFullName());

		if (hasPlannedBudget()) {
			orgUnitDTO.setPlannedBudget(getPlannedBudget());
		}
		if (hasSpendBudget()) {
			orgUnitDTO.setSpendBudget(getSpendBudget());
		}
		if (hasReceivedBudget()) {
			orgUnitDTO.setReceivedBudget(getReceivedBudget());
		}
		if (hasCalendarId()) {
			orgUnitDTO.setCalendarId(getCalendarId());
		}
		orgUnitDTO.setDeleted(getDeleted());
		orgUnitDTO.setChildrenOrgUnits(new HashSet<OrgUnitDTO>());

		return orgUnitDTO;
	}

	public static JsArrayInteger toArray(Set<OrgUnitDTO> children) {
		if (children == null) {
			return null;
		}

		final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();
		for (final OrgUnitDTO child : children) {
			array.push(child.getId());
		}
		return array;
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

	public native String getFullName() /*-{
		return this.fullName;
	}-*/;

	public native void setFullName(String fullName) /*-{
		this.fullName = fullName;
	}-*/;

	public native boolean hasOrgUnitModel() /*-{
		return typeof this.orgUnitModel != 'undefined';
	}-*/;

	public native int getOrgUnitModel() /*-{
		return this.orgUnitModel;
	}-*/;

	public void setOrgUnitModel(OrgUnitModelDTO orgUnitModel) {
		if (orgUnitModel != null) {
			setOrgUnitModel(orgUnitModel.getId());
		}
	}

	public native void setOrgUnitModel(int orgUnitModelId) /*-{
		this.orgUnitModel = orgUnitModelId;
	}-*/;

	public native boolean hasPlannedBudget() /*-{
		return typeof this.plannedBudget != 'undefined';
	}-*/;

	public native double getPlannedBudget() /*-{
		return this.plannedBudget;
	}-*/;

	public void setPlannedBudget(Double plannedBudget) {
		if (plannedBudget != null) {
			setPlannedBudget(plannedBudget.doubleValue());
		}
	}

	public native void setPlannedBudget(double plannedBudget) /*-{
		this.plannedBudget = plannedBudget;
	}-*/;

	public native boolean hasSpendBudget() /*-{
		return typeof this.spendBudget != 'undefined';
	}-*/;

	public native double getSpendBudget() /*-{
		return this.spendBudget;
	}-*/;

	public void setSpendBudget(Double spendBudget) {
		if (spendBudget != null) {
			setSpendBudget(spendBudget.doubleValue());
		}
	}

	public native void setSpendBudget(double spendBudget) /*-{
		this.spendBudget = spendBudget;
	}-*/;

	public native boolean hasReceivedBudget() /*-{
		return typeof this.receivedBudget != 'undefined';
	}-*/;

	public native double getReceivedBudget() /*-{
		return this.receivedBudget;
	}-*/;

	public void setReceivedBudget(Double receivedBudget) {
		if (receivedBudget != null) {
			setReceivedBudget(receivedBudget.doubleValue());
		}
	}

	public native void setReceivedBudget(double receivedBudget) /*-{
		this.receivedBudget = receivedBudget;
	}-*/;

	public native boolean hasParent() /*-{
		return typeof this.parent != 'undefined';
	}-*/;

	public native int getParent() /*-{
		return this.parent;
	}-*/;

	public void setParent(OrgUnitDTO parent) {
		if (parent != null) {
			setParent(parent.getId());
		}
	}

	public native void setParent(int parent) /*-{
		this.parent = parent;
	}-*/;

	public native JsArrayInteger getChildren() /*-{
		return this.children;
	}-*/;

	public native void setChildren(JsArrayInteger children) /*-{
		this.children = children;
	}-*/;

	public native boolean hasCalendarId() /*-{
		return typeof this.calendarId != 'undefined';
	}-*/;

	public native int getCalendarId() /*-{
		return this.calendarId;
	}-*/;

	public void setCalendarId(Integer calendarId) {
		if (calendarId != null) {
			setCalendarId(calendarId.intValue());
		}
	}

	public native void setCalendarId(int calendarId) /*-{
		this.calendarId = calendarId;
	}-*/;

	public native Date getDeleted() /*-{
		return this.deleted;
	}-*/;

	public native void setDeleted(Date deleted) /*-{
		this.deleted = deleted;
	}-*/;

	public native Date getStartDate() /*-{
		return this.startDate;
	}-*/;

	public native void setStartDate(Date startDate) /*-{
		this.startDate = startDate;
	}-*/;

	public native Date getEndDate() /*-{
		return this.endDate;
	}-*/;

	public native void setEndDate(Date endDate) /*-{
		this.endDate = endDate;
	}-*/;

	public native boolean hasCountry() /*-{
		return typeof this.country != 'undefined';
	}-*/;

	public native int getOfficeLocationCountry() /*-{
		return this.country;
	}-*/;

	public void setOfficeLocationCountry(CountryDTO country) {
		if (country != null) {
			setOfficeLocationCountry(country.getId());
		}
	}

	public native void setOfficeLocationCountry(int country) /*-{
		this.country = country;
	}-*/;

}
