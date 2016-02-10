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

import com.extjs.gxt.ui.client.data.RpcMap;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.sigmah.client.dispatch.DispatchListener;
import org.sigmah.offline.dao.MonitoredPointAsyncDAO;
import org.sigmah.offline.dao.PersonalCalendarAsyncDAO;
import org.sigmah.offline.dao.ProjectAsyncDAO;
import org.sigmah.offline.dao.ReminderAsyncDAO;
import org.sigmah.offline.dao.RequestManager;
import org.sigmah.offline.dao.RequestManagerCallback;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.dispatch.UnavailableCommandException;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.command.result.Calendar;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.calendar.Event;
import org.sigmah.shared.dto.calendar.PersonalCalendarIdentifier;
import org.sigmah.shared.dto.calendar.PersonalEventDTO;
import org.sigmah.shared.dto.referential.ReminderChangeType;
import org.sigmah.shared.dto.reminder.MonitoredPointDTO;
import org.sigmah.shared.dto.reminder.MonitoredPointHistoryDTO;
import org.sigmah.shared.dto.reminder.ReminderDTO;
import org.sigmah.shared.dto.reminder.ReminderHistoryDTO;
import org.sigmah.shared.dto.report.ProjectReportDTO;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.CreateEntityHandler}.
 * Used when the user is offline.
 * <p/>
 * Server-side, this handler is used as a generic handler to create entities.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class CreateEntityAsyncHandler implements AsyncCommandHandler<CreateEntity, CreateResult>, DispatchListener<CreateEntity, CreateResult> {

	@Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

	@Inject
	private ProjectAsyncDAO projectAsyncDAO;
	
	@Inject
	private PersonalCalendarAsyncDAO personalCalendarAsyncDAO;
	
	@Inject
	private MonitoredPointAsyncDAO monitoredPointAsyncDAO;
	
	@Inject
	private ReminderAsyncDAO reminderAsyncDAO;
	
	@Override
	public void execute(CreateEntity command, OfflineExecutionContext executionContext, final AsyncCallback<CreateResult> callback) {
		executeCommand(command, null, executionContext.getAuthentication(), callback);
		updateDiaryAsyncDAO.saveOrUpdate(command);
	}

	@Override
	public void onSuccess(CreateEntity command, CreateResult result, Authentication authentication) {
		executeCommand(command, result, authentication, null);
	}
	
	private void executeCommand(CreateEntity command, CreateResult result, Authentication authentication, AsyncCallback<CreateResult> callback) {
		if(ProjectDTO.ENTITY_NAME.equals(command.getEntityName())) {
			exception(command, callback != null);
			
		} else if(PersonalEventDTO.ENTITY_NAME.equals(command.getEntityName())) {
			insertPersonalEvent(command, callback);
			
		} else if(ProjectReportDTO.ENTITY_NAME.equals(command.getEntityName())) {
			exception(command, callback != null);
			
		} else if(ReminderDTO.ENTITY_NAME.equals(command.getEntityName())) {
			if(result != null) {
				reminderAsyncDAO.saveOrUpdate((ReminderDTO)result.getEntity());
			} else {
				insertReminder(command, authentication, callback);
			}
			
		} else if(MonitoredPointDTO.ENTITY_NAME.equals(command.getEntityName())) {
			if(result != null) {
				monitoredPointAsyncDAO.saveOrUpdate((MonitoredPointDTO)result.getEntity());
			} else {
				insertMonitoredPoint(command, authentication, callback);
			}
			
		} else {
			exception(command, callback != null);
		}
	}
	
	private void exception(CreateEntity command, boolean throwException) throws UnavailableCommandException {
		if(throwException) {
			throw new UnavailableCommandException("Creation of type '" + command.getEntityName() + "' is not supported yet.");
		}
	}
	
	private void insertPersonalEvent(CreateEntity command, final AsyncCallback<CreateResult> callback) {
		final Event event = buildPersonalEvent(command.getProperties());
		final PersonalCalendarIdentifier identifier = (PersonalCalendarIdentifier) event.getParent().getIdentifier();
		
		final Date key = (Date) command.getProperties().get(Event.DATE);
		
		// Loading the parent calendar.
		personalCalendarAsyncDAO.get(identifier.getId(), new AsyncCallback<Calendar>() {
			
			@Override
			public void onFailure(Throwable caught) {
				if(callback != null) {
					callback.onFailure(caught);
				}
			}
			
			@Override
			public void onSuccess(Calendar result) {
				List<Event> events = result.getEvents().get(key);
				if(events == null) {
					events = new ArrayList<Event>();
					result.getEvents().put(key, events);
				}
				events.add(event);
				
				// Creating the callback, if necessary.
				final AsyncCallback<Calendar> calendarCallback;
				if(callback != null) {
					calendarCallback = new AsyncCallback<Calendar>() {

						@Override
						public void onFailure(Throwable caught) {
							callback.onFailure(caught);
						}

						@Override
						public void onSuccess(Calendar result) {
							callback.onSuccess(new CreateResult(new PersonalEventDTO()));
						}
					};
				} else {
					calendarCallback = null;
				}
				
				// Persisting the new event.
				personalCalendarAsyncDAO.saveOrUpdate(result, calendarCallback);
			}
		});
	}
	
	private Event buildPersonalEvent(RpcMap properties) {
		final Event event = new Event();
		event.fillValues(properties.getTransientMap());
		
		return event;
	}
	
	private void insertReminder(final CreateEntity command, final Authentication authentication, final AsyncCallback<CreateResult> callback) {
		final ReminderDTO reminder = buildReminderDTO(command.getProperties(), authentication);
		
		final RequestManager<CreateResult> requestManager = new RequestManager<CreateResult>(new CreateResult(reminder), callback);
		
		final int futureRequest = requestManager.prepareRequest();
		final RequestManager<ReminderDTO> objectRequestManager = new RequestManager<ReminderDTO>(reminder, new RequestManagerCallback<CreateResult, ReminderDTO>(requestManager) {
			
			@Override
			public void onRequestSuccess(ReminderDTO result) {
				reminderAsyncDAO.saveOrUpdate(result, new RequestManagerCallback<CreateResult, ReminderDTO>(requestManager, futureRequest) {
					
					@Override
					public void onRequestSuccess(ReminderDTO result) {
						// Reminder has been saved successfully.
					}
				});
			}
		});
		
		// Retrieves project.
		final Integer projectId = (Integer) command.getProperties().get(ReminderDTO.PROJECT_ID);
		projectAsyncDAO.get(projectId, new RequestManagerCallback<ReminderDTO, ProjectDTO>(objectRequestManager) {

			@Override
			public void onRequestSuccess(ProjectDTO result) {
				final Integer remindersListId = result.getRemindersList().getId();
				reminder.setParentListId(remindersListId);
			}
		});
		
		// Generates a negative ID
		reminderAsyncDAO.generateNegativeId(new RequestManagerCallback<ReminderDTO, Integer>(objectRequestManager) {
			
			@Override
			public void onRequestSuccess(Integer result) {
				reminder.setId(result);
			}
		});
		
		objectRequestManager.ready();
		requestManager.ready();
	}
	
	private ReminderDTO buildReminderDTO(RpcMap properties, Authentication authentication) {
		final ReminderDTO reminder = new ReminderDTO();
		
		// Retrieves parameters.
		final Long expectedDate = (Long) properties.get(ReminderDTO.EXPECTED_DATE);
		final String label = (String) properties.get(ReminderDTO.LABEL);
		
		// Creates point.
		reminder.setLabel(label);
		reminder.setExpectedDate(new Date(expectedDate));
		reminder.setCompletionDate(null);
		reminder.setDeleted(false);

		final ArrayList<ReminderHistoryDTO> historyList = new ArrayList<ReminderHistoryDTO>();
		final ReminderHistoryDTO history = new ReminderHistoryDTO();
		history.setDate(new Date());
		history.setType(ReminderChangeType.CREATED);
		history.setUserId(authentication.getUserId());
		history.setValue(authentication.getUserName() + ", " + authentication.getUserFirstName() + " <" + authentication.getUserEmail() + ">");
		historyList.add(history);
		
		reminder.setHistory(historyList);
		
		return reminder;
	}
	
	private void insertMonitoredPoint(final CreateEntity command, final Authentication authentication, final AsyncCallback<CreateResult> callback) {
		final MonitoredPointDTO monitoredPointDTO = buildMonitoredPointDTO(command.getProperties(), authentication);
		
		final RequestManager<CreateResult> requestManager = new RequestManager<CreateResult>(new CreateResult(monitoredPointDTO), callback);
		
		final int futureRequest = requestManager.prepareRequest();
		final RequestManager<MonitoredPointDTO> objectRequestManager = new RequestManager<MonitoredPointDTO>(monitoredPointDTO, new RequestManagerCallback<CreateResult, MonitoredPointDTO>(requestManager) {
			
			@Override
			public void onRequestSuccess(MonitoredPointDTO result) {
				monitoredPointAsyncDAO.saveOrUpdate(result, new RequestManagerCallback<CreateResult, MonitoredPointDTO>(requestManager, futureRequest) {
					
					@Override
					public void onRequestSuccess(MonitoredPointDTO result) {
						// Monitored Point has been saved successfully.
					}
				});
			}
		});
		
		// Retrieves project.
		final Integer projectId = (Integer) command.getProperties().get(ReminderDTO.PROJECT_ID);
		projectAsyncDAO.get(projectId, new RequestManagerCallback<MonitoredPointDTO, ProjectDTO>(objectRequestManager) {

			@Override
			public void onRequestSuccess(ProjectDTO result) {
				final Integer pointsListId = result.getPointsList().getId();
				monitoredPointDTO.setParentListId(pointsListId);
			}
		});
		
		// Generates a negative ID
		monitoredPointAsyncDAO.generateNegativeId(new RequestManagerCallback<MonitoredPointDTO, Integer>(objectRequestManager) {
			
			@Override
			public void onRequestSuccess(Integer result) {
				monitoredPointDTO.setId(result);
			}
		});
		
		objectRequestManager.ready();
		requestManager.ready();
	}
	
	private MonitoredPointDTO buildMonitoredPointDTO(RpcMap properties, Authentication authentication) {
		final MonitoredPointDTO monitoredPoint = new MonitoredPointDTO();
		
		// Retrieves parameters.
		final Long expectedDate = (Long) properties.get(MonitoredPointDTO.EXPECTED_DATE);
		final String label = (String) properties.get(MonitoredPointDTO.LABEL);
		
		// Creates point.
		monitoredPoint.setLabel(label);
		monitoredPoint.setExpectedDate(new Date(expectedDate));
		monitoredPoint.setCompletionDate(null);
		monitoredPoint.setDeleted(false);

		final ArrayList<MonitoredPointHistoryDTO> historyList = new ArrayList<MonitoredPointHistoryDTO>();
		final MonitoredPointHistoryDTO history = new MonitoredPointHistoryDTO();
		history.setDate(new Date());
		history.setType(ReminderChangeType.CREATED);
		history.setUserId(authentication.getUserId());
		history.setValue(authentication.getUserName() + ", " + authentication.getUserFirstName() + " <" + authentication.getUserEmail() + ">");
		historyList.add(history);
		
		monitoredPoint.setHistory(historyList);
		
		return monitoredPoint;
	}
}
