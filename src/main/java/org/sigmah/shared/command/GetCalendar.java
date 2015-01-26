package org.sigmah.shared.command;


import org.sigmah.shared.command.base.AbstractCommand;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;
import org.sigmah.shared.dto.calendar.CalendarType;

/**
 * Command used to ask for events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class GetCalendar extends AbstractCommand<Calendar> {

	private CalendarType type;
	private CalendarIdentifier identifier;

	public GetCalendar() {
		// Serialization.
	}

	public GetCalendar(CalendarType type, CalendarIdentifier identifier) {
		this.type = type;
		this.identifier = identifier;
	}

	public CalendarType getType() {
		return type;
	}

	public void setType(CalendarType type) {
		this.type = type;
	}

	public CalendarIdentifier getIdentifier() {
		return identifier;
	}

	public void setIdentifier(CalendarIdentifier identifier) {
		this.identifier = identifier;
	}
}
