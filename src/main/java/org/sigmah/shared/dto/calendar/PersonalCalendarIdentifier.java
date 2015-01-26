package org.sigmah.shared.dto.calendar;

/**
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PersonalCalendarIdentifier implements CalendarIdentifier {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -189998566249155621L;

	private int id;

	public PersonalCalendarIdentifier() {
	}

	public PersonalCalendarIdentifier(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
