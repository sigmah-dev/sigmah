package org.sigmah.client.ui.view.importation;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.importation.ImportationPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;

/**
 * View of {@link ImportationPresenter}.
 * <p/>
 * This view is a popup that contains a a file selector and a combo box to 
 * select the import scheme to use.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImportationView extends AbstractPopupView<PopupWidget> implements ImportationPresenter.View {

	private FormPanel form;
	
	private ComboBox<ImportationSchemeDTO> schemeField;
	private FileUploadField fileField;
	
	private Button importButton;
	
	private ImportDetailsPopup importDetailsPopup;
	private ElementExtractedValuePopup elementExtractedValuePopup;
	
	public ImportationView() {
		super(new PopupWidget(true));
	}
	
	@Override
	public void initialize() {
		// Importation details popup.
		importDetailsPopup = new ImportDetailsPopup();
		importDetailsPopup.initialize();
		
		// Element extracted value popup.
		elementExtractedValuePopup = new ElementExtractedValuePopup();
		elementExtractedValuePopup.initialize();
		
		// Scheme field.
		schemeField = Forms.combobox(I18N.CONSTANTS.adminImportationScheme(), true, ImportationSchemeDTO.ID, ImportationSchemeDTO.NAME);
		schemeField.setName(FileUploadUtils.DOCUMENT_ID);
		
		// File field.
		fileField = Forms.upload(I18N.CONSTANTS.adminFileImport());
		fileField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		
		// Import button.
		importButton = Forms.button(I18N.CONSTANTS.importItem());
		
		// Building the form.
		form = Forms.panel();
		form.add(schemeField);
		form.add(fileField);
		form.addButton(importButton);
		
		initPopup(form);
	}

	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] {
			form
		};
	}

	@Override
	public Field<ImportationSchemeDTO> getSchemeField() {
		return schemeField;
	}

	@Override
	public FileUploadField getFileField() {
		return fileField;
	}

	@Override
	public Button getImportButton() {
		return importButton;
	}
	
	@Override
	public ListStore<ImportationSchemeDTO> getSchemeListStore() {
		return schemeField.getStore();
	}

	@Override
	public ImportDetailsPopup getImportDetailsPopup() {
		return importDetailsPopup;
	}
	
	@Override
	public ElementExtractedValuePopup getElementExtractedValuePopup() {
		return elementExtractedValuePopup;
	}
}
