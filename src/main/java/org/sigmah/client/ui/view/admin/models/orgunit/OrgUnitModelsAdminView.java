package org.sigmah.client.ui.view.admin.models.orgunit;

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
import org.sigmah.client.ui.presenter.admin.models.orgunit.OrgUnitModelsAdminPresenter;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.OrgUnitModelDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;

import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.inject.Singleton;

/**
 * {@link OrgUnitModelsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class OrgUnitModelsAdminView extends AbstractModelsAdminView<OrgUnitModelDTO> implements OrgUnitModelsAdminPresenter.View {

	private TextField<String> nameField;
	private TextField<String> typeField;
	private ComboBox<EnumModel<ProjectModelStatus>> statusField;
	private CheckBox hasBudgetField;
	private CheckBox canContainProjectsField;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getColumnModel() {
		return new OrgUnitModelsColumnsProvider() {

			@Override
			GridEventHandler<OrgUnitModelDTO> getGridEventHandler() {
				return OrgUnitModelsAdminView.super.getGridEventHandler();
			}

		}.getColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<ProjectModelStatus>> getHeaderStatusField() {
		return statusField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormPanel buildHeaderForm() {

		nameField = Forms.text(I18N.CONSTANTS.adminProjectModelsName(), true);
		typeField = Forms.text(I18N.CONSTANTS.adminOrgUnitsModelTitle(), true);
		statusField = Forms.combobox(I18N.CONSTANTS.adminProjectModelsStatus(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		hasBudgetField = Forms.checkbox(I18N.CONSTANTS.adminOrgUnitsModelHasBudget(), null, I18N.CONSTANTS.adminOrgUnitsModelHasBudget(), false);
		canContainProjectsField =
				Forms.checkbox(I18N.CONSTANTS.adminOrgUnitsModelContainProjects(), null, I18N.CONSTANTS.adminOrgUnitsModelContainProjects(), false);

		final FormPanel headerForm = Forms.panel(170);

		headerForm.add(nameField);
		headerForm.add(typeField);
		headerForm.add(statusField);
		headerForm.add(getMaintenanceGroupField());
		headerForm.add(hasBudgetField);
		headerForm.add(canContainProjectsField);

		return headerForm;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String loadModelHeader(final OrgUnitModelDTO model) {
		nameField.setValue(model.getName());
		typeField.setValue(model.getTitle());
		statusField.setValue(new EnumModel<ProjectModelStatus>(model.getStatus()));
		getMaintenanceGroupField().setVisible(model.getStatus() == ProjectModelStatus.USED || model.isUnderMaintenance());
		getUnderMaintenanceField().setValue(model.getDateMaintenance() != null);
		getMaintenanceDateField().setValue(model.getDateMaintenance());
		getMaintenanceTimeField().setValue(model.getDateMaintenance() != null ? getMaintenanceTimeField().findModel(model.getDateMaintenance()) : null);
		hasBudgetField.setValue(model.getHasBudget());
		canContainProjectsField.setValue(model.getCanContainProjects());
		return model.getName();
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
	public Field<String> getTypeField() {
		return typeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getHasBudgetField() {
		return hasBudgetField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> getCanContainProjectsField() {
		return canContainProjectsField;
	}

}
