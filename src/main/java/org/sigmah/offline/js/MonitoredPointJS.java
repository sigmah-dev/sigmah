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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointHistoryDTO;
import org.sigmah.shared.dto.value.FileDTO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class MonitoredPointJS extends JavaScriptObject {
	
	protected MonitoredPointJS() {
	}
	
	public static MonitoredPointJS toJavaScript(MonitoredPointDTO monitoredPointDTO) {
		final MonitoredPointJS monitoredPointJS = Values.createJavaScriptObject(MonitoredPointJS.class);
		
		monitoredPointJS.setId(monitoredPointDTO.getId());
		monitoredPointJS.setParentListId(monitoredPointDTO.getParentListId());
		monitoredPointJS.setLabel(monitoredPointDTO.getLabel());
		monitoredPointJS.setExpectedDate(Values.toJsDate(monitoredPointDTO.getExpectedDate()));
		monitoredPointJS.setCompletionDate(Values.toJsDate(monitoredPointDTO.getCompletionDate()));
        if(monitoredPointDTO.getCurrentMappingMode() == null || monitoredPointDTO.getCurrentMappingMode() == MonitoredPointDTO.Mode.FULL) {
            monitoredPointJS.setFile(monitoredPointDTO.getFile());
        }
		monitoredPointJS.setDeleted(monitoredPointDTO.getDeleted());
        if(monitoredPointDTO.getCurrentMappingMode() == MonitoredPointDTO.Mode.WITH_HISTORY) {
            monitoredPointJS.setHistory(monitoredPointDTO.getHistory());
        }
		
		return monitoredPointJS;
	}
	
	public MonitoredPointDTO toDTO() {
		final MonitoredPointDTO monitoredPointDTO = new MonitoredPointDTO();
		
		monitoredPointDTO.setId(getId());
		monitoredPointDTO.setParentListId(getParentListId());
		monitoredPointDTO.setLabel(getLabel());
		monitoredPointDTO.setExpectedDate(Values.toDate(getExpectedDate()));
		monitoredPointDTO.setCompletionDate(Values.toDate(getCompletionDate()));
		monitoredPointDTO.setDeleted(isDeleted());
		
        final ArrayList<MonitoredPointHistoryDTO> dtos = new ArrayList<MonitoredPointHistoryDTO>();
        monitoredPointDTO.setHistory(dtos);
        
		final JsArray<MonitoredPointHistoryJS> history = getHistory();
		if(history != null) {
			for(int index = 0; index < history.length(); index++) {
				final MonitoredPointHistoryDTO dto = history.get(index).toDTO();
				dto.setMonitoredPoint(monitoredPointDTO);
				dtos.add(dto);
			}
		}
		
		return monitoredPointDTO;
	}

	public native int getId() /*-{
		return this.id;
	}-*/;

	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	
	public Integer getParentListId() {
		return Values.getInteger(this, "parentListId");
	}
	
	public void setParentListId(Integer parentListId) {
		Values.setInteger(this, "parentListId", parentListId);
	}

	public native String getLabel() /*-{
		return this.label;
	}-*/;

	public native void setLabel(String label) /*-{
		this.label = label;
	}-*/;

	public native JsDate getExpectedDate() /*-{
		return this.expectedDate;
	}-*/;

	public native void setExpectedDate(JsDate expectedDate) /*-{
		this.expectedDate = expectedDate;
	}-*/;

	public native JsDate getCompletionDate() /*-{
		return this.completionDate;
	}-*/;

	public native void setCompletionDate(JsDate completionDate) /*-{
		this.completionDate = completionDate;
	}-*/;

	public native int getFile() /*-{
		return this.file;
	}-*/;

	public void setFile(FileDTO file) {
		if(file != null) {
			setFile(file.getId());
		}
	}
	
	public native void setFile(int file) /*-{
		this.file = file;
	}-*/;

	public native boolean isDeleted() /*-{
		return this.deleted;
	}-*/;

	public native void setDeleted(boolean deleted) /*-{
		this.deleted = deleted;
	}-*/;

	public native JsArray<MonitoredPointHistoryJS> getHistory() /*-{
		return this.history;
	}-*/;

	public void setHistory(List<MonitoredPointHistoryDTO> history) {
		if(history != null) {
			final JsArray<MonitoredPointHistoryJS> array = (JsArray<MonitoredPointHistoryJS>) JavaScriptObject.createArray();
			
			for(final MonitoredPointHistoryDTO entry : history) {
				array.push(MonitoredPointHistoryJS.toJavaScript(entry));
			}
			
			setHistory(array);
		}
	}
	
	public native void setHistory(JsArray<MonitoredPointHistoryJS> history) /*-{
		this.history = history;
	}-*/;
}
