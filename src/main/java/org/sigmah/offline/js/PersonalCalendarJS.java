package org.sigmah.offline.js;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class PersonalCalendarJS extends JavaScriptObject {
	
	protected PersonalCalendarJS() {
	}
	
	public static PersonalCalendarJS toJavaScript(Calendar calendar) {
		final PersonalCalendarJS personalCalendarJS = Values.createJavaScriptObject(PersonalCalendarJS.class);
		
		personalCalendarJS.setId(calendar.getIdentifier());
		personalCalendarJS.setName(calendar.getName());
		personalCalendarJS.setEvents(calendar.getEvents());
		
		return personalCalendarJS;
	}
	
	public Calendar toCalendar() {
		final Calendar calendar = new Calendar();
		
		calendar.setIdentifier(new PersonalCalendarIdentifier(getId()));
		calendar.setName(getName());
		calendar.setEvents(getEventsByDate(calendar));
		calendar.setEditable(true);
		
		return calendar;
	}
	
	public native int getId() /*-{
		return this.id;
	}-*/;
	
	public native void setId(int id) /*-{
		this.id = id;
	}-*/;
	public void setId(CalendarIdentifier identifier) {
		if(identifier instanceof PersonalCalendarIdentifier) {
			final PersonalCalendarIdentifier personalCalendarIdentifier = (PersonalCalendarIdentifier) identifier;
			setId(personalCalendarIdentifier.getId());
		}
	}
	
	public native String getName() /*-{
		return this.name;
	}-*/;
			
	public native void setName(String name) /*-{
		this.name = name;
	}-*/;

	public native JsArray<PersonalEventJS> getEvents() /*-{
		return this.events;
	}-*/;

	public Map<Date, List<Event>> getEventsByDate(Calendar parent) {
		final Map<Date, List<Event>> eventsByDate;
		
		final JsArray<PersonalEventJS> events = getEvents();
		if(events != null) {
			eventsByDate = new HashMap<Date, List<Event>>();
			for(int index = 0; index < events.length(); index++) {
				final Event event = events.get(index).toEvent();
				event.setParent(parent);
				
				final Date key = normalize(event.getDtstart());
				
				List<Event> eventList = eventsByDate.get(key);
				if(eventList == null) {
					eventList = new ArrayList<Event>();
					eventsByDate.put(key, eventList);
				}
				eventList.add(event);
			}
			
		} else {
			eventsByDate = null;
		}
		
		return eventsByDate;
	}
	
	private Date normalize(Date date) {
        return new Date(date.getYear(), date.getMonth(), date.getDate());
    }

	public native void setEvents(JsArray<PersonalEventJS> events) /*-{
		this.events = events;
	}-*/;
	
	public void setEvents(Map<Date, List<Event>> eventsByDates) {
		if(eventsByDates != null) {
			final JsArray<PersonalEventJS> array = (JsArray<PersonalEventJS>) JavaScriptObject.createArray();

			for(final List<Event> events : eventsByDates.values()) {
				for(final Event event : events) {
					array.push(PersonalEventJS.toJavaScript(event));
				}
			}
			
			setEvents(array);
		}
	}
}
