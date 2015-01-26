package org.sigmah.client.ui.view.admin.importation;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.importation.AddVariableImporationSchemePresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class AddVariableImporationSchemeView extends AbstractPopupView<PopupWidget> implements AddVariableImporationSchemePresenter.View {

	private TextField<String> nameField;
	private TextField<String> referenceField;
	private Button saveButton;
	private FormPanel mainPanel;

	private static final int LABEL_WIDTH = 90;

	public AddVariableImporationSchemeView() {
		super(new PopupWidget(true), 550);
	}

	/**
	 * init panel
	 */

	@Override
	public void initialize() {

		mainPanel = Forms.panel();

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		mainPanel.setLayout(layout);

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.importVariableName());
		nameField.setAllowBlank(false);

		referenceField = new TextField<String>();

		// TODO
		/*
		 * switch (type) { case ROW: referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceColumn()); break; case
		 * SEVERAL: referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceCell()); break; case UNIQUE:
		 * referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceSheetCell()); break; default: break; }
		 */

		referenceField.setAllowBlank(false);

		// TODO case Update
		/*
		 * if (variableToUpdate.getId() > 0) { nameField.setValue(variableToUpdate.getName());
		 * referenceField.setValue(variableToUpdate.getReference()); }
		 */

		saveButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		mainPanel.add(referenceField);
		mainPanel.add(nameField);
		mainPanel.add(saveButton);

		initPopup(mainPanel);

	}

	@Override
	public TextField<String> getNameField() {
		return nameField;
	}

	@Override
	public TextField<String> getReferenceField() {
		return referenceField;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	@Override
	public FormPanel getMainPanel() {
		return mainPanel;
	}

	@Override
	public void clearForm() {

		nameField.clear();
		referenceField.clear();

	}
}
