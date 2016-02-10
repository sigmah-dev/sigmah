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

import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.google.gwt.core.client.JavaScriptObject;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class LogFrameModelJS extends JavaScriptObject {
	
	protected LogFrameModelJS() {
	}

	public static LogFrameModelJS toJavaScript(LogFrameModelDTO logFrameModelDTO) {
		final LogFrameModelJS logFrameModelJS = Values.createJavaScriptObject(LogFrameModelJS.class);
		
		logFrameModelJS.setId(logFrameModelDTO.getId());
		logFrameModelJS.setName(logFrameModelDTO.getName());
		logFrameModelJS.setEnableSpecificObjectivesGroups(logFrameModelDTO.getEnableSpecificObjectivesGroups());
		logFrameModelJS.setSpecificObjectivesMax(logFrameModelDTO.getSpecificObjectivesMax());
		logFrameModelJS.setSpecificObjectivesGroupsMax(logFrameModelDTO.getSpecificObjectivesGroupsMax());
		logFrameModelJS.setSpecificObjectivesPerGroupMax(logFrameModelDTO.getSpecificObjectivesPerGroupMax());
		logFrameModelJS.setEnableExpectedResultsGroups(logFrameModelDTO.getEnableExpectedResultsGroups());
		logFrameModelJS.setExpectedResultsMax(logFrameModelDTO.getExpectedResultsMax());
		logFrameModelJS.setExpectedResultsGroupsMax(logFrameModelDTO.getExpectedResultsGroupsMax());
		logFrameModelJS.setExpectedResultsPerGroupMax(logFrameModelDTO.getExpectedResultsPerGroupMax());
		logFrameModelJS.setExpectedResultsPerSpecificObjectiveMax(logFrameModelDTO.getExpectedResultsPerSpecificObjectiveMax());
		logFrameModelJS.setEnableActivitiesGroups(logFrameModelDTO.getEnableActivitiesGroups());
		logFrameModelJS.setActivitiesMax(logFrameModelDTO.getActivitiesMax());
		logFrameModelJS.setActivitiesGroupsMax(logFrameModelDTO.getActivitiesGroupsMax());
		logFrameModelJS.setActivitiesPerGroupMax(logFrameModelDTO.getActivitiesPerGroupMax());
		logFrameModelJS.setActivitiesPerExpectedResultMax(logFrameModelDTO.getActivitiesPerExpectedResultMax());
		logFrameModelJS.setEnablePrerequisitesGroups(logFrameModelDTO.getEnablePrerequisitesGroups());
		logFrameModelJS.setPrerequisitesMax(logFrameModelDTO.getPrerequisitesMax());
		logFrameModelJS.setPrerequisitesGroupsMax(logFrameModelDTO.getPrerequisitesGroupsMax());
		logFrameModelJS.setPrerequisitesPerGroupMax(logFrameModelDTO.getPrerequisitesPerGroupMax());
		
		return logFrameModelJS;
	}
	
	public LogFrameModelDTO toDTO() {
		final LogFrameModelDTO logFrameModelDTO = new LogFrameModelDTO();
		
		logFrameModelDTO.setId(getId());
		logFrameModelDTO.setName(getName());
		logFrameModelDTO.setEnableSpecificObjectivesGroups(isEnableSpecificObjectivesGroups());
		logFrameModelDTO.setSpecificObjectivesMax(getSpecificObjectivesMax());
		logFrameModelDTO.setSpecificObjectivesGroupsMax(getSpecificObjectivesGroupsMax());
		logFrameModelDTO.setSpecificObjectivesPerGroupMax(getSpecificObjectivesPerGroupMax());
		logFrameModelDTO.setEnableExpectedResultsGroups(isEnableExpectedResultsGroups());
		logFrameModelDTO.setExpectedResultsMax(getExpectedResultsMax());
		logFrameModelDTO.setExpectedResultsGroupsMax(getExpectedResultsGroupsMax());
		logFrameModelDTO.setExpectedResultsPerGroupMax(getExpectedResultsPerGroupMax());
		logFrameModelDTO.setExpectedResultsPerSpecificObjectiveMax(getExpectedResultsPerSpecificObjectiveMax());
		logFrameModelDTO.setEnableActivitiesGroups(isEnableActivitiesGroups());
		logFrameModelDTO.setActivitiesMax(getActivitiesMax());
		logFrameModelDTO.setActivitiesGroupsMax(getActivitiesGroupsMax());
		logFrameModelDTO.setActivitiesPerGroupMax(getActivitiesPerGroupMax());
		logFrameModelDTO.setActivitiesPerExpectedResultMax(getActivitiesPerExpectedResultMax());
		logFrameModelDTO.setEnablePrerequisitesGroups(isEnablePrerequisitesGroups());
		logFrameModelDTO.setPrerequisitesMax(getPrerequisitesMax());
		logFrameModelDTO.setPrerequisitesGroupsMax(getPrerequisitesGroupsMax());
		logFrameModelDTO.setPrerequisitesPerGroupMax(getPrerequisitesPerGroupMax());
		
		return logFrameModelDTO;
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

	public native boolean isEnableSpecificObjectivesGroups() /*-{
		return this.enableSpecificObjectivesGroups;
	}-*/;

	public native void setEnableSpecificObjectivesGroups(boolean enableSpecificObjectivesGroups) /*-{
		this.enableSpecificObjectivesGroups = enableSpecificObjectivesGroups;
	}-*/;

	public Integer getSpecificObjectivesMax() {
		return Values.getInteger(this, "specificObjectivesMax");
	}

	public void setSpecificObjectivesMax(Integer specificObjectivesMax) {
		Values.setInteger(this, "specificObjectivesMax", specificObjectivesMax);
	}

	public Integer getSpecificObjectivesGroupsMax() {
		return Values.getInteger(this, "specificObjectivesGroupsMax");
	}

	public void setSpecificObjectivesGroupsMax(Integer specificObjectivesGroupsMax) {
		Values.setInteger(this, "specificObjectivesGroupsMax", specificObjectivesGroupsMax);
	}

	public Integer getSpecificObjectivesPerGroupMax() {
		return Values.getInteger(this, "specificObjectivesPerGroupMax");
	}

	public void setSpecificObjectivesPerGroupMax(Integer specificObjectivesPerGroupMax) {
		Values.setInteger(this, "specificObjectivesPerGroupMax", specificObjectivesPerGroupMax);
	}

	public native boolean isEnableExpectedResultsGroups() /*-{
		return this.enableExpectedResultsGroups;
	}-*/;

	public native void setEnableExpectedResultsGroups(boolean enableExpectedResultsGroups) /*-{
		this.enableExpectedResultsGroups = enableExpectedResultsGroups;
	}-*/;

	public Integer getExpectedResultsMax() {
		return Values.getInteger(this, "expectedResultsMax");
	}

	public void setExpectedResultsMax(Integer expectedResultsMax) {
		Values.setInteger(this, "expectedResultsMax", expectedResultsMax);
	}

	public Integer getExpectedResultsGroupsMax() {
		return Values.getInteger(this, "expectedResultsGroupsMax");
	}

	public void setExpectedResultsGroupsMax(Integer expectedResultsGroupsMax) {
		Values.setInteger(this, "expectedResultsGroupsMax", expectedResultsGroupsMax);
	}

	public Integer getExpectedResultsPerGroupMax() {
		return Values.getInteger(this, "expectedResultsPerGroupMax");
	}

	public void setExpectedResultsPerGroupMax(Integer expectedResultsPerGroupMax) {
		Values.setInteger(this, "expectedResultsPerGroupMax", expectedResultsPerGroupMax);
	}

	public Integer getExpectedResultsPerSpecificObjectiveMax() {
		return Values.getInteger(this, "expectedResultsPerSpecificObjectiveMax");
	}

	public void setExpectedResultsPerSpecificObjectiveMax(Integer expectedResultsPerSpecificObjectiveMax) {
		Values.setInteger(this, "expectedResultsPerSpecificObjectiveMax", expectedResultsPerSpecificObjectiveMax);
	}

	public native boolean isEnableActivitiesGroups() /*-{
		return this.enableActivitiesGroups;
	}-*/;

	public native void setEnableActivitiesGroups(boolean enableActivitiesGroups) /*-{
		this.enableActivitiesGroups = enableActivitiesGroups;
	}-*/;

	public Integer getActivitiesMax() {
		return Values.getInteger(this, "activitiesMax");
	}

	public void setActivitiesMax(Integer activitiesMax) {
		Values.setInteger(this, "activitiesMax", activitiesMax);
	}

	public Integer getActivitiesGroupsMax() {
		return Values.getInteger(this, "activitiesGroupsMax");
	}

	public void setActivitiesGroupsMax(Integer activitiesGroupsMax) {
		Values.setInteger(this, "activitiesGroupsMax", activitiesGroupsMax);
	}

	public Integer getActivitiesPerGroupMax() {
		return Values.getInteger(this, "activitiesPerGroupMax");
	}

	public void setActivitiesPerGroupMax(Integer activitiesPerGroupMax) {
		Values.setInteger(this, "activitiesPerGroupMax", activitiesPerGroupMax);
	}

	public Integer getActivitiesPerExpectedResultMax() {
		return Values.getInteger(this, "activitiesPerExpectedResultMax");
	}

	public void setActivitiesPerExpectedResultMax(Integer activitiesPerExpectedResultMax) {
		Values.setInteger(this, "activitiesPerExpectedResultMax", activitiesPerExpectedResultMax);
	}

	public native boolean isEnablePrerequisitesGroups() /*-{
		return this.enablePrerequisitesGroups;
	}-*/;

	public native void setEnablePrerequisitesGroups(boolean enablePrerequisitesGroups) /*-{
		this.enablePrerequisitesGroups = enablePrerequisitesGroups;
	}-*/;

	public Integer getPrerequisitesMax() {
		return Values.getInteger(this, "prerequisitesMax");
	}

	public void setPrerequisitesMax(Integer prerequisitesMax) {
		Values.setInteger(this, "prerequisitesMax", prerequisitesMax);
	}

	public Integer getPrerequisitesGroupsMax() {
		return Values.getInteger(this, "prerequisitesGroupsMax");
	}

	public void setPrerequisitesGroupsMax(Integer prerequisitesGroupsMax) {
		Values.setInteger(this, "prerequisitesGroupsMax", prerequisitesGroupsMax);
	}

	public Integer getPrerequisitesPerGroupMax() {
		return Values.getInteger(this, "prerequisitesPerGroupMax");
	}

	public void setPrerequisitesPerGroupMax(Integer prerequisitesPerGroupMax) {
		Values.setInteger(this, "prerequisitesPerGroupMax", prerequisitesPerGroupMax);
	}
}
