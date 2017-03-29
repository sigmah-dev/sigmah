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


import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.LoginView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.LoginCommand;
import org.sigmah.shared.command.result.Authentication;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.event.UpdateEvent;

/**
 * Login presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */

public class LoginPresenter extends AbstractPagePresenter<LoginPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */

	public static interface View extends ViewInterface {

		ListBox getLanguagesField();

		ValueBoxBase<String> getLoginField();

		ValueBoxBase<String> getPasswordField();

		HasClickHandlers getLostPasswordLink();

		Button getLoginButton();

	}

	/**
	 * {@code KeyDownHandler} firing event on enter key down.
	 */
	private final KeyDownHandler keyDownHandler = new KeyDownHandler() {

		@Override
		public void onKeyDown(final KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				view.getLoginButton().fireEvent(Events.Select);
			}
		}
	};

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	public LoginPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.LOGIN;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Languages form field values.
		for (final Language language : Language.values()) {
			view.getLanguagesField().addItem(Language.i18n(language), language.name());
		}

		// Enter key handler.
		view.getLoginField().addKeyDownHandler(keyDownHandler);
		view.getPasswordField().addKeyDownHandler(keyDownHandler);

		// Lost password link handler.
		view.getLostPasswordLink().addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				onLostPasswordLinkAction();
			}
		});

		// Login action handler.
		view.getLoginButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onLoginAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		view.getPasswordField().setValue(null);

		final int selectedLanguage = auth().getLanguage() != null ? auth().getLanguage().ordinal() : 0;
		view.getLanguagesField().setSelectedIndex(selectedLanguage);

		final String email = request.getParameter(RequestParameter.ID);
		if (ClientUtils.isNotBlank(email)) {
			view.getLoginField().setValue(email);
		}
	}

	// ----------------------------------------------------------------------------------------------
	//
	// ACTIONS HANDLERS.
	//
	// ----------------------------------------------------------------------------------------------

	/**
	 * Handler executed on lost password link click.
	 */
	private void onLostPasswordLinkAction() {

		final Language language = Language.fromString(view.getLanguagesField().getValue(view.getLanguagesField().getSelectedIndex()));

		eventBus.navigateRequest(Page.LOST_PASSWORD.requestWith(RequestParameter.LANGUAGE, language != null ? language : auth().getLanguage()));
	}

	/**
	 * Handler executed on login button click.
	 */
	private void onLoginAction() {

		final String login = view.getLoginField().getValue();
		final String password = view.getPasswordField().getValue();
		final String language = view.getLanguagesField().getValue(view.getLanguagesField().getSelectedIndex());

		// Executes login command.
		dispatch.execute(new LoginCommand(login, password, Language.fromString(language)), new CommandResultHandler<Authentication>() {

			@Override
			protected void onCommandSuccess(final Authentication authentication) {
				if (Log.isInfoEnabled()) {
					Log.info("Authentication proccesed successfully, reloading application.");
				}
				factory.getAuthenticationProvider().login(authentication);
				eventBus.fireEvent(new UpdateEvent(UpdateEvent.USER_LOGGED_IN));

				if (GWT.isScript()) {
					// PRODUCTION: Reloads entire application in order to set the selected language into HTML meta.
					Location.assign(ClientUtils.getApplicationUrl());

				} else {
					// DEV MODE: To avoid full page reloading, a simple redirection is executed. Selected language is not taken
					// into account.
					eventBus.navigate(null, view.getLoadables());
				}
			}

		}, view.getLoadables());
	}

}
