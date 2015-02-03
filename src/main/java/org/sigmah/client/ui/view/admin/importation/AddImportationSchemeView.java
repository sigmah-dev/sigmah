package org.sigmah.client.ui.view.admin.importation;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.importation.AddImportationSchemePresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.referential.ImportationSchemeFileFormat;
import org.sigmah.shared.dto.referential.ImportationSchemeImportType;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */
@Singleton
public class AddImportationSchemeView extends AbstractPopupView<PopupWidget> implements AddImportationSchemePresenter.View {

	private TextField<String> nameField;
	private Radio csvRadio;
	private Radio odsRadio;
	private Radio excelRadio;
	private Radio uniqueRadio;
	private Radio severalRadio;
	private Radio lineRadio;
	private RadioGroup importTypeGroup;
	private RadioGroup fileFormatGroup;
	private FormPanel formPanel;
	private ImportationSchemeFileFormat currentFileFormat;
	private ImportationSchemeImportType currentImportType;
	private Button createButton;

	private static final int LABEL_WIDTH = 220;

	public AddImportationSchemeView() {
		super(new PopupWidget(true), 550);
	}

	/**
	 * Popup initialization.
	 */

	@Override
	public void initialize() {

		formPanel = Forms.panel();
		formPanel.setAutoHeight(true);
		formPanel.setAutoWidth(true);

		FormLayout layout = new FormLayout();
		layout.setLabelWidth(LABEL_WIDTH);
		formPanel.setLayout(layout);

		nameField = new TextField<String>();
		nameField.setFieldLabel(I18N.CONSTANTS.importSchemeName());
		nameField.setAllowBlank(false);

		formPanel.add(nameField);

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

		formPanel.add(fileFormatGroup);

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

		formPanel.add(importTypeGroup);

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

		// Create button.
		createButton = new Button(I18N.CONSTANTS.save());

		formPanel.add(createButton);

		initPopup(formPanel);

	}

	@Override
	public Radio getFileFormatRadioFilter(ImportationSchemeFileFormat type) {

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

	@Override
	public Radio getImportTypeRadioFilter(ImportationSchemeImportType type) {

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

	@Override
	public TextField<String> getNameField() {
		return nameField;
	}

	@Override
	public Radio getCsvRadio() {
		return csvRadio;
	}

	@Override
	public Radio getOdsRadio() {
		return odsRadio;
	}

	@Override
	public Radio getExcelRadio() {
		return excelRadio;
	}

	@Override
	public Radio getUniqueRadio() {
		return uniqueRadio;
	}

	@Override
	public Radio getSeveralRadio() {
		return severalRadio;
	}

	@Override
	public Radio getLineRadio() {
		return lineRadio;
	}

	@Override
	public RadioGroup getImportTypeGroup() {
		return importTypeGroup;
	}

	@Override
	public RadioGroup getFileFormatGroup() {
		return fileFormatGroup;
	}

	@Override
	public FormPanel getFormPanel() {
		return formPanel;
	}

	@Override
	public ImportationSchemeFileFormat getCurrentFileFormat() {
		return currentFileFormat;
	}

	@Override
	public ImportationSchemeImportType getCurrentImportType() {
		return currentImportType;
	}

	@Override
	public Button getCreateButton() {
		return createButton;
	}

	@Override
	public void clearForm() {

		getNameField().clear();

		csvRadio.setValue(true);
		odsRadio.setValue(false);
		excelRadio.setValue(false);

		lineRadio.setValue(true);
		lineRadio.setEnabled(false);

		uniqueRadio.setValue(false);
		uniqueRadio.setEnabled(false);

		severalRadio.setValue(false);
		severalRadio.setEnabled(false);

		importTypeGroup.show();
		fileFormatGroup.show();
	}
}
