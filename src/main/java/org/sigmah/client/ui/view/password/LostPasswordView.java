package org.sigmah.client.ui.view.password;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.password.LostPasswordPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.util.EntityConstants;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * Lost password view.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LostPasswordView extends AbstractPopupView<PopupWidget> implements LostPasswordPresenter.View {

	private FormPanel formPanel;
	private TextField<String> emailTextBox;
	private Button loginButton;

	/**
	 * Popup initialization.
	 */
	public LostPasswordView() {
		super(new PopupWidget(true), 400);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		formPanel = Forms.panel();

		emailTextBox = Forms.text(I18N.CONSTANTS.loginLoginField(), true);
		emailTextBox.setRegex(EntityConstants.EMAIL_REGULAR_EXPRESSION);

		loginButton = Forms.button(I18N.CONSTANTS.ok());

		formPanel.add(emailTextBox);
		formPanel.addButton(loginButton);

		initPopup(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return formPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getEmailField() {
		return emailTextBox;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getValidateButton() {
		return loginButton;
	}

}
