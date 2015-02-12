package org.sigmah.offline.js;

import com.google.gwt.core.client.JsArray;
import java.util.ArrayList;
import java.util.List;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * JavaScript version of the {@link UpdateReminders} command.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class UpdateRemindersJS extends CommandJS {
	
	protected UpdateRemindersJS() {
	}
	
	public static UpdateRemindersJS toJavaScript(UpdateReminders updateReminders) {
		final UpdateRemindersJS updateRemindersJS = Values.createJavaScriptObject(UpdateRemindersJS.class);
		
		updateRemindersJS.setList(updateReminders.getList());
		
		return updateRemindersJS;
	}
	
	public UpdateReminders toUpdateReminders() {
		return new UpdateReminders(getList());
	}
	
	public native JsArray<ReminderJS> getArray() /*-{
		return this.list;
	}-*/;
	
	public native void setArray(JsArray<ReminderJS> list) /*-{
		this.list = list;
	}-*/;
	
	public List<ReminderDTO> getList() {
		if(getArray() != null) {
			final ArrayList<ReminderDTO> list = new ArrayList<ReminderDTO>();
			
			final JsArray<ReminderJS> array = getArray();
			for(int index = 0; index < array.length(); index++) {
				list.add(array.get(index).toDTO());
			}
			
			return list;
		}
		return null;
	}
	
	public void setList(List<ReminderDTO> list) {
		if(list != null) {
			final JsArray<ReminderJS> array = Values.createJavaScriptArray(JsArray.class);
			
			for(final ReminderDTO reminder : list) {
				array.push(ReminderJS.toJavaScript(reminder));
			}
			
			setArray(array);
		}
	}
}
