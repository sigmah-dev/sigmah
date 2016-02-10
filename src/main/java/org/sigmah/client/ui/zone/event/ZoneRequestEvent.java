package org.sigmah.client.ui.zone.event;

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

import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.ui.zone.handler.ZoneRequestHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Zone request event.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ZoneRequestEvent extends GwtEvent<ZoneRequestHandler> {

	private static Type<ZoneRequestHandler> TYPE;

	private ZoneRequest zoneRequest;

	public static Type<ZoneRequestHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ZoneRequestHandler>();
		}
		return TYPE;
	}

	public ZoneRequestEvent(ZoneRequest zoneRequest) {
		this.zoneRequest = zoneRequest;
	}

	public ZoneRequest getZoneRequest() {
		return zoneRequest;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<ZoneRequestHandler> getAssociatedType() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(ZoneRequestHandler handler) {
		handler.onZoneRequest(this);
	}

	/**
	 * Returns if the current event concerns the given {@code zone}.
	 * 
	 * @param zone
	 *          The zone.
	 * @return {@code true} if the current event concerns the given {@code zone}, {@code false} otherwise.
	 */
	public boolean concern(final Zone zone) {
		final Zone currentZone = getZoneRequest() != null ? getZoneRequest().getZone() : null;
		return currentZone != null && currentZone == zone;
	}

}
