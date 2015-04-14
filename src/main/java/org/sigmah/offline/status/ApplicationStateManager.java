package org.sigmah.offline.status;

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
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;

/**
 * Poll the network to detect changes in the connection state.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
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
	
	@Inject
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
	public void fireCurrentState(final Runnable onStateFound) {
		updateApplicationState(onStateFound);
	}
	
	// ---
	// Getters and setters.
	// ---
	
	private void setOnline(boolean status) {
		if(this.online != status) {
			this.online = status;
			updateApplicationState(onReconnection);
		}
	}

	public ApplicationState getState() {
		return state;
	}

	private void setState(ApplicationState state, Runnable runnable) {
		setState(state, true);
		
		if(runnable != null) {
			runnable.run();
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
				}
			}
		});
		
		onReconnection = new Runnable() {
			private ApplicationState lastState = ApplicationStateManager.this.state;

			@Override
			public void run() {
				if(lastState == ApplicationState.OFFLINE && state == ApplicationState.READY_TO_SYNCHRONIZE) {
					// Display a message saying that the network is available.
					N10N.offlineNotif(I18N.CONSTANTS.sigmahOfflineFirstReconnectionTitle(), I18N.CONSTANTS.sigmahOfflineFirstReconnectionMessage(), eventBus);
					
				} else {
					lastState = state;
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
	
	private void updateApplicationState(final Runnable runnable) {
		if(!GWT.isProdMode()) {
			setState(ApplicationState.ONLINE, runnable);
			
		} else if(online) {
			isPushNeeded(new AsyncCallback<Boolean>() {

				@Override
				public void onFailure(Throwable caught) {
					setState(ApplicationState.ONLINE, runnable);
				}

				@Override
				public void onSuccess(Boolean pushNeeded) {
					if(pushNeeded) {
                        setState(ApplicationState.READY_TO_SYNCHRONIZE, runnable);
                    } else {
                        setState(ApplicationState.ONLINE, runnable);
                    }
				}
			});
			
		} else {
			setState(ApplicationState.OFFLINE, runnable);
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
