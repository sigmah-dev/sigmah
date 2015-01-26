package org.sigmah.client.ui.widget.popup;

import org.sigmah.client.event.handler.ClosePopupHandler;
import org.sigmah.client.ui.view.base.HasPageMessage;
import org.sigmah.client.ui.widget.Loadable;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

/**
 * Interface implemented by all popup widgets.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface IsPopupWidget extends IsWidget, HasPageMessage, Loadable {

	/**
	 * Displays and centers the popup widget.
	 */
	void center();

	/**
	 * Shows the popup widget.
	 */
	void show();

	/**
	 * Hides the popup widget.
	 */
	void hide();

	/**
	 * Sets the popup widget style name.
	 * 
	 * @param styleName
	 *          The popup widget style name.
	 */
	void setStyleName(String styleName);

	/**
	 * Adds a style name to the popup widget.
	 * 
	 * @param styleName
	 *          The popup widget style name added.
	 */
	void addStyleName(String styleName);

	/**
	 * Removes a style name from the popup widget.
	 * 
	 * @param styleName
	 *          The popup widget style name removed.
	 */
	void removeStyleName(String styleName);

	/**
	 * Sets the new popup title.
	 * 
	 * @param title
	 *          The new popup title.
	 */
	void setTitle(String title);

	/**
	 * Sets the new popup content with the given {@code widget} (if a previous content was set, it is overridden).
	 * 
	 * @param widget
	 *          The new popup content widget.
	 */
	void setContent(Widget widget);

	/**
	 * Sets the popup widget's width.
	 * 
	 * @param width
	 *          The popup width ({@code null} to set auto width).
	 */
	void setWidth(String width);

	/**
	 * Sets the popup widget's height.
	 * 
	 * @param height
	 *          The popup height ({@code null} to set auto height).
	 */
	void setHeight(String height);

	/**
	 * Sets popup and glasspane z-index;
	 * 
	 * @param zIndex
	 *          The z-index value.
	 */
	void setZIndex(int zIndex);

	/**
	 * Sets the close handler.
	 * 
	 * @param handler
	 *          The handler.
	 */
	void setClosePopupHandler(ClosePopupHandler handler);

}
