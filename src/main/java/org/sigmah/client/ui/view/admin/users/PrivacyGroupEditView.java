package org.sigmah.client.ui.view.admin.users;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.users.PrivacyGroupEditPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * PrivacyGroup create/edit popup view implementation.
 * 
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr) (v2.0)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class PrivacyGroupEditView extends AbstractPopupView<PopupWidget> implements PrivacyGroupEditPresenter.View {

	private FormPanel formPanel;
	private TextField<String> nameField;
	private NumberField codeField;
	private Button createButton;

	/**
	 * View popup initialization.
	 */
	public PrivacyGroupEditView() {
		super(new PopupWidget(true), 500);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		formPanel = Forms.panel(90);

		codeField = Forms.number(I18N.CONSTANTS.adminPrivacyGroupsCode(), true);

		nameField = Forms.text(I18N.CONSTANTS.adminPrivacyGroupsName(), true);

		createButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		formPanel.add(codeField);
		formPanel.add(nameField);
		formPanel.addButton(createButton);

		initPopup(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getCreateButton() {
		return createButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Number> getCodeField() {
		return codeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return formPanel;
	}

}
