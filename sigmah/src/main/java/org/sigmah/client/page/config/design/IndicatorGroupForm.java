package org.sigmah.client.page.config.design;

import org.sigmah.client.i18n.UIConstants;

import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;

public class IndicatorGroupForm extends AbstractDesignForm {

	private FormBinding binding;
	
	public IndicatorGroupForm() {
		super();

		binding = new FormBinding(this);

		UIConstants constants = GWT.create(UIConstants.class);

		this.setLabelWidth(150);
		this.setFieldWidth(200);

		TextField<String> nameField = new TextField<String>();
		nameField.setFieldLabel(constants.name());
		nameField.setAllowBlank(false);
		nameField.setMaxLength(128);
		binding.addFieldBinding(new FieldBinding(nameField, "name"));
		this.add(nameField);
	}

	public int getPreferredDialogHeight() {
        return 200;
    }
	
	@Override
	public FormBinding getBinding() {
		return binding;
	}

}
