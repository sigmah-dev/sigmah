package org.sigmah.client.ui.widget.popup;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
