package org.sigmah.client.page.event;

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

import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.handler.PageChangedHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Page changed event.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PageChangedEvent extends GwtEvent<PageChangedHandler> {

	private static Type<PageChangedHandler> TYPE;

	public static Type<PageChangedHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<PageChangedHandler>();
		}
		return TYPE;
	}

	private final PageRequest request;

	public PageChangedEvent(PageRequest request) {
		this.request = request;
	}

	public PageRequest getRequest() {
		return request;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(PageChangedHandler handler) {
		handler.onPageChange(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<PageChangedHandler> getAssociatedType() {
		return getType();
	}

}
