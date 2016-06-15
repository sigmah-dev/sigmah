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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.EditGroupsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.shared.dto.GroupsDTO;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.inject.Singleton;

/**
 * {@link EditGroupsAdminPresenter}'s view implementation.
 */
@Singleton
public class EditGroupsAdminView extends AbstractPopupView<PopupWidget> implements EditGroupsAdminPresenter.View {

	private FormPanel form;
	private TextField<String> nameField;
	private ComboBox<BaseModelData> containerField;
	private NumberField positionField;
	private Button saveButton;

	/**
	 * Builds the view.
	 */
	public EditGroupsAdminView() {
		super(new PopupWidget(true), 500);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		// Form.
		
		form = Forms.panel(130);

		// Name field.

		nameField = Forms.text(I18N.CONSTANTS.adminGroupsName(), true);

		// Container Field.		

		containerField = Forms.combobox(I18N.CONSTANTS.adminGroupsContainer(), true, EntityDTO.ID, PhaseModelDTO.NAME);
		containerField.setEmptyText(I18N.CONSTANTS.adminGroupsContainerChoice());
		containerField.setFireChangeEventOnSetValue(true);

		// Vertical Position Field

		positionField = Forms.number(I18N.CONSTANTS.adminGroupsPosition(), true, false);

		// Save button.

		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());


		// View initialization.

		form.add(nameField);
		form.add(containerField);
		form.add(positionField);
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
	public Field<Number> getPositionField() {
		return positionField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	
}
