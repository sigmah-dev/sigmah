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

import org.sigmah.client.event.handler.ClosePopupHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Fired when a popup is closed.
 * 
 * @author Raphaël GRENIER (rgrenier@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class ClosePopupEvent extends GwtEvent<ClosePopupHandler> {

	private static Type<ClosePopupHandler> TYPE;

	public static Type<ClosePopupHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<ClosePopupHandler>();
		}
		return TYPE;
	}

	public void closePopup() {
		// Does nothing by default.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<ClosePopupHandler> getAssociatedType() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(ClosePopupHandler handler) {
		handler.onClosePopup(this);
	}

}
