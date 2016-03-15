package org.sigmah.client.ui.view;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.LoginPresenter;
import org.sigmah.client.ui.res.ResourcesUtils;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.inject.Singleton;

/**
 * Login view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LoginView extends AbstractView implements LoginPresenter.View {

	// CSS.
	public static final String LOGIN_BG = "login-background";
	public static final String LOGIN_BOX = "login-box";
	public static final String LOGIN_BOX_LOGO = "login-box-logo";
	public static final String LOGIN_BOX_FORM = "login-box-form";
	public static final String LOGIN_BOX_FORM_LABEL = "login-box-form-label";
	public static final String LOGIN_BOX_FORM_SEPARATOR = "login-box-form-separator";
	private static final String LOGIN_BOX_FORM_FORGOTTEN = "login-box-form-forgotten";

	// Images.
	public static final String LOGO_URL = ResourcesUtils.buildImageURL("login/login-logo.png");

	private TextBox loginTextBox;
	private PasswordTextBox passwordTextBox;
	private ListBox languageListBox;
	private Anchor lostPasswordLink;
	private Button loginButton;
	/**
	 * Id of email input.
	 */
	private final String LOGIN_EMAIL_ID="loginEmailId";
	/**
	 * Id of password input.
	 */
	private final String LOGIN_PASSWORD_ID="loginPasswordId";
	/**
	 * Id of open session button;
	 */
	private final String LOGIN_OUVRIR_SESSION_ID="ouvrirSessionId";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final SimplePanel panel = new SimplePanel();
		panel.setStyleName(LOGIN_BG);

		final Grid grid = new Grid(1, 2);
		grid.setStyleName(LOGIN_BOX);

		// Logo.
		grid.setWidget(0, 0, new Image(LOGO_URL));

		// Form.
		final FlexTable form = new FlexTable();
		form.setWidth("90%");

		int y = 0;

		// Login field (email).
		form.setText(y, 0, I18N.CONSTANTS.loginLoginField());
		form.getCellFormatter().setStyleName(y, 0, LOGIN_BOX_FORM_LABEL);

		loginTextBox = new TextBox();
		loginTextBox.getElement().setId(LOGIN_EMAIL_ID);
		loginTextBox.setWidth("100%");
		form.setWidget(y, 1, loginTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Password field.
		form.setText(y, 0, I18N.CONSTANTS.loginPasswordField());
		form.getCellFormatter().setStyleName(y, 0, LOGIN_BOX_FORM_LABEL);

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.getElement().setId(LOGIN_PASSWORD_ID);
		passwordTextBox.setWidth("100%");
		form.setWidget(y, 1, passwordTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Language field.
		form.setText(y, 0, I18N.CONSTANTS.loginLanguageField());
		form.getCellFormatter().setStyleName(y, 0, LOGIN_BOX_FORM_LABEL);

		languageListBox = new ListBox(false);
		languageListBox.setWidth("100%");
		form.setWidget(y, 1, languageListBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Password forgotten link.
		final FlowPanel bottomPanel = new FlowPanel();
		bottomPanel.getElement().getStyle().setPosition(Position.RELATIVE);

		lostPasswordLink = new Anchor(I18N.CONSTANTS.loginPasswordForgotten());
		lostPasswordLink.setStyleName(LOGIN_BOX_FORM_FORGOTTEN);
		bottomPanel.add(lostPasswordLink);

		form.setWidget(y, 0, bottomPanel);
		form.getFlexCellFormatter().setColSpan(y, 0, 2);

		// Login button.
		loginButton = new Button(I18N.CONSTANTS.loginConnectButton());
		loginButton.setId(LOGIN_OUVRIR_SESSION_ID);
		loginButton.setWidth("120px");
		form.setWidget(y, 1, loginButton);
		form.getCellFormatter().setHorizontalAlignment(y, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		y++;

		// Adding the form to the orange box.
		grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.setWidget(0, 1, form);

		// Styles.
		grid.getCellFormatter().setStyleName(0, 0, LOGIN_BOX_LOGO);
		grid.getCellFormatter().setStyleName(0, 1, LOGIN_BOX_FORM);

		panel.add(grid);

		add(panel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isFullPage() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Loadable[] getLoadables() {
		return new Loadable[] { loginButton
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListBox getLanguagesField() {
		return languageListBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueBoxBase<String> getLoginField() {
		return loginTextBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueBoxBase<String> getPasswordField() {
		return passwordTextBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HasClickHandlers getLostPasswordLink() {
		return lostPasswordLink;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getLoginButton() {
		return loginButton;
	}

}
