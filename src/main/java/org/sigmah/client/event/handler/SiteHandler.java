package org.sigmah.client.event.handler;

import com.google.gwt.event.shared.EventHandler;
import org.sigmah.client.event.SiteEvent;

/**
 * Handles the {@link SiteEvent} events.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface SiteHandler extends EventHandler {
	
	/**
	 * Called when a site has been created or updated.
	 * 
	 * @param siteEvent The event.
	 */
	void handleEvent(SiteEvent siteEvent);
}
