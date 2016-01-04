package org.sigmah.offline.handler;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.shared.command.UpdateReminders;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.ListResult;
import org.sigmah.shared.dto.reminder.ReminderDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.UpdateRemindersHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class UpdateRemindersAsyncHandler implements AsyncCommandHandler<UpdateReminders, ListResult<ReminderDTO>>, DispatchListener<UpdateReminders, ListResult<ReminderDTO>> {

	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;
	
	@Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(final UpdateReminders command, OfflineExecutionContext executionContext, final AsyncCallback<ListResult<ReminderDTO>> callback) {
		reminderAsyncDAO.saveAll(command.getList(), new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
				callback.onSuccess(new ListResult<ReminderDTO>(command.getList()));
			}
		});
		
		updateDiaryAsyncDAO.saveOrUpdate(command);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSuccess(UpdateReminders command, ListResult<ReminderDTO> result, Authentication authentication) {
		reminderAsyncDAO.saveAll(result, null);
	}
	
}
