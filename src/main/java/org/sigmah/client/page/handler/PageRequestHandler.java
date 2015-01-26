package org.sigmah.client.page.handler;

import org.sigmah.client.page.event.PageRequestEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Page request handler.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface PageRequestHandler extends EventHandler {

	/**
	 * Called when something has requested a new page. Should be implemented by instances which can show the page.
	 * 
	 * @param event
	 *          The event.
	 */
	void onPageRequest(PageRequestEvent event);

}
