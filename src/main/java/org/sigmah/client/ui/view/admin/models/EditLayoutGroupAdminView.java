package org.sigmah.client.ui.view.admin.models;

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

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.EditLayoutGroupAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.PhaseModelDTO;
import org.sigmah.shared.dto.base.EntityDTO;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.inject.Singleton;

/**
 * {@link EditLayoutGroupAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class EditLayoutGroupAdminView extends AbstractPopupView<PopupWidget> implements EditLayoutGroupAdminPresenter.View {

	private FormPanel form;
	private TextField<String> nameField;
	private ComboBox<BaseModelData> containerField;
	private SimpleComboBox<Integer> rowField;
	private CheckBox hasIterationsField;
	private Button saveButton;
	private Button deleteButton;

	/**
	 * Popup's initialization.
	 */
	public EditLayoutGroupAdminView() {
		super(new PopupWidget(true), 450);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		form = Forms.panel(130);

		// --
		// Name field.
		// --

		nameField = Forms.text(I18N.CONSTANTS.adminPrivacyGroupsName(), true);

		// --
		// Container field.
		// The value property should match OrgUnitDetails, ProjectDetails and PhaseModel 'name' property.
		// --

		containerField = Forms.combobox(I18N.CONSTANTS.adminFlexibleContainer(), true, EntityDTO.ID, PhaseModelDTO.NAME);

		// --
		// Row field (disabled by default).
		// --

		rowField = Forms.simpleCombobox(I18N.CONSTANTS.adminFlexibleGroupVPosition(), true);
		rowField.disable();

		// --
		// HasIterations field.
		// --

		hasIterationsField = Forms.checkbox("", null, I18N.CONSTANTS.adminFlexibleGroupHasIterations(), false);

		// --
		// Save button.
		// --

		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		deleteButton = Forms.button(I18N.CONSTANTS.formWindowDeleteAction(), IconImageBundle.ICONS.remove());

		form.add(nameField);
		form.add(containerField);
		form.add(rowField);
		
		form.add(hasIterationsField);
		
//		form.addButton(deleteButton);
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
	public ComboBox<BaseModelData> getContainerField() {
		return containerField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SimpleComboBox<Integer> getRowField() {
		return rowField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CheckBox getHasIterationsField() {
		return hasIterationsField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getDeleteButton() {
		return deleteButton;
	}

}
