package org.sigmah.offline.status;

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
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;

/**
 * Poll the network to detect changes in the connection state.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ApplicationStateManager implements OfflineEvent.Source {
	
	public static final int NETWORK_POLLING_INTERVAL = 3000;
	
	/**
	 * Event bus to let other classes know of changes in the application state.
	 */
	private final EventBus eventBus;
	
	/**
	 * DAO to read the content of the UpdateDiary table. Used to know if one
	 * or more changes have been made in offline mode.
	 */
	private final UpdateDiaryAsyncDAO updateDiaryAsyncDAO;
	
	/**
	 * Current application state.
	 */
	private ApplicationState state;
	
	/**
	 * Current network state.
	 */
	private boolean online;
	
	/**
	 * Keep tracks of the first reconnection.
	 */
	private Runnable onReconnection;
	

	public ApplicationStateManager(EventBus eventBus, UpdateDiaryAsyncDAO updateDiaryAsyncDAO) {
		this.eventBus = eventBus;
		this.updateDiaryAsyncDAO = updateDiaryAsyncDAO;
		
		this.state = ApplicationState.UNKNOWN;
		this.online = getInitialStatus();
		
		registerEventHandlers();
		
		if(GWT.isProdMode()) {
			startNetworkPolling();
		}
	}
	
	// --
	// Public API.
	// --
	
	/**
	 * Fire the current state to every state listener.
	 * 
	 * @param onStateFound Called when the state is found.
	 */
	public void fireCurrentState(final Runnable onStateFound) {
		updateApplicationState(onStateFound, onReconnection);
	}
	
	/**
	 * Retrieves the last found state.
	 * 
	 * @return Last application state.
	 */
	public ApplicationState getLastState() {
		return state;
	}
	
	// ---
	// Getters and setters.
	// ---
	
	private void setOnline(boolean status) {
		if(this.online != status || state == ApplicationState.UNKNOWN) {
			this.online = status;
			updateApplicationState(onReconnection);
		}
	}

	public ApplicationState getState() {
		return state;
	}

	private void setState(ApplicationState state, Runnable... runnables) {
		setState(state, true);
		
		if(runnables != null) {
			for(final Runnable runnable : runnables) {
				runnable.run();
			}
		}
	}
	
	private void setState(ApplicationState state, boolean fireEvent) {
		this.state = state;
		
		if(fireEvent) {
			eventBus.fireEvent(new OfflineEvent(this, state));
		}
	}
	
	// ---
	// Initialization.
	// ---
	
	private void startNetworkPolling() {
		new Timer() {
			@Override
			public void run() {
				updateStatus();
			}
		}.scheduleRepeating(NETWORK_POLLING_INTERVAL);
	}
	
	private void registerEventHandlers() {
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				if(ApplicationStateManager.this != event.getEventSource()) {
					final ApplicationState state = event.getState();
					setState(state, false);
					
					if(state != ApplicationState.UNKNOWN) {
						online = state != ApplicationState.OFFLINE;
					}
					
					onReconnection.run();
				}
			}
		});
		
		onReconnection = new Runnable() {
			private ApplicationState lastState = ApplicationStateManager.this.state;
			private boolean firstTime = true;

			@Override
			public void run() {
				if(lastState == ApplicationState.ONLINE && state == ApplicationState.OFFLINE) {
					// BUGFIX #690: Display a message saying that the connection has been lost.
					N10N.offlineNotif(I18N.CONSTANTS.sigmahOfflineDeconnectionTitle(), I18N.CONSTANTS.sigmahOfflineDeconnectionMessage());
					
				} else if(lastState == ApplicationState.OFFLINE && state == ApplicationState.READY_TO_SYNCHRONIZE) {
					// Display a message saying that the network is available.
					N10N.offlineNotif(I18N.CONSTANTS.sigmahOfflineFirstReconnectionTitle(), I18N.CONSTANTS.sigmahOfflineFirstReconnectionMessage());
					
					if(firstTime) {
						// Also show the offline menu
						eventBus.updateZoneRequest(Zone.OFFLINE_BANNER.requestWith(RequestParameter.SHOW_BRIEFLY, true));
						firstTime = false;
					}
				}
				
				lastState = state;

				if(state == ApplicationState.ONLINE) {
					firstTime = true;
				}
			}
		};
	}
	
	// ---
	// Status change handlers.
	// --
	
	private void updateStatus() {
		final RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, "sigmah/online.nocache.json");
		try {
			requestBuilder.sendRequest(null, new RequestCallback() {
				@Override
				public void onResponseReceived(com.google.gwt.http.client.Request request, Response response) {
					if(response != null && response.getText() != null && !response.getText().isEmpty()) {
						try {
							final JSONValue value = JSONParser.parseStrict(response.getText());
							final JSONObject object = value.isObject();
							if(object != null) {
								final JSONValue onlineObject = object.get("online");
								final JSONBoolean online = onlineObject.isBoolean();
								setOnline(online != null && online.booleanValue());
							} else {
								setOnline(false);
							}
						} catch(JSONException ex) {
							setOnline(false);
							Log.error("An error occured while parsing the JSON string: '" + response.getText() + "'.", ex);
						}
					} else {
						setOnline(false);
					}
				}
				
				@Override
				public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
					setOnline(false);
				}
			});
			
		} catch (RequestException ex) {
			setOnline(false);
			Log.error("An error occured while checking the connection state.", ex);
		}
	}
	
	private void updateApplicationState(final Runnable... runnables) {
		if(!GWT.isProdMode()) {
			setState(ApplicationState.ONLINE, runnables);
			
		} else if(online) {
			isPushNeeded(new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					setState(ApplicationState.ONLINE, runnables);
				}

				@Override
				public void onSuccess(Boolean pushNeeded) {
					if(pushNeeded) {
                        setState(ApplicationState.READY_TO_SYNCHRONIZE, runnables);
                    } else {
                        setState(ApplicationState.ONLINE, runnables);
                    }
				}
			});
			
		} else {
			setState(ApplicationState.OFFLINE, runnables);
		}
	}
	
	private void isPushNeeded(final AsyncCallback<Boolean> callback) {
		if(updateDiaryAsyncDAO.isAnonymous()) {
			callback.onSuccess(Boolean.FALSE);
			
		} else {
			updateDiaryAsyncDAO.count(new AsyncCallback<Integer>() {
				@Override
				public void onFailure(Throwable caught) {
					callback.onFailure(caught);
				}

				@Override
				public void onSuccess(Integer result) {
					callback.onSuccess(result > 0);
				}
			});
		}
	}
	
	private native boolean getInitialStatus() /*-{
		return typeof $wnd.online == 'undefined' || $wnd.online;
	}-*/;
}
