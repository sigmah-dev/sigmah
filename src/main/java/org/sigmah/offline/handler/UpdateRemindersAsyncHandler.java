package org.sigmah.offline.handler;

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
public class UpdateRemindersAsyncHandler implements AsyncCommandHandler<UpdateReminders, ListResult<ReminderDTO>>, DispatchListener<UpdateReminders, ListResult<ReminderDTO>> {

	private ReminderAsyncDAO reminderAsyncDAO;
	
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	
	public UpdateRemindersAsyncHandler(ReminderAsyncDAO reminderAsyncDAO, UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
		this.reminderAsyncDAO = reminderAsyncDAO;
		this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
	}

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
