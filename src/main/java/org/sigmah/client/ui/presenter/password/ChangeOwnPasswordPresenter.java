package org.sigmah.client.ui.presenter.password;

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
