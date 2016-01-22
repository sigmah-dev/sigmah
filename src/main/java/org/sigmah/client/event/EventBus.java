package org.sigmah.client.event;

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

import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

/**
 * <p>
 * Application event bus interface.
 * </p>
 * <p>
 * Handles pages navigation, zones update, messages and notifications display.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public interface EventBus extends HasHandlers {

	/**
	 * Callback used to execute process on presenter leaving.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	static interface LeavingCallback {

		/**
		 * Executed if the current presenter should be left.
		 */
		void leavingOk();

		/**
		 * Executed if the current presenter should <b>not</b> be left.
		 */
		void leavingKo();

	}

	/**
	 * Registers the given {@code handler} for the {@code type}.
	 * 
	 * @param <H>
	 *          The event handler type.
	 * @param type
	 *          The type.
	 * @param handler
	 *          The handler.
	 * @return The {@code HandlerRegistration}.
	 */
	<H extends EventHandler> HandlerRegistration addHandler(final Type<H> type, final H handler);

	/**
	 * Gets the handler at the given {@code code}.
	 * 
	 * @param <H>
	 *          The event handler type.
	 * @param index
	 *          The index.
	 * @param type
	 *          The handler's event type.
	 * @return The given handler.
	 */
	<H extends EventHandler> H getHandler(final Type<H> type, final int index);

	/**
	 * Gets the number of handlers listening to the event {@code type}.
	 * 
	 * @param type
	 *          The event type.
	 * @return The number of registered handlers.
	 */
	int getHandlerCount(final Type<?> type);

	/**
	 * Does this handler manager handle the given event type?
	 * 
	 * @param e
	 *          The event type.
	 * @return Whether the given event type is handled.
	 */
	boolean isEventHandled(final Type<?> e);

	/**
	 * Logs out the current authenticated user (if any).
	 */
	void logout();

	/**
	 * Navigates to the given {@code page}.<br>
	 * To access the page with <em>URL</em> and/or <em>Object</em> parameters, use
	 * {@link #navigateRequest(PageRequest, Loadable...)}.
	 * 
	 * @param page
	 *          The {@link Page} to access (without URL parameters).
	 * @param loadables
	 *          (optional) The {@link Loadable} elements to set in {@code loading} mode during page access rights
	 *          retrieval action.
	 */
	void navigate(final Page page, final Loadable... loadables);

	/**
	 * Navigates to the given page {@code request}.
	 * 
	 * @param request
	 *          The {@link Page} request to access.
	 * @param loadables
	 *          (optional) The {@link Loadable} elements to set in {@code loading} mode during page access rights
	 *          retrieval action.
	 */
	void navigateRequest(final PageRequest request, final Loadable... loadables);

	/**
	 * Updates the given {@code zone}.<br>
	 * To update a zone with <em>Object</em> parameters, use {@link #updateZoneRequest(ZoneRequest)}.
	 * 
	 * @param zone
	 *          The {@link Zone} to update.
	 */
	void updateZone(final Zone zone);

	/**
	 * Updates the given {@code zoneRequest} corresponding zone.
	 * 
	 * @param zoneRequest
	 *          The {@link ZoneRequest} containing the zone to update and some optional data parameters.
	 */
	void updateZoneRequest(final ZoneRequest zoneRequest);

}
