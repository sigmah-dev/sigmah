package org.sigmah.client.event.handler;

import org.sigmah.client.event.ClosePopupEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handles the {@link ClosePopupEvent} events.
 * 
 * @author Raphaël GRENIER (rgrenier@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface ClosePopupHandler extends EventHandler {

	/**
	 * Called when a popup is closed.
	 * 
	 * @param event
	 *          The event.
	 */
	void onClosePopup(ClosePopupEvent event);

}
