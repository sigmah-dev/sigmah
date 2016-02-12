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

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.user.client.ui.Label;
import java.util.Arrays;
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
	private AutomatedImportResultPopup automatedImportResultPopup;
	
	private Radio massImportRadio;
	private CheckBox newProjectsPolicyField;
	private CheckBox projectCorePolicyField;
	private Radio multipleMatchRadio;
	
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
		
		// Automated import result popup.
		automatedImportResultPopup = new AutomatedImportResultPopup();
		automatedImportResultPopup.initialize();
		
		// Scheme field.
		schemeField = Forms.combobox(I18N.CONSTANTS.adminImportationScheme(), true, ImportationSchemeDTO.ID, ImportationSchemeDTO.NAME);
		schemeField.setName(FileUploadUtils.DOCUMENT_ID);
		
		// File field.
		fileField = Forms.upload(I18N.CONSTANTS.adminFileImport());
		fileField.setName(FileUploadUtils.DOCUMENT_CONTENT);
		
		// Mass import fields.
		massImportRadio = Forms.radio(I18N.CONSTANTS.importationModeMass(), Boolean.TRUE);
		final RadioGroup massImportRadioGroup = Forms.radioGroup(I18N.CONSTANTS.importationMode(), Style.Orientation.VERTICAL, 
				massImportRadio, Forms.radio(I18N.CONSTANTS.importationModeWithControl()));
		
		newProjectsPolicyField = Forms.checkbox(I18N.CONSTANTS.importationMassParameterCreateNewProjects());
		projectCorePolicyField = Forms.checkbox(I18N.CONSTANTS.importationMassParameterUnlockCores());
		multipleMatchRadio = Forms.radio(I18N.CONSTANTS.importationMassParameterMultipleMatchesAll());
		final RadioGroup multipleMatchRadioGroup = Forms.radioGroup("", multipleMatchRadio, Forms.radio(I18N.CONSTANTS.importationMassParameterMultipleMatchesNone(), Boolean.TRUE));
		
		for (final Field<?> field : Arrays.asList(newProjectsPolicyField, projectCorePolicyField, multipleMatchRadioGroup)) {
			field.setHideLabel(true);
		}
		
		// Import button.
		importButton = Forms.button(I18N.CONSTANTS.importItem());
		
		// Building the form.
		form = Forms.panel();
		form.add(schemeField);
		form.add(fileField);
		form.add(massImportRadioGroup);
		form.add(createText(I18N.CONSTANTS.importationMassParameters(), true));
		form.add(newProjectsPolicyField);
		form.add(projectCorePolicyField);
		form.add(createText(I18N.CONSTANTS.importationMassParameterMultipleMatches(), false));
		form.add(multipleMatchRadioGroup);
		form.addButton(importButton);
		
		initPopup(form);
	}
	
	/**
	 * Creates a simple text element. Since it is not a {@link Field}, it will
	 * take the full width of the layout.
	 * 
	 * @param text
	 *          Text du use.
	 * @param bold
	 *          <code>true</code> to set <code>font-weight</code> CSS property as <code>bold</code>,
	 *          <code>false</code> otherwise.
	 * @return A new <code>Label</code> containing the given text.
	 */
	private Label createText(final String text, final boolean bold) {
		final Label label = new Label(text);
		label.addStyleName("x-form-item");
		if (bold) {
			label.getElement().getStyle().setFontWeight(com.google.gwt.dom.client.Style.FontWeight.BOLD);
		}
		return label;
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
		return massImportRadio;
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
	public Radio getMultipleMatchPolicyField() {
		return multipleMatchRadio;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public AutomatedImportResultPopup getAutomatedImportResultPopup() {
		return automatedImportResultPopup;
	}
}
