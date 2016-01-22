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
import com.google.gwt.core.client.JsDate;
import org.sigmah.shared.dto.calendar.Event;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PersonalEventJS extends JavaScriptObject {
	
	protected PersonalEventJS() {
	}
	
	public static PersonalEventJS toJavaScript(Event event) {
		final PersonalEventJS personalEventJS = (PersonalEventJS)JavaScriptObject.createObject();
		
		personalEventJS.setId(event.getIdentifier());
		personalEventJS.setSummary(event.getSummary());
		personalEventJS.setDescription(event.getDescription());
		personalEventJS.setDtstart(Values.toJsDate(event.getDtstart()));
		personalEventJS.setDtend(Values.toJsDate(event.getDtend()));
		
		return personalEventJS;
	}
	
	public Event toEvent() {
		final Event event = new Event();
		
		event.setIdentifier(getId());
		event.setSummary(getSummary());
		event.setDescription(getDescription());
		event.setDtstart(Values.toDate(getDtstart()));
		event.setDtend(Values.toDate(getDtend()));
		
		return event;
	}

	public Integer getId() {
		return Values.getInteger(this, "id");
	}

	public void setId(Integer id) {
		Values.setInteger(this, "id", id);
	}

	public native String getSummary() /*-{
		return this.summary;
	}-*/;

	public native void setSummary(String summary) /*-{
		this.summary = summary;
	}-*/;

	public native String getDescription() /*-{
		return this.description;
	}-*/;

	public native void setDescription(String description) /*-{
		this.description = description;
	}-*/;

	public native JsDate getDtstart() /*-{
		return this.dtstart;
	}-*/;

	public native void setDtstart(JsDate dtstart) /*-{
		this.dtstart = dtstart;
	}-*/;

	public native JsDate getDtend() /*-{
		return this.dtend;
	}-*/;

	public native void setDtend(JsDate dtend) /*-{
		this.dtend = dtend;
	}-*/;
}
