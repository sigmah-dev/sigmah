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

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.PersonalCalendarAsyncDAO;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.dispatch.UnavailableCommandException;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.Delete;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.dto.value.ListableValue;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.GetRemindersHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */

public class DeleteAsyncHandler implements AsyncCommandHandler<Delete, VoidResult>, DispatchListener<Delete, VoidResult> {
	
	
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	

	private ValueAsyncDAO valueAsyncDAO;
	
	private PersonalCalendarAsyncDAO personalCalendarAsyncDAO;
	
	private final Map<String, String> entityNameMap;
	
	

	public DeleteAsyncHandler(ClientFactory factory) {
		this.updateDiaryAsyncDAO = factory.getUpdateDiaryAsyncDAO();
		this.valueAsyncDAO = factory.getValueAsyncDAO();
		this.personalCalendarAsyncDAO = factory.getPersonalCalendarAsyncDAO();
		entityNameMap = new HashMap<String, String>();
		entityNameMap.put(FileDTO.ENTITY_NAME, FilesListElementDTO.ENTITY_NAME);
		entityNameMap.put(FileVersionDTO.ENTITY_NAME, FilesListElementDTO.ENTITY_NAME);
	}

	
	@Override
	public void execute(final Delete command, OfflineExecutionContext executionContext, AsyncCallback<VoidResult> callback) {
		executeCommand(command, callback);
		updateDiaryAsyncDAO.saveOrUpdate(command);
	}

	@Override
	public void onSuccess(Delete command, VoidResult result, Authentication authentication) {
		executeCommand(command, null);
	}
	
	private void executeCommand(Delete command, AsyncCallback<VoidResult> callback) {
		if(FileDTO.ENTITY_NAME.equals(command.getEntityName())) {
			deleteFileDTO(command, callback);
			
		} else if(FileVersionDTO.ENTITY_NAME.equals(command.getEntityName())) {
			deleteFileVersionDTO(command, callback);
			
		} else if(PersonalEventDTO.ENTITY_NAME.equals(command.getEntityName())) {
			deletePersonalEvent(command, callback);
			
		} else if(ProjectReportDTO.ENTITY_NAME.equals(command.getEntityName())) {
			// TODO: Add support for ProjectReports
			exception(command, callback != null);
			
		} else {
			exception(command, callback != null);
		}
	}
	
	private void exception(Delete command, boolean throwException) throws UnsupportedOperationException {
		if(throwException) {
			throw new UnavailableCommandException("Deletion of type '" + command.getEntityName() + "' is not supported yet.");
		}
	}
	
	private String getEntityName(Delete command) {
		final String mapping = entityNameMap.get(command.getEntityName());
		return mapping != null ? mapping : command.getEntityName();
	}
	
	private String getOfflineId(Delete command) {
		return ValueJSIdentifierFactory.toIdentifier(
			getEntityName(command), 
			command.getProjectId(), 
			command.getElementId(), null);
	}
	
	private void deleteFileDTO(final Delete command, final AsyncCallback<VoidResult> callback) {
		valueAsyncDAO.get(getOfflineId(command), new AsyncCallback<ValueResult>() {

			@Override
			public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(ValueResult result) {
				if (result == null || result.getValuesObject() == null)  {
					Log.warn("Delete not done. Value not found for element '" + getOfflineId(command) + "'.");
					if(callback != null) {
						callback.onSuccess(null);
					}
					return;
				}
				
				final Iterator<ListableValue> iterator = result.getValuesObject().iterator();
				while(iterator.hasNext()) {
					final ListableValue value = iterator.next();
					if(value instanceof FileDTO) {
						final FileDTO file = (FileDTO)value;
						if(file.getId().equals(command.getId())) {
							iterator.remove();
						}
					}
				}

				valueAsyncDAO.saveOrUpdate(new GetValue(command.getProjectId(), command.getElementId(), getEntityName(command)), result, callback);
			}
		});
	}
	
	private void deleteFileVersionDTO(final Delete command, final AsyncCallback<VoidResult> callback) {
		valueAsyncDAO.get(getOfflineId(command), new AsyncCallback<ValueResult>() {

			@Override
			public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(ValueResult result) {
				if (result == null || result.getValuesObject() == null)  {
					Log.warn("Delete not done. Value not found for element '" + getOfflineId(command) + "'.");
					if(callback != null) {
						callback.onSuccess(null);
					}
					return;
				}
				
				for (final ListableValue value : result.getValuesObject()) {
					if (value instanceof FileDTO) {
						final FileDTO file = (FileDTO)value;

						final Iterator<FileVersionDTO> iterator = file.getVersions().iterator();
						while (iterator.hasNext()) {
							final FileVersionDTO version = iterator.next();
							if (version.getId().equals(command.getId())) {
								iterator.remove();
							}
						}
					}
				}

				valueAsyncDAO.saveOrUpdate(new GetValue(command.getProjectId(), command.getElementId(), getEntityName(command)), result, callback);
			}
		});
	}
	
	private void deletePersonalEvent(final Delete command, final AsyncCallback<VoidResult> callback) {
		personalCalendarAsyncDAO.get(command.getParentId(), new AsyncCallback<Calendar>() {

			@Override
			public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
			}

			@Override
			public void onSuccess(Calendar result) {
				for(final Map.Entry<Date, List<Event>> entry : result.getEvents().entrySet()) {
					final Iterator<Event> iterator = entry.getValue().iterator();
					while(iterator.hasNext()) {
						final Event event = iterator.next();
						if(event.getIdentifier() != null && event.getIdentifier().equals(command.getId())) {
							iterator.remove();
						}
					}
				}
				
				final AsyncCallback<Calendar> calendarCallback = callback != null ?
					new AsyncCallback<Calendar>() {

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}

					@Override
					public void onSuccess(Calendar result) {
						callback.onSuccess(null);
					}
				} : null;
				
				personalCalendarAsyncDAO.saveOrUpdate(result, calendarCallback);
			}
		});
	}
}
