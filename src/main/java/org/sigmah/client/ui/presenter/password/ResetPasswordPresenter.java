package org.sigmah.client.ui.presenter.password;

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
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.password.ResetPasswordView;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.command.PasswordManagementCommand;
import org.sigmah.shared.command.result.StringResult;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Reset password presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class ResetPasswordPresenter extends AbstractPagePresenter<ResetPasswordPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ResetPasswordView.class)
	public static interface View extends ViewInterface {

		ValueBoxBase<String> getEmailField();

		ValueBoxBase<String> getPasswordField();

		ValueBoxBase<String> getPasswordConfirmationField();

		Button getValidateButton();

	}

	/**
	 * {@code KeyDownHandler} firing event on enter key down.
	 */
	private final KeyDownHandler keyDownHandler = new KeyDownHandler() {

		@Override
		public void onKeyDown(KeyDownEvent event) {
			if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
				view.getValidateButton().fireEvent(Events.Select);
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
	@Inject
	public ResetPasswordPresenter(final View view, final Injector injector) {
		super(view, injector);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.RESET_PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Enter key handler.
		view.getPasswordField().addKeyDownHandler(keyDownHandler);
		view.getPasswordConfirmationField().addKeyDownHandler(keyDownHandler);

		// Validate button action handler.
		view.getValidateButton().addListener(Events.Select, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(final BaseEvent be) {
				onValidateAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {

		// Reset.
		view.getEmailField().setValue(null);
		view.getPasswordField().setValue(null);
		view.getPasswordConfirmationField().setValue(null);

		// Read parameters from request.
		final String changePasswordToken = request.getParameter(RequestParameter.ID);

		if (ClientUtils.isBlank(changePasswordToken)) {
			eventBus.navigate(null);
			return;
		}

		// Retrieves user email from "change password" token.
		dispatch.execute(new PasswordManagementCommand(changePasswordToken), new CommandResultHandler<StringResult>() {

			@Override
			protected void onCommandSuccess(final StringResult result) {

				if (result == null || ClientUtils.isBlank(result.getValue())) {
					N10N.warn(I18N.CONSTANTS.invalidLink(), I18N.CONSTANTS.invalidLinkDetail());
					eventBus.navigate(null);
					return;
				}

				// Populates email field with result value.
				view.getEmailField().setValue(result.getValue());
			}
		}, view.getLoadables());
	}

	// ----------------------------------------------------------------------------------------------
	//
	// ACTIONS HANDLERS.
	//
	// ----------------------------------------------------------------------------------------------

	/**
	 * Handler executed on validate button click.
	 */
	private void onValidateAction() {

		final String email = view.getEmailField().getValue();
		final String password = view.getPasswordField().getValue();
		final String passwordConfirmation = view.getPasswordConfirmationField().getValue();

		if (ClientUtils.isBlank(password) || ClientUtils.isBlank(passwordConfirmation)) {
			N10N.warn(I18N.CONSTANTS.formWindowFieldsUnfilledDetails());
			return;
		}

		if (!ClientUtils.equals(password, passwordConfirmation)) {
			N10N.warn(I18N.CONSTANTS.passwordNotMatch());
			return;
		}

		// Executes command.
		dispatch.execute(new PasswordManagementCommand(email, password), new CommandResultHandler<StringResult>() {

			@Override
			protected void onCommandSuccess(final StringResult result) {
				N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.passwordUpdated());
				eventBus.navigateRequest(Page.LOGIN.requestWith(RequestParameter.ID, email), view.getLoadables());
			}

			@Override
			protected void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.save(), I18N.CONSTANTS.saveError());
			}

		}, view.getLoadables());
	}

}
