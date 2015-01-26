package org.sigmah.offline.js;

import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsDate;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class ReminderHistoryJS extends JavaScriptObject {
	
	protected ReminderHistoryJS() {
	}
	
	public static ReminderHistoryJS toJavaScript(ReminderHistoryDTO reminderHistoryDTO) {
		final ReminderHistoryJS monitoredPointHistoryJS = Values.createJavaScriptObject(ReminderHistoryJS.class);
		
		monitoredPointHistoryJS.setId(reminderHistoryDTO.getId());
		monitoredPointHistoryJS.setDate(Values.toJsDate(reminderHistoryDTO.getDate()));
		monitoredPointHistoryJS.setValue(reminderHistoryDTO.getValue());
		monitoredPointHistoryJS.setUserId(monitoredPointHistoryJS.getUserId());
		monitoredPointHistoryJS.setType(reminderHistoryDTO.getType());
		monitoredPointHistoryJS.setReminder(reminderHistoryDTO.getReminder());
		
		return monitoredPointHistoryJS;
	}
	
	public ReminderHistoryDTO toDTO() {
		final ReminderHistoryDTO reminderHistoryDTO = new ReminderHistoryDTO();
		
		reminderHistoryDTO.setId(getId());
		reminderHistoryDTO.setDate(Values.toDate(getDate()));
		reminderHistoryDTO.setValue(getValue());
		reminderHistoryDTO.setUserId(getUserId());
		if(getType() != null) {
			reminderHistoryDTO.setType(ReminderChangeType.valueOf(getType()));
		}
		
		return reminderHistoryDTO;
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

	public native int getReminder() /*-{
		return this.reminder;
	}-*/;

	public void setReminder(ReminderDTO reminder) {
		if(reminder != null) {
			setReminder(reminder.getId());
		}
	}
	
	public native void setReminder(int reminder) /*-{
		this.reminder = reminder;
	}-*/;
}
