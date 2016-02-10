package org.sigmah.client.ui.view.importation;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

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
