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
			final JsArray<ReminderJS> array = Values.createTypedJavaScriptArray(ReminderJS.class);
			
			for(final ReminderDTO reminder : list) {
				array.push(ReminderJS.toJavaScript(reminder));
			}
			
			setArray(array);
		}
	}
}
