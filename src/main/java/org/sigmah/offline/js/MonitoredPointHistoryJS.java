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

import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointHistoryDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class MonitoredPointHistoryJS extends JavaScriptObject {
	
	protected MonitoredPointHistoryJS() {
	}
	
	public static MonitoredPointHistoryJS toJavaScript(MonitoredPointHistoryDTO monitoredPointHistoryDTO) {
		final MonitoredPointHistoryJS monitoredPointHistoryJS = Values.createJavaScriptObject(MonitoredPointHistoryJS.class);
		
		monitoredPointHistoryJS.setId(monitoredPointHistoryDTO.getId());
		monitoredPointHistoryJS.setDate(Values.toJsDate(monitoredPointHistoryDTO.getDate()));
		monitoredPointHistoryJS.setValue(monitoredPointHistoryDTO.getValue());
		monitoredPointHistoryJS.setUserId(monitoredPointHistoryJS.getUserId());
		monitoredPointHistoryJS.setType(monitoredPointHistoryDTO.getType());
		monitoredPointHistoryJS.setMonitoredPoint(monitoredPointHistoryDTO.getMonitoredPoint());
		
		return monitoredPointHistoryJS;
	}
	
	public MonitoredPointHistoryDTO toDTO() {
		final MonitoredPointHistoryDTO monitoredPointHistoryDTO = new MonitoredPointHistoryDTO();
		
		monitoredPointHistoryDTO.setId(getId());
		monitoredPointHistoryDTO.setDate(Values.toDate(getDate()));
		monitoredPointHistoryDTO.setValue(getValue());
		monitoredPointHistoryDTO.setUserId(getUserId());
		if(getType() != null) {
			monitoredPointHistoryDTO.setType(ReminderChangeType.valueOf(getType()));
		}
		
		return monitoredPointHistoryDTO;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;

	public native JsDate getDate() /*-{
		return this.date;
	}-*/;

	public native void setDate(JsDate date) /*-{
		this.date = date;
	}-*/;

	public native String getValue() /*-{
		return this.value;
	}-*/;

	public native void setValue(String value) /*-{
		this.value = value;
	}-*/;

	public native int getUserId() /*-{
		return this.userId;
	}-*/;

	public native void setUserId(int userId) /*-{
		this.userId = userId;
	}-*/;

	public native String getType() /*-{
		return this.type;
	}-*/;

	public void setType(ReminderChangeType type) {
		if(type != null) {
			setType(type.name());
		}
	}
	
	public native void setType(String type) /*-{
		this.type = type;
	}-*/;

	public native int getMonitoredPoint() /*-{
		return this.monitoredPoint;
	}-*/;

	public void setMonitoredPoint(MonitoredPointDTO monitoredPoint) {
		if(monitoredPoint != null) {
			setMonitoredPoint(monitoredPoint.getId());
		}
	}
	
	public native void setMonitoredPoint(int monitoredPoint) /*-{
		this.monitoredPoint = monitoredPoint;
	}-*/;
}
