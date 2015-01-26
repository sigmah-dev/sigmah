package org.sigmah.client.page.admin.model.common.element;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.shared.dto.element.BudgetElementDTO;
import org.sigmah.shared.dto.element.BudgetSubFieldDTO;

import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class BudgetSubFieldForm extends FormPanel {
	private Dispatcher dispatcher;
	private final TextField<String> nameField;
	private final BudgetElementDTO budgetElementToUpdate;
	private final Button saveButton;

	private final static int LABEL_WIDTH = 90;
	private final static int PANEL_HEIGHT = 200;
	private final static int PANEL_WIDTH = 400;

	public BudgetSubFieldForm(Dispatcher dispatcher, final MaskingAsyncMonitor maskingAsyncMonitor,
	                BudgetElementDTO budgetElement, BudgetSubFieldDTO subFieldToUpdate) {
		this.dispatcher = dispatcher;
		this.budgetElementToUpdate = budgetElement;

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);

		nameField = new TextField<String>();
		// SetFieldName
		nameField.setFieldLabel(I18N.CONSTANTS.adminBudgetSubFieldName());
		nameField.setAllowBlank(false);

		if (subFieldToUpdate != null && subFieldToUpdate.getId() > 0) {
			nameField.setValue(subFieldToUpdate.getLabel());
		}

		saveButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		add(nameField);
		add(saveButton);

		setWidth(PANEL_WIDTH);
		setHeight(PANEL_HEIGHT);

	}

	public Button getSaveButton() {
		return saveButton;
	}

	public void addBudgetFieldToBudgetElement() {
		BudgetSubFieldDTO budgetSubField = new BudgetSubFieldDTO();
		budgetSubField.setLabel(nameField.getValue());
		budgetSubField.setBudgetElementDTO(budgetElementToUpdate);
		budgetElementToUpdate.getBudgetSubFieldsDTO().add(budgetSubField);
	}

	public TextField<String> getNameField() {
		return nameField;
	}

}
