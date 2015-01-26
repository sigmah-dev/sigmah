package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.PersonalCalendarAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.GetCalendar;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.calendar.PersonalCalendarHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PersonalCalendarAsyncHandler implements AsyncCommandHandler<GetCalendar, Calendar>, DispatchListener<GetCalendar, Calendar> {
	
	private final PersonalCalendarAsyncDAO personalCalendarAsyncDAO;

	@Inject
	public PersonalCalendarAsyncHandler(PersonalCalendarAsyncDAO personalCalendarAsyncDAO) {
		this.personalCalendarAsyncDAO = personalCalendarAsyncDAO;
	}

	@Override
	public void execute(GetCalendar command, OfflineExecutionContext executionContext, AsyncCallback<Calendar> callback) {
		final PersonalCalendarIdentifier identifier = (PersonalCalendarIdentifier)command.getIdentifier();
		personalCalendarAsyncDAO.get(identifier.getId(), callback);
	}
	
	@Override
	public void onSuccess(GetCalendar command, Calendar result, Authentication authentication) {
		personalCalendarAsyncDAO.saveOrUpdate(result);
	}
	
}
