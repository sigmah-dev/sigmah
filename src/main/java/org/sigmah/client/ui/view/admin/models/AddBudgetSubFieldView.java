package org.sigmah.client.ui.view.admin.models;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.AddBudgetSubFieldPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddBudgetSubFieldView extends AbstractPopupView<PopupWidget> implements AddBudgetSubFieldPresenter.View {

	private TextField<String> nameField;
	private Button saveButton;
	private FormPanel form;

	public AddBudgetSubFieldView() {
		super(new PopupWidget(true), 400);
	}

	@Override
	public void initialize() {

		form = Forms.panel();

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.adminBudgetSubFieldName());
		nameField.setAllowBlank(false);

		saveButton = new Button(I18N.CONSTANTS.save(), org.sigmah.client.ui.res.icon.IconImageBundle.ICONS.save());

		form.add(nameField);
		form.add(saveButton);

		form.setAutoHeight(true);
		form.setAutoWidth(true);

		initPopup(form);

	}

	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public TextField<String> getNameField() {
		return nameField;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

}
