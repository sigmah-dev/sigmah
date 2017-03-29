package org.sigmah.client.ui.view.password;

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
import org.sigmah.client.ui.presenter.password.ResetPasswordPresenter;
import org.sigmah.client.ui.view.LoginView;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.Loadable;
import org.sigmah.client.ui.widget.button.Button;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.google.inject.Singleton;

/**
 * Reset password view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ResetPasswordView extends AbstractView implements ResetPasswordPresenter.View {

	private TextBox emailTextBox;
	private PasswordTextBox passwordTextBox;
	private PasswordTextBox passwordConfirmationTextBox;
	private Button validationButton;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		final SimplePanel panel = new SimplePanel();
		panel.setStyleName(LoginView.LOGIN_BG);

		final Grid grid = new Grid(1, 2);
		grid.setStyleName(LoginView.LOGIN_BOX);

		// Logo.
		grid.setWidget(0, 0, new Image(LoginView.LOGO_URL));

		// Form.
		final FlexTable form = new FlexTable();
		form.setWidth("90%");

		int y = 0;

		// Login field (email).
		form.setText(y, 0, I18N.CONSTANTS.loginLoginField());
		form.getCellFormatter().setStyleName(y, 0, LoginView.LOGIN_BOX_FORM_LABEL);

		emailTextBox = new TextBox();
		emailTextBox.setWidth("100%");
		emailTextBox.setEnabled(false);
		form.setWidget(y, 1, emailTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LoginView.LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Password field.
		form.setText(y, 0, I18N.CONSTANTS.newPassword() + '*');
		form.getCellFormatter().setStyleName(y, 0, LoginView.LOGIN_BOX_FORM_LABEL);

		passwordTextBox = new PasswordTextBox();
		passwordTextBox.setWidth("100%");
		form.setWidget(y, 1, passwordTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LoginView.LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Confirmation password field.
		form.setText(y, 0, I18N.CONSTANTS.confirmPassword() + '*');
		form.getCellFormatter().setStyleName(y, 0, LoginView.LOGIN_BOX_FORM_LABEL);

		passwordConfirmationTextBox = new PasswordTextBox();
		passwordConfirmationTextBox.setWidth("100%");
		form.setWidget(y, 1, passwordConfirmationTextBox);
		form.getFlexCellFormatter().setColSpan(y, 1, 2);
		y++;

		// Separator.
		for (int i = 0; i < 3; i++) {
			form.getCellFormatter().setStyleName(y, i, LoginView.LOGIN_BOX_FORM_SEPARATOR);
		}
		y++;

		// Validation button.
		validationButton = new Button(I18N.CONSTANTS.confirmUpdate());
		validationButton.setWidth("130px");
		form.setWidget(y, 0, validationButton);
		form.getCellFormatter().setHorizontalAlignment(y, 1, HasHorizontalAlignment.ALIGN_RIGHT);
		y++;

		// Adding the form to the orange box.
		grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
		grid.setWidget(0, 1, form);

		// Styles.
		grid.getCellFormatter().setStyleName(0, 0, LoginView.LOGIN_BOX_LOGO);
		grid.getCellFormatter().setStyleName(0, 1, LoginView.LOGIN_BOX_FORM);

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
		return new Loadable[] { validationButton
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueBoxBase<String> getEmailField() {
		return emailTextBox;
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
	public ValueBoxBase<String> getPasswordConfirmationField() {
		return passwordConfirmationTextBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getValidateButton() {
		return validationButton;
	}

}
