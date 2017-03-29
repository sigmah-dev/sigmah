package org.sigmah.client.ui.presenter;

import org.sigmah.client.ClientFactory;

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

import org.sigmah.client.event.UpdateEvent;
import org.sigmah.client.event.handler.UpdateHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.presenter.base.Presenter;
import org.sigmah.client.ui.presenter.zone.AppLoaderPresenter;
import org.sigmah.client.ui.presenter.zone.AuthenticationBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MenuBannerPresenter;
import org.sigmah.client.ui.presenter.zone.MessageBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OfflineBannerPresenter;
import org.sigmah.client.ui.presenter.zone.OrganizationBannerPresenter;
import org.sigmah.client.ui.view.ApplicationView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.base.ViewPopupInterface;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.MessageType;
import org.sigmah.shared.command.GetProperties;
import org.sigmah.shared.command.result.MapResult;
import org.sigmah.shared.conf.PropertyKey;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Application presenter.
 * This is the main presenter in charge of displaying the page that the user is navigating to using
 * {@link #showPresenter(Presenter)} method.
 *
 * The application presenter is also in charge of displaying the application message (if one is defined).
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @author Claire Yang (cyang@ideia.fr)
 * @author Tom Miette (tmiette@ideia.fr)
 */

public class ApplicationPresenter extends AbstractPresenter<ApplicationPresenter.View> {

	/**
	 * Application view.
	 */
	
	public static interface View extends ViewInterface {

		/**
		 * Hides the lodaing panel.
		 */
		void hideLoadingPanel();

		/**
		 * Shows the given {@code presenterWidget} view into proper area.
		 * 
		 * @param presenterWidget
		 *          The presenter's view to show.
		 * @param fullPage
		 *          {@code true} if the view should be shown on <em>full page</em>, {@code false} if it should be shown
		 *          within content area.
		 */
		void showPresenter(IsWidget presenterWidget, boolean fullPage);

		/**
		 * Initializes the given zones view areas.
		 */
		void initZones(OrganizationBannerPresenter.View organizationBannerView, AuthenticationBannerPresenter.View authenticationBannerPresenter,
				OfflineBannerPresenter.View offlineBannerPresenter, AppLoaderPresenter.View appLoaderPresenter, MenuBannerPresenter.View menuBannerPresenter,
				MessageBannerPresenter.View messageBannerPresenter);

		/**
		 * Returns the <em>credits</em> widget capable of handling a {@code ClickHandler}.
		 * 
		 * @return the <em>credits</em> widget capable of handling a {@code ClickHandler}.
		 */
		HasClickHandlers getCreditsHandler();

		/**
		 * Returns the <em>reports</em> widget capable of handling a {@code ClickHandler}.
		 * 
		 * @return the <em>reports</em> widget capable of handling a {@code ClickHandler}.
		 */
		HasClickHandlers getBugReportHandler();

		/**
		 * Returns the <em>help</em> widget capable of handling a {@code ClickHandler}.
		 * 
		 * @return the <em>help</em> widget capable of handling a {@code ClickHandler}.
		 */
		HasClickHandlers getHelpHandler();

		/**
		 * Updates the viewport size to appropriate dimensions.
		 */
		void updateViewportSize();

		void setPageMessage(String html, MessageType type);

	}

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param factory
	 *          Injected client injector.
	 */
	
	public ApplicationPresenter(final View view, final ClientFactory factory) {

		super(view, factory); // Executes 'bind()' method.

		view.initZones(factory.getOrganizationBannerPresenter().getView(), factory.getAuthenticationBannerPresenter().getView(), factory
			.getOfflineBannerPresenter().getView(), factory.getAppLoaderPresenter().getView(), factory.getMenuBannerPresenter().getView(), factory
			.getMessageBannerPresenter().getView());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void bind() {

		// Credits frame action.
		view.getCreditsHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.navigate(Page.CREDITS);
			}

		});

		// Bug report action.
		view.getBugReportHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				dispatch.execute(new GetProperties(PropertyKey.MAIL_SUPPORT_TO, PropertyKey.VERSION_NUMBER), new AsyncCallback<MapResult<PropertyKey, String>>() {

					@Override
					public void onFailure(Throwable caught) {

						// Cannot retrieves some properties, build the mailto link anyway.
						if (Log.isErrorEnabled()) {
							Log.error("Cannot retrieves some application properties.", caught);
						}

						mailto("support@sigmah.org", "???");

					}

					@Override
					public void onSuccess(MapResult<PropertyKey, String> result) {
						mailto(result.get(PropertyKey.MAIL_SUPPORT_TO), result.get(PropertyKey.VERSION_NUMBER));
					}

					private void mailto(String to, String versionNumber) {

						// Mail subject.
						final String url = ClientUtils.getApplicationUrl();
						final String date = ClientUtils.formatDate(ClientUtils.now(), "dd-MM-yyyy");
						final String subject = I18N.MESSAGES.bugReportMailObject(auth().getUserEmail(), url, date);

						// Mail body.
						final String userAgent = ClientUtils.getUserAgent();
						final String body = I18N.MESSAGES.bugReportBody(userAgent, versionNumber);

						// Mail-to.
						ClientUtils.mailTo(subject, body, to);

					}

				});

			}

		});

		// Help action.
		view.getHelpHandler().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				eventBus.navigate(Page.HELP);
			}

		});

		// Application message event handler.
		registerHandler(eventBus.addHandler(UpdateEvent.getType(), new UpdateHandler() {

			@Override
			public void onUpdate(final UpdateEvent event) {

				if (event.concern(UpdateEvent.VIEWPORT_SIZE_UPDATE)) {
					// Message banner has been updated: updates viewport size.
					view.updateViewportSize();
				}
			}
		}));
	}

	/**
	 * Displays the given {@code view} on the application main content area.
	 * 
	 * @param presenter
	 *          The current presenter to display into application main panel.
	 */
	public void showPresenter(final Presenter<?> presenter) {

		// Retrieving accessed page.
		final Page page;
		if (presenter instanceof PagePresenter) {
			page = ((PagePresenter<?>) presenter).getPage();
		} else {
			page = null;
		}

		// Page title.
		final String pageTitle = ClientUtils.isBlank(Page.getTitle(page)) ? "" : Page.getTitle(page);

		if (presenter.getView() instanceof ViewPopupInterface) {

			// Popup presenter's view case.
			final ViewPopupInterface viewPopup = (ViewPopupInterface) presenter.getView();
			viewPopup.setPopupTitle(pageTitle);
			viewPopup.center();

		} else {

			// Basic presenter's view case.

			// Displaying page view.
			view.showPresenter(presenter.getView().asWidget(), presenter.getView().isFullPage());

			// Sets page title widget visibility and content.
			setPageTitle(pageTitle);
		}

		// Hides the loading panel.
		view.hideLoadingPanel();
	}

	/**
	 * Updates page title. Can be used to update title after asynchronous data loading for example.
	 * The title area is automatically hidden if the given {@code pageTitle} is invalid.
	 * 
	 * @param pageTitle
	 *          The new page title.
	 */
	public void setPageTitle(String pageTitle) {
		// No page title area defined yet.
	}

	/**
	 * Sets the page message. If the message id null or empty, it will be hidden. Otherwise it will be displayed.
	 * 
	 * @param html
	 *          The message content.
	 * @param type
	 *          The message type.
	 */
	public void setPageMessage(String html, MessageType type) {
		view.setPageMessage(html, type);
		eventBus.fireEvent(new UpdateEvent(UpdateEvent.VIEWPORT_SIZE_UPDATE));
	}

}
