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

import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.handler.PageRequestHandler;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Page request event.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PageRequestEvent extends GwtEvent<PageRequestHandler> {

	private static Type<PageRequestHandler> TYPE;

	public static Type<PageRequestHandler> getType() {
		if (TYPE == null) {
			TYPE = new Type<PageRequestHandler>();
		}
		return TYPE;
	}

	private final PageRequest request;
	private final boolean fromHistory;

	public PageRequestEvent(final Page page) {
		this(page != null ? page.request() : null, false);
	}

	public PageRequestEvent(final PageRequest request) {
		this(request, false);
	}

	public PageRequestEvent(final PageRequest request, final boolean fromHistory) {
		this.request = request;
		this.fromHistory = fromHistory;
	}

	public PageRequest getRequest() {
		return request;
	}

	public boolean isFromHistory() {
		return fromHistory;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void dispatch(final PageRequestHandler handler) {
		handler.onPageRequest(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Type<PageRequestHandler> getAssociatedType() {
		return getType();
	}

	/**
	 * Returns if the current event concerns the given {@code page}.
	 * 
	 * @param page
	 *          The page.
	 * @return {@code true} if the current event concerns the given {@code page}, {@code false} otherwise.
	 */
	public boolean concern(final Page page) {
		final Page currentPage = getRequest() != null ? getRequest().getPage() : null;
		return currentPage != null && currentPage == page;
	}

}
