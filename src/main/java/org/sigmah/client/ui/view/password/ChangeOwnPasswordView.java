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

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;
import java.util.Arrays;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.password.ChangeOwnPasswordPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

/**
 * Change own password popup view.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ChangeOwnPasswordView extends AbstractPopupView<PopupWidget> implements ChangeOwnPasswordPresenter.View {

	private FormPanel formPanel;
	private TextField<String> emailField;
	private TextField<String> currentPasswordField;
	private TextField<String> newPasswordField;
	private TextField<String> confirmNewPasswordField;
	private Button changeButton;
	
	/**
	 * Popup initialization.
	 */
	public ChangeOwnPasswordView() {
		super(new PopupWidget(true), 400);
	}
	
	@Override
	public void initialize() {
		
		// Email field.
		emailField = Forms.text(I18N.CONSTANTS.email(), false);
		emailField.setEnabled(false);
		
		// Current password field.
		currentPasswordField = Forms.text(I18N.CONSTANTS.currentPassword(), true);
		
		// New password field.
		newPasswordField = Forms.text(I18N.CONSTANTS.newPassword(), true);
		
		// Confirm new password field.
		confirmNewPasswordField = Forms.text(I18N.CONSTANTS.confirmPassword(), true);
		
		// Set fields as password fields.
		for(final TextField<String> field : Arrays.asList(currentPasswordField, newPasswordField, confirmNewPasswordField)) {
			field.setPassword(true);
		}
		
		// Confirm change button.
		changeButton = Forms.button(I18N.CONSTANTS.save());
		
		// Building form.
		formPanel = Forms.panel();
		formPanel.add(emailField);
		formPanel.add(currentPasswordField);
		formPanel.add(newPasswordField);
		formPanel.add(confirmNewPasswordField);
		formPanel.addButton(changeButton);
		
		initPopup(formPanel);
	}

	@Override
	public FormPanel getForm() {
		return formPanel;
	}

	@Override
	public Field<String> getEmailField() {
		return emailField;
	}

	@Override
	public TextField<String> getCurrentPasswordField() {
		return currentPasswordField;
	}

	@Override
	public TextField<String> getNewPasswordField() {
		return newPasswordField;
	}

	@Override
	public TextField<String> getConfirmNewPasswordField() {
		return confirmNewPasswordField;
	}
	
	@Override
	public Button getValidateButton() {
		return changeButton;
	}
	
}
