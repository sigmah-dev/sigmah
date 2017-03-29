package org.sigmah.client.page;

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

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.event.EventBus;
import org.sigmah.client.page.event.PageChangedEvent;
import org.sigmah.client.page.event.PageRequestEvent;
import org.sigmah.client.page.handler.PageChangedHandler;
import org.sigmah.client.page.handler.PageRequestHandler;
import org.sigmah.client.ui.presenter.base.Presenter.PagePresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.shared.util.Pair;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.inject.Inject;

/**
 * Page manager handling History and pages loading.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
public class PageManager implements ValueChangeHandler<String>, PageChangedHandler, PageRequestHandler {
    
    /**
     * Enable or disable Google Analytics tracking.
     */
    public static boolean trackingEnabled = false;

	/**
	 * Application event bus.
	 */
	private final EventBus eventBus;

	/**
	 * Record of all the application presenter's pages.
	 */
	private final Map<String, Pair<Page, Boolean>> pages;

	/**
	 * Record of all the application {@link Page}s with their corresponding {@link PagePresenter} instance.
	 */
	private final Map<Page, PagePresenter<?>> presenters;

	/**
	 * The current page request.
	 */
	private PageRequest currentPageRequest;

	/**
	 * The current popup page request.
	 */
	private PageRequest currentPopupPageRequest;

	/**
	 * Page manager constructor.
	 * 
	 * @param eventBus
	 *          application event bus.
	 */

	public PageManager(final EventBus eventBus) {
		this.eventBus = eventBus;
		pages = new HashMap<String, Pair<Page, Boolean>>();
		presenters = new HashMap<Page, PagePresenter<?>>();

		// Register ourselves with the History API.
		History.addValueChangeHandler(this);

		// Listen for manual place change events.
		eventBus.addHandler(PageChangedEvent.getType(), this);
	}

	/**
	 * Registers a {@link PagePresenter} instance.
	 * 
	 * @param pagePresenter
	 *          The {@link PagePresenter} instance associated to a {@link Page} token.
	 * @param popupView
	 *          {@code true} if the given {@code page} view is displayed as a popup view.
	 */
	public void registerPage(final PagePresenter<?> pagePresenter, final boolean popupView) {

		if (pagePresenter == null) {
			throw new IllegalArgumentException("Invalid page presenter instance.");
		}

		final Page page = pagePresenter.getPage();

		if (page != null) {
			presenters.put(page, pagePresenter);
			pages.put(page.getToken(), new Pair<Page, Boolean>(page, popupView));
		}
	}

	/**
	 * Returns the {@link Page} instance associated to the given page id.
	 * 
	 * @param pageId
	 *          page id value
	 * @return the {@link Page} instance associated to the given page id, or {@code null} if no page instance exists for
	 *         the given id.
	 */
	public Page getPage(final String pageId) {
		return pages.get(pageId) != null ? pages.get(pageId).left : null;
	}

	/**
	 * Returns if the given {@link Page} instance is associated to a popup view.
	 * If the page is {@code null} or is not registered into page manager, the method returns {@code false}.
	 * 
	 * @param page
	 *          The page instance.
	 * @return {@code true} if the given {@link Page} instance is associated to a popup view, {@code false} otherwise.
	 */
	public boolean isPopupView(final Page page) {
		if (page == null || pages.get(page.getToken()) == null) {
			return false;
		}
		return pages.get(page.getToken()).right;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onValueChange(final ValueChangeEvent<String> event) {
		try {

			final PageRequest pageRequest = PageRequest.fromString(event.getValue(), pages);

			// A popup page cannot be accessed directly by URL modification (such as <a> elements).
			// Except the 'release' popup.
			if (isPopupView(pageRequest.getPage())) {
				if (Log.isInfoEnabled()) {
					Log.info("Popup page '" + pageRequest + "' cannot be accessed directly by URL.");
				}
				eventBus.navigate(null);
			} else {
				eventBus.fireEvent(new PageRequestEvent(pageRequest, true));
			}

		} catch (final PageParsingException e) {
			eventBus.navigate(null);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageChange(final PageChangedEvent event) {

		final PageRequest pageRequest = event.getRequest();
		final Page page = pageRequest.getPage();

		// Tracks current page.
		trackPage(page);

		if (page != null && isPopupView(page)) {
			currentPopupPageRequest = pageRequest;
		} else {
			currentPageRequest = pageRequest;
		}

		newPlace(pageRequest);

		updateZones(pageRequest);

	}

	/**
	 * Updates zones.
	 * Does nothing if given {@code pageRequest} references a pop-up view.
	 * 
	 * @param pageRequest
	 *          The page request (containing access rights).
	 */
	private void updateZones(final PageRequest pageRequest) {

		final Page page = pageRequest.getPage();

		if (page != null && isPopupView(page)) {
			return;
		}

		eventBus.updateZone(Zone.ORG_BANNER);
		eventBus.updateZone(Zone.AUTH_BANNER);
		eventBus.updateZone(Zone.OFFLINE_BANNER);
		eventBus.updateZoneRequest(Zone.MENU_BANNER.requestWith(RequestParameter.REQUEST, pageRequest));

	}

	/**
	 * Adds a new browser history entry only if the requested page exists among the {@code pages} map attribute.
	 * Does nothing if given {@code pageRequest} references a pop-up view or a <em>skip history</em> page.
	 * 
	 * @param request
	 *          Page request.
	 */
	private void newPlace(final PageRequest request) {

		final Page page = request.getPage();

		if (page != null && (page.skipHistory() || isPopupView(page))) {
			return; // "Pop-up views" and "Skip history pages" don't generate a new History item.
		}

		History.newItem(request.toString(), false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequestEvent event) {
		if (!event.isFromHistory()) {
			currentPageRequest = event.getRequest();
			newPlace(event.getRequest());
		}
	}

	public void fireCurrentPlace() {
		if (History.getToken() != null) {
			History.fireCurrentHistoryState();
		}
	}

	/**
	 * Returns the current page (even if the current page is a popup).
	 * 
	 * @return the current page or {@code null}.
	 */
	public Page getCurrentPage() {
		return getCurrentPage(true);
	}

	/**
	 * Returns the current page.
	 * 
	 * @param includeCurrentPopup
	 *          Set to <code>true</code> to get the current popup's page if any popup is currently displayed, set to
	 *          <code>false</code> to get the current non-popup page even if a popup is currently displayed.
	 * @return the current page or {@code null}.
	 */
	public Page getCurrentPage(boolean includeCurrentPopup) {

		final PageRequest pageRequest = getCurrentPageRequest(includeCurrentPopup);
		return pageRequest != null ? pageRequest.getPage() : null;
	}

	/**
	 * Returns the current page token (even if the current page is a popup).
	 * 
	 * @return the current page <b>token</b> or {@code null}.
	 */
	public String getCurrentPageToken() {
		return getCurrentPageToken(true);
	}

	/**
	 * Returns the current page <b>token</b>.
	 * 
	 * @param includeCurrentPopup
	 *          Set to <code>true</code> to get the current popup's page token if any popup is currently displayed, set to
	 *          <code>false</code> to get the current non-popup page token even if a popup is currently displayed.
	 * @return the current page token or {@code null}.
	 */
	public String getCurrentPageToken(boolean includeCurrentPopup) {
		final Page currentPage = getCurrentPage(includeCurrentPopup);
		return currentPage != null ? currentPage.getToken() : null;
	}

	/**
	 * Returns the current page request (even if the current page is a popup).
	 * 
	 * @return the current page request or {@code null}.
	 */
	public PageRequest getCurrentPageRequest() {
		return getCurrentPageRequest(true);
	}

	/**
	 * Returns the current page request.
	 * 
	 * @param includeCurrentPopup
	 *          Set to <code>true</code> to get the current popup's page request if any popup is currently displayed, set
	 *          to <code>false</code> to get the current non-popup page request even if a popup is currently displayed.
	 * @return the current page request or {@code null}.
	 */
	public PageRequest getCurrentPageRequest(boolean includeCurrentPopup) {

		if (includeCurrentPopup && AbstractPopupView.isPopupDisplayed()) {
			return new PageRequest(currentPopupPageRequest);
		} else {
			return new PageRequest(currentPageRequest);
		}
	}

	/**
	 * Returns if the current page is a popup view.
	 * 
	 * @return {@code true} if the current page is a popup view.
	 */
	public boolean isCurrentPagePopup() {
		return isPopupView(getCurrentPage());
	}

	/**
	 * Returns the current {@link PagePresenter} (even if the current page is a popup).
	 * 
	 * @return the current {@link PagePresenter} instance or {@code null}.
	 */
	public PagePresenter<?> getCurrentPresenter() {
		return getCurrentPresenter(true);
	}

	/**
	 * Returns the current {@link PagePresenter} instance.
	 * 
	 * @param includeCurrentPopup
	 *          Set to <code>true</code> to get the current popup's {@link PagePresenter} if any popup is currently
	 *          displayed, set to <code>false</code> to get the current non-popup {@link PagePresenter} even if a popup is
	 *          currently displayed.
	 * @return the current {@link PagePresenter} instance or {@code null}.
	 */
	public PagePresenter<?> getCurrentPresenter(boolean includeCurrentPopup) {

		final Page currentPage = getCurrentPage(includeCurrentPopup);

		return currentPage != null ? presenters.get(currentPage) : null;
	}

	/**
	 * Returns the given {@code url} corresponding {@code PageRequest}.
	 * 
	 * @param url
	 *          The URL string value to parse.
	 * @return the given {@code url} corresponding {@code PageRequest}.
	 * @throws IllegalArgumentException
	 *           If the given {@code url} is invalid.
	 */
	public PageRequest getPageRequest(final String url) {
		try {

			return PageRequest.fromString(url, pages);

		} catch (final Exception e) {
			throw new IllegalArgumentException("URL '" + url + "' is invalid.", e);
		}
	}

	/**
	 * Tracks given page in Google Analytics.
	 * 
	 * @param page
	 *          The tracked page.
	 */
	public static void trackPage(final Page page) {
		if (trackingEnabled && page != null) {
			trackPage(page.getToken());
		}
	}

	/**
	 * Tracks given page in Google Analytics.
	 * 
	 * @param pageName
	 *          The tracked page name.
	 */
	private static native void trackPage(final String pageName) /*-{
        try {
            $wnd._gaq.push([ '_setAccount', 'UA-000000000-1' ]);
            $wnd._gaq.push([ '_trackPageview', pageName ]);
            $wnd._gaq.push([ '_trackPageLoadTime' ]);
        } catch (err) {
            // Custom exception handling.
        }
    }-*/;

}
