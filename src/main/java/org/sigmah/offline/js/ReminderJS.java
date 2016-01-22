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

import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ReminderJS extends JavaScriptObject {
	
	protected ReminderJS() {
	}
	
	public static ReminderJS toJavaScript(ReminderDTO reminderDTO) {
		final ReminderJS reminderJS = (ReminderJS)JavaScriptObject.createObject();
		
		reminderJS.setId(reminderDTO.getId());
		reminderJS.setParentListId(reminderDTO.getParentListId());
		reminderJS.setLabel(reminderDTO.getLabel());
		reminderJS.setExpectedDate(Values.toJsDate(reminderDTO.getExpectedDate()));
		reminderJS.setCompletionDate(Values.toJsDate(reminderDTO.getCompletionDate()));
		reminderJS.setDeleted(reminderDTO.getDeleted());
        if(reminderDTO.getCurrentMappingMode() == ReminderDTO.Mode.WITH_HISTORY) {
            reminderJS.setHistory(reminderDTO.getHistory());
        }
		
		return reminderJS;
	}
	
	public ReminderDTO toDTO() {
		final ReminderDTO reminderDTO = new ReminderDTO();
		
		reminderDTO.setId(getId());
		reminderDTO.setParentListId(getParentListId());
		reminderDTO.setLabel(getLabel());
		reminderDTO.setExpectedDate(Values.toDate(getExpectedDate()));
		reminderDTO.setCompletionDate(Values.toDate(getCompletionDate()));
		reminderDTO.setDeleted(isDeleted());
		
        final ArrayList<ReminderHistoryDTO> dtos = new ArrayList<ReminderHistoryDTO>();
        reminderDTO.setHistory(dtos);
        
		final JsArray<ReminderHistoryJS> history = getHistory();
		if(history != null) {
			for(int index = 0; index < history.length(); index++) {
				final ReminderHistoryDTO dto = history.get(index).toDTO();
				dto.setReminder(reminderDTO);
				dtos.add(dto);
			}
		}
		
		return reminderDTO;
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

	public native boolean isDeleted() /*-{
		return this.deleted;
	}-*/;

	public native void setDeleted(boolean deleted) /*-{
		this.deleted = deleted;
	}-*/;

	public native JsArray<ReminderHistoryJS> getHistory() /*-{
		return this.history;
	}-*/;

	public void setHistory(List<ReminderHistoryDTO> history) {
		if(history != null) {
			final JsArray<ReminderHistoryJS> array = (JsArray<ReminderHistoryJS>) JavaScriptObject.createArray();
			
			for(final ReminderHistoryDTO entry : history) {
				array.push(ReminderHistoryJS.toJavaScript(entry));
			}
			
			setHistory(array);
		}
	}
	
	public native void setHistory(JsArray<ReminderHistoryJS> history) /*-{
		this.history = history;
	}-*/;
}
