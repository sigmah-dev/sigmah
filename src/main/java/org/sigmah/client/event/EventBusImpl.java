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


import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.page.event.PageRequestEvent;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.Presenter;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.zone.Zone;
import org.sigmah.client.ui.zone.ZoneRequest;
import org.sigmah.client.ui.zone.event.ZoneRequestEvent;
import org.sigmah.shared.command.SecureNavigationCommand;
import org.sigmah.shared.command.result.SecureNavigationResult;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Application event bus implementation.
 * <p>
 * FIXME: This implementation makes strong references on the registered
 * handlers. This can create memory leaks and undesired network usage when the
 * view associated to the handler is destroyed (happens frequently with flexible
 * elements).
 * 
 * A better implementation should use <code>WeakSet</code> or 
 * <code>WeakMap</code> to avoid storing strong references.
 * 
 * @see https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/WeakSet.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */
@Singleton
public class EventBusImpl extends HandlerManager implements EventBus {

	/**
	 * Page where anonymous users are redirected using "{@code navigate(null)}".
	 */
	private static final Page DEFAULT_ANONYMOUS_PAGE = Page.LOGIN;

	/**
	 * Page where authenticated users are redirected using "{@code navigate(null)}".
	 */
	private static final Page DEFAULT_AUTHENTICATED_PAGE = Page.DASHBOARD;

	/**
	 * Callback interface used to handle {@link PageRequestEvent} dispatch action.
	 * 
	 * @author Denis Colliot (dcolliot@ideia.fr)
	 */
	private static interface PageRequestEventCallback {

		/**
		 * Method called once the dispatch action is complete.
		 * 
		 * @param event
		 *          The returned event.
		 */
		void onPageRequestEventComplete(final GwtEvent<?> event);

	}

	/**
	 * Injected application injector.
	 */
	private final Injector injector;

	/**
	 * Last failed accessed {@link PageRequest} that will be reload on next authentication.
	 */
	private PageRequest failedAccessedPageRequest;

	/**
	 * Optional {@link Loadable}s to set in {@code loading} mode during page access.
	 */
	private Loadable[] loadables;

	/**
	 * Application event bus initialization.
	 * 
	 * @param injector
	 *          The application injector.
	 */
	@Inject
	public EventBusImpl(final Injector injector) {

		super(null);

		this.injector = injector;
		this.failedAccessedPageRequest = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logout() {

		// Clears the session.
		injector.getAuthenticationProvider().clearAuthentication();

		// Navigates to default page.
		navigate(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fireEvent(final GwtEvent<?> event) {

		if (!(event instanceof PageRequestEvent)) {
			// Other (or null) events.
			handleEvent(event);
			return;
		}

		// Page request event.

		// Before leaving the current presenter.
		final Presenter<?> presenter = injector.getPageManager().getCurrentPresenter();
		updateZoneRequest(Zone.APP_LOADER.requestWith(RequestParameter.CONTENT, true));

		if (presenter == null) {
			handleEvent(event);
			return;
		}

		final PageRequest pageRequest = ((PageRequestEvent) event).getRequest();
		final Page page = pageRequest != null ? pageRequest.getPage() : null;

		if (injector.getPageManager().isPopupView(page)) {
			// Popup case : no 'before leaving' action.
			// TODO [BEFORE LEAVING] Associer le 'beforeLeaving' des popup à la fermeture des popups.
			handleEvent(event);

		} else {
			// Page change : 'before leaving' action launched.

			presenter.beforeLeaving(new LeavingCallback() {

				@Override
				public void leavingOk() {
					handleEvent(event);
				}

				@Override
				public void leavingKo() {
					handleEvent(null);
					updateZoneRequest(Zone.APP_LOADER.requestWith(RequestParameter.CONTENT, false));
				}

			});
		}
	}

	/**
	 * Handlers the given {@code event}.
	 * 
	 * @param event
	 *          The event. If the event is a {@link PageRequestEvent}, a command is sent to the server in order to
	 *          retrieve user access rights.
	 */
	private void handleEvent(final GwtEvent<?> event) {

		if (!(event instanceof PageRequestEvent)) {
			// Other (or null) events.
			if (event != null) {
				super.fireEvent(event);
			}
			return;
		}

		// Page request event.
		handlePageRequestEvent((PageRequestEvent) event, new PageRequestEventCallback() {

			@SuppressWarnings("deprecation")
			@Override
			public void onPageRequestEventComplete(final GwtEvent<?> event) {

				updateZoneRequest(Zone.APP_LOADER.requestWith(RequestParameter.CONTENT, false));

				if (event == null) {
					navigate(null, loadables);
					return;
				}

				// Local cache initialization.
				injector.getClientCache().init();

				EventBusImpl.super.fireEvent(event);
			}
		});
	}

	/**
	 * Executes a command to retrieve access rights for the current [authenticated/anonymous] user.
	 * 
	 * @param event
	 *          The {@link PageRequestEvent} event.
	 * @param callback
	 *          The {@link PageRequestEventCallback} instance executed once event has been handled (i.e. on command
	 *          callback execution).
	 */
	private void handlePageRequestEvent(final PageRequestEvent event, final PageRequestEventCallback callback) {

		final Page page = event.getRequest().getPage();

		if (Log.isTraceEnabled()) {
			Log.trace("User is attempting to access page '" + page + "'.");
		}

		// Determining page token to access.
		final PageRequestEvent accessedPageEvent;

		if (page != null && injector.getPageManager().getPage(page.getToken()) != null) {
			// Page is valid and registered with PageManager.
			accessedPageEvent = event;

		} else if (injector.getAuthenticationProvider().isAnonymous()) {
			// Page is invalid and user anonymous: redirecting user to login page.
			accessedPageEvent = new PageRequestEvent(DEFAULT_ANONYMOUS_PAGE);

		} else {
			// Page is invalid and user authenticated: redirecting user to home page.
			accessedPageEvent = new PageRequestEvent(DEFAULT_AUTHENTICATED_PAGE);
		}

		// Executing command securing the navigation event.
		injector.getDispatch().execute(new SecureNavigationCommand(accessedPageEvent.getRequest().getPage()), new CommandResultHandler<SecureNavigationResult>() {

			@Override
			protected void onCommandSuccess(final SecureNavigationResult result) {

				final boolean wasAuthenticated = !injector.getAuthenticationProvider().isAnonymous();

				// Sets the authentication.
				injector.getAuthenticationProvider().updateCache(result.getAuthentication());

				if (result.isGranted()) {
					// Page access is granted.
					callback.onPageRequestEventComplete(accessedPageEvent);

				} else {
					// Unauthorized page access.

					if (event.isFromHistory()) {
						// Directly from URL.
						callback.onPageRequestEventComplete(null);

					} else {
						// From application link.
						N10N.error(I18N.CONSTANTS.navigation_unauthorized_access());

						if (wasAuthenticated && injector.getAuthenticationProvider().isAnonymous()) {
							// User is no longer authenticated (expired session).
							callback.onPageRequestEventComplete(null);
						}
					}
				}
			}

			@Override
			protected void onCommandFailure(final Throwable caught) {
				if (Log.isErrorEnabled()) {
					Log.error("An unexpected error occured during 'SecureNavigationCommand' executin.", caught);
				}
				N10N.error(I18N.CONSTANTS.navigation_error());
			}

		}, loadables);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void navigate(final Page page, final Loadable... loadables) {

		final PageRequest pageRequest;

		// If a requested page (not a popup one) has been recorded, we redirect the logged in user towards its previous
		// request.
		if (page == null && failedAccessedPageRequest != null && !injector.getPageManager().isPopupView(failedAccessedPageRequest.getPage())) {

			pageRequest = failedAccessedPageRequest;
			failedAccessedPageRequest = null;
		}
		// Default case.
		else {
			pageRequest = new PageRequest(page);
		}

		navigateRequest(pageRequest, loadables);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void navigateRequest(final PageRequest request, final Loadable... loadables) {
		this.loadables = loadables;
		this.fireEvent(new PageRequestEvent(request));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateZone(final Zone zone) {
		updateZoneRequest(new ZoneRequest(zone));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateZoneRequest(final ZoneRequest zoneRequest) {
		this.fireEvent(new ZoneRequestEvent(zoneRequest));
	}

}
