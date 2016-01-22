package org.sigmah.client.ui.view.admin.models.importer;

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


import javax.inject.Inject;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.importer.AddImportationSchemeModelsAdminPresenter;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.base.EntityDTO;
import org.sigmah.shared.dto.importation.ImportationSchemeDTO;

import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.inject.Singleton;

/**
 * @author Mehdi Benabdeslam (mehdi.benabdeslam@netapsys.fr)
 */

@Singleton
public class AddImportationSchemeModelsAdminView extends AbstractPopupView<PopupWidget> implements AddImportationSchemeModelsAdminPresenter.View {

	private FormPanel mainPanel;
	private ComboBox<ImportationSchemeDTO> schemasCombo;
	private Button submitButton;
	private ListStore<ImportationSchemeDTO> schemasStore;

	@Inject
	protected AddImportationSchemeModelsAdminView() {
		super(new PopupWidget(true), 500);
	}

	@Override
	public void initialize() {

		mainPanel = Forms.panel();

		schemasCombo = Forms.combobox(I18N.CONSTANTS.adminImportationScheme(), true, EntityDTO.ID, ImportationSchemeDTO.NAME);
		schemasCombo.setFireChangeEventOnSetValue(true);

		schemasStore = new ListStore<ImportationSchemeDTO>();
		schemasCombo.setStore(schemasStore);

		mainPanel.add(schemasCombo);

		submitButton = Forms.button(I18N.CONSTANTS.save());

		mainPanel.add(submitButton);

		initPopup(mainPanel);

	}

	/**
	 * @return the schemasCombo
	 */
	@Override
	public ComboBox<ImportationSchemeDTO> getSchemasCombo() {
		return schemasCombo;
	}

	@Override
	public FormPanel getMainPanel() {
		return mainPanel;
	}

	@Override
	public Button getSubmitButton() {
		return submitButton;
	}

	@Override
	public ListStore<ImportationSchemeDTO> getSchemasStore() {
		return schemasStore;
	}

	@Override
	public void clearForm() {

		schemasCombo.clear();
		schemasCombo.clearSelections();
		schemasCombo.clearState();

	}
}
