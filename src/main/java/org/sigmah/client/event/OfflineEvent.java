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

import com.google.gwt.event.shared.GwtEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.offline.status.ApplicationState;

/**
 * Fired when the internet connection changes.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OfflineEvent extends GwtEvent<OfflineHandler> {

	/**
	 * Offline event source. Must be implemented by objects that can fire
	 * an offline event.
	 */
	public interface Source {
	}
	
	private static Type<OfflineHandler> TYPE;
	
	private final ApplicationState state;
	private final Source eventSource;
	
	public OfflineEvent(Source eventSource, ApplicationState state) {
		this.eventSource = eventSource;
		this.state = state;
	}

	// --
	// GWT event method.
	// --
	
	public static Type<OfflineHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<OfflineHandler>();
		}
		return TYPE;
	}
	
	@Override
	public Type<OfflineHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(OfflineHandler handler) {
		handler.handleEvent(this);
	}
	
	// --
	// Event properties.
	// --

	public ApplicationState getState() {
		return state;
	}

	public Source getEventSource() {
		return eventSource;
	}
	
}
