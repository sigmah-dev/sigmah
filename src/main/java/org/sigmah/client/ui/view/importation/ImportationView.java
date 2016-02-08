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
import com.extjs.gxt.ui.client.widget.form.CheckBox;
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
	private CheckBox automatedField;
	private CheckBox newProjectsPolicyField;
	private CheckBox projectCorePolicyField;
	private CheckBox multipleMatchPolicyField;
	
	public ImportationView() {
		super(new PopupWidget(true));
	}
	
	/**
	 * {@inheritDoc}
	 */
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
		
		// Automated import fields.
		automatedField = Forms.checkbox("", null, I18N.CONSTANTS.USE_AUTOMATED_IMPORT(), true);
		newProjectsPolicyField = Forms.checkbox(I18N.CONSTANTS.AUTOMATED_IMPORT_CREATE_PROJECTS_YES(), null, I18N.CONSTANTS.AUTOMATED_IMPORT_CREATE_PROJECTS(), false);
		projectCorePolicyField = Forms.checkbox(I18N.CONSTANTS.AUTOMATED_IMPORT_CORE_UNLOCK_YES(), null, I18N.CONSTANTS.AUTOMATED_IMPORT_CORE_UNLOCK(), false);
		multipleMatchPolicyField = Forms.checkbox(I18N.CONSTANTS.AUTOMATED_IMPORT_MULTIPLE_MATCHES_UPDATE_YES(), null, I18N.CONSTANTS.AUTOMATED_IMPORT_MULTIPLE_MATCHES_UPDATE(), false);
		
		// Import button.
		importButton = Forms.button(I18N.CONSTANTS.importItem());
		
		// Building the form.
		form = Forms.panel();
		form.add(schemeField);
		form.add(fileField);
		form.add(automatedField);
		form.add(newProjectsPolicyField);
		form.add(projectCorePolicyField);
		form.add(multipleMatchPolicyField);
		form.addButton(importButton);
		
		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel[] getForms() {
		return new FormPanel[] {
			form
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<ImportationSchemeDTO> getSchemeField() {
		return schemeField;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public FileUploadField getFileField() {
		return fileField;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getAutomatedField() {
		return automatedField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getNewProjectsPolicyField() {
		return newProjectsPolicyField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getProjectCorePolicyField() {
		return projectCorePolicyField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getMultipleMatchPolicyField() {
		return multipleMatchPolicyField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getImportButton() {
		return importButton;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ListStore<ImportationSchemeDTO> getSchemeListStore() {
		return schemeField.getStore();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImportDetailsPopup getImportDetailsPopup() {
		return importDetailsPopup;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ElementExtractedValuePopup getElementExtractedValuePopup() {
		return elementExtractedValuePopup;
	}
}
