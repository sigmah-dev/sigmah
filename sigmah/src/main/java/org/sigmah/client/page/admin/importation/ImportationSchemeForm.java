package org.sigmah.client.page.admin.importation;

import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.monitor.MaskingAsyncMonitor;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.page.admin.AdminUtil;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.domain.importation.ImportationSchemeFileFormat;
import org.sigmah.shared.domain.importation.ImportationSchemeImportType;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportationSchemeForm extends FormPanel {
	private final Dispatcher dispatcher;
	private final TextField<String> nameField;
	private final Radio csvRadio;
	private final Radio odsRadio;
	private final Radio excelRadio;
	private final Radio uniqueRadio;
	private final Radio severalRadio;
	private final Radio lineRadio;
	private final RadioGroup importTypeGroup;
	private final RadioGroup fileFormatGroup;
	private ImportationSchemeFileFormat currentFileFormat;
	private ImportationSchemeImportType currentImportType;

	private final static int LABEL_WIDTH = 220;
	private final static int PANEL_HEIGHT = 250;
	private final static int PANEL_WIDTH = 600;

	public ImportationSchemeForm(Dispatcher dispatcher, final MaskingAsyncMonitor maskingAsyncMonitor,
	                final AsyncCallback<CreateResult> callback, final ImportationSchemeDTO schemaToUpdate) {
		this.dispatcher = dispatcher;

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		setLayout(layout);

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.importSchemeName());
		nameField.setAllowBlank(false);

		add(nameField);

		fileFormatGroup = new RadioGroup("fileFormat");
		fileFormatGroup.setFieldLabel(I18N.CONSTANTS.importSchemeFileFormat());
		fileFormatGroup.setFireChangeEventOnSetValue(true);

		csvRadio = createRadio(I18N.CONSTANTS.csv(), true);
		currentFileFormat = ImportationSchemeFileFormat.CSV;
		currentImportType = ImportationSchemeImportType.ROW;

		odsRadio = createRadio(I18N.CONSTANTS.ods(), false);
		excelRadio = createRadio(I18N.CONSTANTS.excel(), false);

		fileFormatGroup.add(csvRadio);
		fileFormatGroup.add(odsRadio);
		fileFormatGroup.add(excelRadio);

		add(fileFormatGroup);

		uniqueRadio = createRadio(I18N.CONSTANTS.adminImportSchemeFileImportTypeUnique(), false);
		severalRadio = createRadio(I18N.CONSTANTS.adminImportSchemeFileImportTypeSeveral(), false);
		lineRadio = createRadio(I18N.CONSTANTS.adminImportSchemeFileImportTypeLine(), true);

		importTypeGroup = new RadioGroup("importType");
		importTypeGroup.setFieldLabel(I18N.CONSTANTS.adminImportSchemeFileImportType());
		importTypeGroup.setOrientation(Orientation.VERTICAL);

		importTypeGroup.add(uniqueRadio);
		importTypeGroup.add(severalRadio);
		importTypeGroup.add(lineRadio);

		// The default value is CSV
		lineRadio.setValue(true);
		importTypeGroup.disable();

		add(importTypeGroup);

		// Adds actions on filter by model type.
		for (final ImportationSchemeFileFormat type : ImportationSchemeFileFormat.values()) {
			getFileFormatRadioFilter(type).addListener(Events.Change, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					if (Boolean.TRUE.equals(be.getValue())) {
						currentFileFormat = type;
					}

					// For csv files, it will automatically consider each line
					// as a project
					if (type == ImportationSchemeFileFormat.CSV) {
						currentImportType = ImportationSchemeImportType.ROW;
						lineRadio.setValue(true);
						importTypeGroup.disable();
					} else {
						importTypeGroup.enable();
					}
				}
			});
		}

		for (final ImportationSchemeImportType type : ImportationSchemeImportType.values()) {
			getImportTypeRadioFilter(type).addListener(Events.Change, new Listener<FieldEvent>() {

				@Override
				public void handleEvent(FieldEvent be) {
					if (Boolean.TRUE.equals(be.getValue())) {
						currentImportType = type;
					}
				}
			});
		}

		if (schemaToUpdate.getId() > 0) {
			nameField.setValue(schemaToUpdate.getName());

			// Prevent changing the import type and file format to avoid
			// incoherence
			importTypeGroup.hide();
			fileFormatGroup.hide();

		}

		// Create button.
		final Button createButton = new Button(I18N.CONSTANTS.save());
		createButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			@Override
			public void handleEvent(ButtonEvent be) {
				createSchema(maskingAsyncMonitor, callback, schemaToUpdate);
			}
		});
		add(createButton);

		setSize(PANEL_WIDTH, PANEL_HEIGHT);
	}

	protected void createSchema(MaskingAsyncMonitor maskingAsyncMonitor, AsyncCallback<CreateResult> callback,
	                ImportationSchemeDTO model) {
		if (!this.isValid()) {
			MessageBox.alert(I18N.CONSTANTS.createFormIncomplete(),
			                I18N.MESSAGES.createFormIncompleteDetails(I18N.CONSTANTS.adminImportationScheme()), null);
			return;
		}
		Map<String, Object> newSchemaProperties = new HashMap<String, Object>();
		newSchemaProperties.put(AdminUtil.ADMIN_SCHEMA, model);
		newSchemaProperties.put(AdminUtil.PROP_SCH_NAME, nameField.getValue());
		newSchemaProperties.put(AdminUtil.PROP_SCH_FILE_FORMAT, currentFileFormat);

		newSchemaProperties.put(AdminUtil.PROP_SCH_IMPORT_TYPE, currentImportType);
		CreateEntity cmd = new CreateEntity("ImportationScheme", newSchemaProperties);
		dispatcher.execute(cmd, maskingAsyncMonitor, callback);

	}

	private Radio getFileFormatRadioFilter(ImportationSchemeFileFormat type) {

		if (type != null) {
			switch (type) {
			case CSV:
				return csvRadio;
			case MS_EXCEL:
				return excelRadio;
			case ODS:
				return odsRadio;
			default:
				break;
			}
		}
		return null;
	}

	private Radio getImportTypeRadioFilter(ImportationSchemeImportType type) {

		if (type != null) {
			switch (type) {
			case ROW:
				return lineRadio;
			case SEVERAL:
				return severalRadio;
			case UNIQUE:
				return uniqueRadio;
			default:
				return null;
			}
		}
		return null;
	}

	private Radio createRadio(String boxLabel, Boolean value) {
		final Radio radio = new Radio();
		radio.setFireChangeEventOnSetValue(true);
		radio.setValue(value);
		radio.setBoxLabel(boxLabel);
		radio.setHideLabel(false);
		radio.addStyleName("toolbar-radio");
		return radio;
	}

}
