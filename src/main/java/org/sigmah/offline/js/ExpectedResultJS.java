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
import java.util.Map;

import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.logframe.ExpectedResultDTO;
import org.sigmah.shared.dto.logframe.LogFrameActivityDTO;
import org.sigmah.shared.dto.logframe.LogFrameGroupDTO;
import org.sigmah.shared.dto.logframe.SpecificObjectiveDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ExpectedResultJS extends JavaScriptObject {// LogFrameElementJS {
	
	protected ExpectedResultJS() {
	}
	
	public static ExpectedResultJS toJavaScript(ExpectedResultDTO expectedResultDTO) {
		final ExpectedResultJS expectedResultJS = Values.createJavaScriptObject(ExpectedResultJS.class);
		
		expectedResultJS.setId(expectedResultDTO.getId());
		expectedResultJS.setCode(expectedResultDTO.getCode());
		expectedResultJS.setPosition(expectedResultDTO.getPosition());
		expectedResultJS.setRisksAndAssumptions(expectedResultDTO.getRisksAndAssumptions());
		expectedResultJS.setGroup(expectedResultDTO.getGroup());
		expectedResultJS.setIndicators(expectedResultDTO.getIndicators());
		expectedResultJS.setInterventionLogic(expectedResultDTO.getInterventionLogic());
		expectedResultJS.setActivities(expectedResultDTO.getActivities());
		expectedResultJS.setParentSpecificObjective(expectedResultDTO.getParentSpecificObjective());
		expectedResultJS.setLabel(expectedResultDTO.getLabel());
		
		return expectedResultJS;
	}
	
	public ExpectedResultDTO toDTO(Map<Integer, LogFrameGroupDTO> groupMap) {
		final ExpectedResultDTO expectedResultDTO = new ExpectedResultDTO();
		
		expectedResultDTO.setId(getId());
		expectedResultDTO.setCode(getCode());
		expectedResultDTO.setPosition(getPosition());
		expectedResultDTO.setRisksAndAssumptions(getRisksAndAssumptions());
		expectedResultDTO.setInterventionLogic(getInterventionLogic());
		expectedResultDTO.setActivities(getActivities(expectedResultDTO, groupMap));
		expectedResultDTO.setLabel(getLabel());
		
		return expectedResultDTO;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public Integer getCode() {
		return Values.getInteger(this, "code");
	}

	public void setCode(Integer code) {
		Values.setInteger(this, "code", code);
	}

	public Integer getPosition() {
		return Values.getInteger(this, "position");
	}

	public void setPosition(Integer position) {
		Values.setInteger(this, "position", position);
	}

	public native String getRisksAndAssumptions() /*-{
		return this.risksAndAssumptions;
	}-*/;

	public native void setRisksAndAssumptions(String risksAndAssumptions) /*-{
		this.risksAndAssumptions = risksAndAssumptions;
	}-*/;

	public boolean hasGroup() {
		return Values.isDefined(this, "group");
	}
	
	public native int getGroup() /*-{
		return this.group;
	}-*/;

	public native void setGroup(int group) /*-{
		this.group = group;
	}-*/;
	
	public void setGroup(LogFrameGroupDTO logFrameGroupDTO) {
		if(logFrameGroupDTO != null) {
			setGroup(logFrameGroupDTO.getId());
		}
	}

	public native JsArrayInteger getIndicators() /*-{
		return this.indicators;
	}-*/;

	public native void setIndicators(JsArrayInteger indicators) /*-{
		this.indicators = indicators;
	}-*/;
	
	public void setIndicators(List<IndicatorDTO> indicators) {
		if(indicators != null) {
			final JsArrayInteger array = (JsArrayInteger) JavaScriptObject.createArray();

			for(final IndicatorDTO indicator : indicators) {
				array.push(indicator.getId());
			}

			setIndicators(array);
		}
	}

	public native String getInterventionLogic() /*-{
		return this.interventionLogic;
	}-*/;

	public native void setInterventionLogic(String interventionLogic) /*-{
		this.interventionLogic = interventionLogic;
	}-*/;

	public native JsArray<LogFrameActivityJS> getActivities() /*-{
		return this.activities;
	}-*/;

	public native void setActivities(JsArray<LogFrameActivityJS> activities) /*-{
		this.activities = activities;
	}-*/;
	
	public void setActivities(List<LogFrameActivityDTO> activities) {
		if(activities != null) {
			final JsArray<LogFrameActivityJS> array = (JsArray<LogFrameActivityJS>) JavaScriptObject.createArray();

			for(final LogFrameActivityDTO activity : activities) {
				array.push(LogFrameActivityJS.toJavaScript(activity));
			}
			
			setActivities(array);
		}
	}
	
	public List<LogFrameActivityDTO> getActivities(ExpectedResultDTO parentExpectedResult, Map<Integer, LogFrameGroupDTO> groupMap) {
		final JsArray<LogFrameActivityJS> activities = ExpectedResultJS.this.getActivities();
		if(activities != null) {
			final ArrayList<LogFrameActivityDTO> list = new ArrayList<LogFrameActivityDTO>();
			for(int index = 0; index < activities.length(); index++) {
				final LogFrameActivityJS logFrameActivityJS = activities.get(index);
				final LogFrameActivityDTO logFrameActivityDTO = logFrameActivityJS.toDTO();
				logFrameActivityDTO.setParentExpectedResult(parentExpectedResult);
				if(logFrameActivityJS.hasGroup()) {
					logFrameActivityDTO.setGroup(groupMap.get(logFrameActivityJS.getGroup()));
				}
				list.add(logFrameActivityDTO);
			}
			return list;
		}
		return null;
	}

	public native int getParentSpecificObjective() /*-{
		return this.parentSpecificObjective;
	}-*/;

	public native void setParentSpecificObjective(int parentSpecificObjective) /*-{
		this.parentSpecificObjective = parentSpecificObjective;
	}-*/;

	public void setParentSpecificObjective(SpecificObjectiveDTO parentSpecificObjective) {
		if(parentSpecificObjective != null) {
			setParentSpecificObjective(parentSpecificObjective.getId());
		}
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;
}
