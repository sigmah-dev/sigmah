package org.sigmah.client.event;

import com.google.gwt.event.shared.GwtEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.offline.status.ApplicationState;

/**
 * Fired when the internet connection changes.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineEvent extends GwtEvent<OfflineHandler> {

	/**
	 * Offline event source. Must be implemented by objects that can fire
	 * an offline event.
	 */
	public interface Source {
	}
	
	private static Type<OfflineHandler> TYPE;
	
	private final ApplicationState state;
	private final Source eventSource;
	
	public OfflineEvent(Source eventSource, ApplicationState state) {
		this.eventSource = eventSource;
		this.state = state;
	}

	// --
	// GWT event method.
	// --
	
	public static Type<OfflineHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<OfflineHandler>();
		}
		return TYPE;
	}
	
	@Override
	public Type<OfflineHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(OfflineHandler handler) {
		handler.handleEvent(this);
	}
	
	// --
	// Event properties.
	// --

	public ApplicationState getState() {
		return state;
	}

	public Source getEventSource() {
		return eventSource;
	}
	
}
