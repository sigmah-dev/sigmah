package org.sigmah.client.event.handler;

import org.sigmah.client.event.UpdateEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles the {@link UpdateEvent} events.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface UpdateHandler extends EventHandler {

	/**
	 * Called when a element is updated.
	 * 
	 * @param event
	 *          The event.
	 */
	void onUpdate(UpdateEvent event);

}
