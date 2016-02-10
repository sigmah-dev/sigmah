package org.sigmah.shared.dto.element.event;

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

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

/**
 * A GWT event that cannot be killed.
 * 
 * @author tmi
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <H>
 *          Event handler type.
 */
abstract class GWTImmortalEvent<H extends EventHandler> extends GwtEvent<H> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void kill() {
		// nothing.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void revive() {
		// nothing.
	}

}
