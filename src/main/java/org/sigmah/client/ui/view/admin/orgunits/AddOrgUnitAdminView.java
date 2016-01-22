package org.sigmah.client.ui.view.admin.orgunits;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.orgunits.AddOrgUnitAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.util.EntityConstants;

import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * {@link AddOrgUnitAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class AddOrgUnitAdminView extends AbstractPopupView<PopupWidget> implements AddOrgUnitAdminPresenter.View {

	private FormPanel form;
	private TextField<String> nameField;
	private TextField<String> fullNameField;
	private ComboBox<CountryDTO> countryField;
	private ComboBox<OrgUnitModelDTO> modelField;
	private Button saveButton;

	/**
	 * Builds the view.
	 */
	public AddOrgUnitAdminView() {
		super(new PopupWidget(true), 500);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// --
		// Form.
		// --

		form = Forms.panel(90);

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminOrgUnitCode(), true);
		nameField.setMaxLength(EntityConstants.ORG_UNIT_NAME_MAX_LENGTH);

		// --
		// Full name field.
		// --

		fullNameField = Forms.text(I18N.CONSTANTS.adminOrgUnitTitle(), true);
		fullNameField.setMaxLength(EntityConstants.ORG_UNIT_FULL_NAME_MAX_LENGTH);

		// --
		// Country field.
		// --

		countryField = Forms.combobox(I18N.CONSTANTS.adminOrgUnitCountry(), true, CountryDTO.ID, CountryDTO.NAME);
		countryField.setEmptyText(I18N.CONSTANTS.flexibleElementDefaultSelectCountry());
		countryField.setTriggerAction(TriggerAction.ALL);

		countryField.getStore().addStoreListener(new StoreListener<CountryDTO>() {

			@Override
			public void storeAdd(final StoreEvent<CountryDTO> se) {
				countryField.setEnabled(true);
			}

			@Override
			public void storeClear(final StoreEvent<CountryDTO> se) {
				countryField.setEnabled(false);
			}

		});

		// --
		// Model field.
		// --

		modelField = Forms.combobox(I18N.CONSTANTS.adminOrgUnitModel(), true, OrgUnitModelDTO.ID, OrgUnitModelDTO.NAME);
		modelField.setEmptyText(I18N.CONSTANTS.adminOrgUnitModelEmptyChoice());
		modelField.setTriggerAction(TriggerAction.ALL);

		modelField.getStore().addStoreListener(new StoreListener<OrgUnitModelDTO>() {

			@Override
			public void storeAdd(final StoreEvent<OrgUnitModelDTO> se) {
				modelField.setEnabled(true);
			}

			@Override
			public void storeClear(final StoreEvent<OrgUnitModelDTO> se) {
				modelField.setEnabled(false);
			}

		});

		// --
		// Save button.
		// --

		saveButton = Forms.button(I18N.CONSTANTS.adminOrgUnitCreateButton(), IconImageBundle.ICONS.add());

		// --
		// View initialization.
		// --

		form.add(nameField);
		form.add(fullNameField);
		form.add(countryField);
		form.add(modelField);
		form.addButton(saveButton);

		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return form;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getFullNameField() {
		return fullNameField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<CountryDTO> getCountryField() {
		return countryField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<OrgUnitModelDTO> getModelField() {
		return modelField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

}
