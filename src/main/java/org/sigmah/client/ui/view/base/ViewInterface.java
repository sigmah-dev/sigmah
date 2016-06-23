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

import org.sigmah.client.ui.widget.Loadable;

import com.google.gwt.user.client.ui.IsWidget;

/**
 * Each presenter's view implementation should implement this interface.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface ViewInterface extends IsWidget {

	/**
	 * Initializes the view components.
	 * <b>Executed only one time on first load.</b>
	 */
	void initialize();

	/**
	 * View revealed callback.
	 * <b>Executed each time the view is revealed.</b>
	 */
	void onViewRevealed();

	/**
	 * Gets the {@link Loadable} elements list that will be locked when action is executed.
	 * 
	 * @return the {@link Loadable} elements list that will be locked when action is executed.
	 */
	Loadable[] getLoadables();

	/**
	 * <p>
	 * Returns if the current view should be shown on <em>full page</em> and not within the common content area.
	 * </p>
	 * <p>
	 * <em>Default implementation returns {@code false}.</em>
	 * </p>
	 * 
	 * @return {@code true} if the current view should be shown on <em>full page</em> and not within common content area,
	 *         {@code false} if the view should be shown within common content area.
	 */
	boolean isFullPage();

}
