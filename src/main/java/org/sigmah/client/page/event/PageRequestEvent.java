package org.sigmah.client.page.event;

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
