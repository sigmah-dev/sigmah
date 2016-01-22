package org.sigmah.client.ui.view.base;

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
