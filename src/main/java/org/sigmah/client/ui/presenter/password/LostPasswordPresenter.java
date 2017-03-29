package org.sigmah.client.ui.presenter.password;

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
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.page.RequestParameter;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.password.LostPasswordView;
import org.sigmah.shared.Language;
import org.sigmah.shared.command.PasswordManagementCommand;
import org.sigmah.shared.command.result.StringResult;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Lost password presenter.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */

public class LostPasswordPresenter extends AbstractPagePresenter<LostPasswordPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {
		FormPanel getForm();

		Field<String> getEmailField();

		Button getValidateButton();
	}

	/**
	 * Selected language.
	 */
	private Language language;

	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	public LostPasswordPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.LOST_PASSWORD;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// Enter key handler.
		view.getEmailField().addListener(Events.KeyDown, new KeyListener() {

			@Override
			public void componentKeyDown(final ComponentEvent event) {
				if (event.getKeyCode() == KeyCodes.KEY_ENTER) {
					onValidateAction();
				}
			}
		});

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

		view.getEmailField().clear();

		language = Language.fromString(request.getParameter(RequestParameter.LANGUAGE));
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

		if (!view.getForm().isValid()) {
			return;
		}

		final String email = view.getEmailField().getValue();

		final Dialog waitDialog = new Dialog();
		waitDialog.setHeadingHtml(I18N.CONSTANTS.loginPasswordForgotten());
		waitDialog.addText(I18N.CONSTANTS.loading());
		waitDialog.setButtons("");
		waitDialog.setModal(true);
		waitDialog.setClosable(false);
		waitDialog.show();

		// Executes command.
		dispatch.execute(new PasswordManagementCommand(email, language), new CommandResultHandler<StringResult>() {

			@Override
			protected void onCommandSuccess(final StringResult result) {
				waitDialog.hide();
				hideView();
				N10N.warn(I18N.MESSAGES.loginResetPasswordSuccessfull(email));
			}

			@Override
			protected void onCommandFailure(final Throwable caught) {
				waitDialog.hide();
				N10N.error(I18N.MESSAGES.loginRetrievePasswordBadLogin(email));
			}

		}, view.getLoadables());
	}

}
