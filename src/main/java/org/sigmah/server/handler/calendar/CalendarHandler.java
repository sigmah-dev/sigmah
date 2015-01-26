package org.sigmah.server.handler.calendar;


import javax.persistence.EntityManager;

import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.CalendarIdentifier;

/**
 * Describes an utility class for fetching a type of Calendar.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Maxime Lombard (mlombard@ideia.fr)
 */
public interface CalendarHandler {

	Calendar getCalendar(CalendarIdentifier identifier,EntityManager em);

}
