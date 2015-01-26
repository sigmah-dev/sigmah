package org.sigmah.client.page.admin.importation;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.icon.IconImageBundle;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.importation.VariableDTO;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Form to create a variable
 * 
 * @author gjb
 * 
 */
public class VariableForm extends FormPanel {
	private Dispatcher dispatcher;
	private final TextField<String> nameField;
	private final TextField<String> referenceField;
	private final Integer schemaToUpdate;
	private final VariableDTO variableToUpdate;

	private final static int LABEL_WIDTH = 90;
	private final static int PANEL_HEIGHT = 200;
	private final static int PANEL_WIDTH = 170;

	public VariableForm(Dispatcher dispatcher, final MaskingAsyncMonitor maskingAsyncMonitor,
	                final AsyncCallback<CreateResult> callback, Integer schema, VariableDTO variableToUpdate,
	                ImportationSchemeImportType type) {
		this.dispatcher = dispatcher;
		this.schemaToUpdate = schema;
		this.variableToUpdate = variableToUpdate;

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.importVariableName());
		nameField.setAllowBlank(false);

		referenceField = new TextField<String>();
		switch (type) {
		case ROW:
			referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceColumn());
			break;
		case SEVERAL:
			referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceCell());
			break;
		case UNIQUE:
			referenceField.setFieldLabel(I18N.CONSTANTS.adminImportReferenceSheetCell());
			break;
		default:
			break;

		}
		referenceField.setAllowBlank(false);

		if (variableToUpdate.getId() > 0) {
			nameField.setValue(variableToUpdate.getName());
			referenceField.setValue(variableToUpdate.getReference());
		}

		final Button saveButton = new Button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		saveButton.addListener(Events.OnClick, new Listener<BaseEvent>() {

			@Override
			public void handleEvent(BaseEvent be) {
				createVariable(maskingAsyncMonitor, callback);

			}
		});

		add(referenceField);
		add(nameField);
		add(saveButton);

		setWidth(PANEL_WIDTH);
		setHeight(PANEL_HEIGHT);

	}

	protected void createVariable(MaskingAsyncMonitor maskingAsyncMonitor, AsyncCallback<CreateResult> callback) {
		if (!this.isValid()) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
			                I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()), null);
			return;
		}
		Map<String, Object> newSchemaProperties = new HashMap<String, Object>();
		newSchemaProperties.put(AdminUtil.PROP_VAR_NAME, nameField.getValue());
		newSchemaProperties.put(AdminUtil.PROP_VAR_REFERENCE, referenceField.getValue());
		newSchemaProperties.put(AdminUtil.PROP_VAR_VARIABLE, variableToUpdate);
		ImportationSchemeDTO importationSchemeDTO = new ImportationSchemeDTO();
		importationSchemeDTO.setId(schemaToUpdate);
		newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, importationSchemeDTO);
		CreateEntity cmd = new CreateEntity("ImportationScheme", newSchemaProperties);
		dispatcher.execute(cmd, maskingAsyncMonitor, callback);

	}

}
