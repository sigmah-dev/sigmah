package org.sigmah.client.ui.view.base;

import org.sigmah.client.event.handler.ClosePopupHandler;
import org.sigmah.client.ui.widget.Loadable;

/**
 * Each presenter's popup view implementation should implement this interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface ViewPopupInterface extends ViewInterface, HasPageMessage, Loadable {

	/**
	 * Displays and centers the current popup view.
	 */
	void center();

	/**
	 * Hides the current popup view.
	 */
	void hide();

	/**
	 * Sets the popup title.
	 * 
	 * @param title
	 *          The new popup title.
	 */
	void setPopupTitle(String title);

	/**
	 * Removes the given style name from the popup.
	 * 
	 * @param style
	 *          The style name.
	 */
	void removePopupStyleName(String style);

	/**
	 * Adds a style name to the popup.
	 * 
	 * @param style
	 *          The style name.
	 */
	void addPopupStyleName(String style);

	/**
	 * Provides a close action handler to the popup.
	 * 
	 * @param handler
	 *          The close handler. Does nothing if {@code null}.
	 */
	void setCloseHandler(ClosePopupHandler handler);

}
