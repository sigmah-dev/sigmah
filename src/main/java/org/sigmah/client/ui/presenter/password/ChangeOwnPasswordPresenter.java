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

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.page.Page;
import org.sigmah.client.page.PageRequest;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.base.AbstractPagePresenter;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.view.password.ChangeOwnPasswordView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.shared.command.ChangePasswordCommand;
import org.sigmah.shared.command.result.VoidResult;

/**
 * Change own password presenter.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class ChangeOwnPasswordPresenter extends AbstractPagePresenter<ChangeOwnPasswordPresenter.View> {
	
	/**
	 * Description of the view managed by this presenter.
	 */
	@ImplementedBy(ChangeOwnPasswordView.class)
	public static interface View extends ViewInterface {

		FormPanel getForm();

		Field<String> getEmailField();
		
		Field<String> getCurrentPasswordField();

		Field<String> getNewPasswordField();

		Field<String> getConfirmNewPasswordField();

		Button getValidateButton();

	}
	
	/**
	 * Presenters's initialization.
	 * 
	 * @param view
	 *          Presenter's view interface.
	 * @param injector
	 *          Injected client injector.
	 */
	@Inject
	public ChangeOwnPasswordPresenter(final ChangeOwnPasswordPresenter.View view, final Injector injector) {
		super(view, injector);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page getPage() {
		return Page.CHANGE_OWN_PASSWORD;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {
		setPageTitle(I18N.CONSTANTS.changePassword());
		
		view.getValidateButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(ButtonEvent ce) {
				onChangePasswordAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onPageRequest(final PageRequest request) {
		view.getForm().clearAll();
		view.getEmailField().setValue(auth().getUserEmail());
	}
	
	// ----------------------------------------------------------------------------------------------
	//
	// ACTIONS HANDLERS.
	//
	// ----------------------------------------------------------------------------------------------

	/**
	 * Handler executed on validate button click.
	 */
	private void onChangePasswordAction() {
		final String currentPassword = view.getCurrentPasswordField().getValue();
		final String newPassword = view.getNewPasswordField().getValue();
		final String confirmNewPassword = view.getConfirmNewPasswordField().getValue();

		if (atLeastOneIsEmpty(currentPassword, newPassword, confirmNewPassword)) {
			N10N.errorNotif(I18N.CONSTANTS.error(), I18N.CONSTANTS.formWindowFieldsUnfilledDetails());
			
		} else if (!newPassword.equals(confirmNewPassword)) {
			N10N.errorNotif(I18N.CONSTANTS.error(), I18N.CONSTANTS.passwordNotMatch());
			
		} else {
			dispatch.execute(new ChangePasswordCommand(currentPassword, newPassword, confirmNewPassword), new CommandResultHandler<VoidResult>() {

				@Override
				protected void onCommandSuccess(VoidResult result) {
					hideView();
					N10N.infoNotif(I18N.CONSTANTS.infoConfirmation(), I18N.CONSTANTS.passwordChanged());
				}

			}, view.getValidateButton());
		}
	}
	
	private boolean atLeastOneIsEmpty(String... values) {
		for(final String value : values) {
			if(value == null || value.isEmpty()) {
				return true;
			}
		}
		return false;
	}
}
