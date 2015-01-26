package org.sigmah.client.ui.zone.handler;

import org.sigmah.client.ui.zone.event.ZoneRequestEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Zone request handler.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface ZoneRequestHandler extends EventHandler {

	/**
	 * Called when something has requested a zone update. Should be implemented by instances which can update the zone.
	 * 
	 * @param event
	 *          The event.
	 */
	void onZoneRequest(ZoneRequestEvent event);

}
