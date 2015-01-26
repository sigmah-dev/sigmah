package org.sigmah.client.page.handler;

import org.sigmah.client.page.event.PageChangedEvent;

import com.google.gwt.event.shared.EventHandler;

/**
 * Page changed handler.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface PageChangedHandler extends EventHandler {

	/**
	 * Called after the current page has already changed. Allows handlers to update any internal tracking, etc.
	 * 
	 * @param event
	 *          The event.
	 */
	void onPageChange(PageChangedEvent event);

}
