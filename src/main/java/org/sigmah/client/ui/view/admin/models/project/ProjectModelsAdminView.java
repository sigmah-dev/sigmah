package org.sigmah.client.ui.view.admin.models.project;

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


import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.form.AdapterField;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.google.inject.Singleton;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter;
import org.sigmah.client.ui.presenter.admin.models.project.ProjectModelsAdminPresenter.ProjectTypeProvider;
import org.sigmah.client.ui.view.admin.models.base.AbstractModelsAdminView;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ListComboBox;
import org.sigmah.client.ui.widget.form.ProjectModelTypeField;
import org.sigmah.client.util.EnumModel;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.profile.ProfileDTO;
import org.sigmah.shared.dto.referential.ProjectModelStatus;
import org.sigmah.shared.dto.referential.ProjectModelType;

/**
 * {@link ProjectModelsAdminPresenter}'s view implementation.
 *
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
@Singleton
public class ProjectModelsAdminView extends AbstractModelsAdminView<ProjectModelDTO> implements ProjectModelsAdminPresenter.View {

	private TextField<String> nameField;
	private ComboBox<EnumModel<ProjectModelStatus>> statusField;
	private ProjectModelTypeField modelTypeField;
	private ProjectTypeProvider projectTypeProvider;
	private ListComboBox<ProfileDTO> defaultProfiles;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ColumnModel getColumnModel() {
		return new ProjectModelsColumnsProvider() {

			@Override
			GridEventHandler<ProjectModelDTO> getGridEventHandler() {
				return ProjectModelsAdminView.super.getGridEventHandler();
			}

			@Override
			ProjectTypeProvider getProjectTypeProvider() {
				return projectTypeProvider;
			}

		}.getColumnModel();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectTypeProvider(final ProjectTypeProvider provider) {
		this.projectTypeProvider = provider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComboBox<EnumModel<ProjectModelStatus>> getHeaderStatusField() {
		return statusField;
	}

	@Override
	public ListComboBox<ProfileDTO> getDefaultProfilesComboBox() {
		return defaultProfiles;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected FormPanel buildHeaderForm() {

		nameField = Forms.text(I18N.CONSTANTS.adminProjectModelsName(), true);
		statusField = Forms.combobox(I18N.CONSTANTS.adminProjectModelsStatus(), true, EnumModel.VALUE_FIELD, EnumModel.DISPLAY_FIELD);
		modelTypeField = new ProjectModelTypeField(I18N.CONSTANTS.adminProjectModelType(), true, Orientation.VERTICAL);

		defaultProfiles = new ListComboBox<ProfileDTO>(ProfileDTO.ID, ProfileDTO.NAME);
		defaultProfiles.setNoAvailableValueTooltip(I18N.CONSTANTS.noAvailableProfileToAddInDefaultTeamMemberProfiles());
		defaultProfiles.initComponent();
		defaultProfiles.addStyleName("admin__project-models__default-team-member-profiles");

		final FormPanel headerForm = Forms.panel(140);
		headerForm.setAutoHeight(true);

		headerForm.add(nameField);
		headerForm.add(statusField);
		headerForm.add(getMaintenanceGroupField());
		headerForm.add(modelTypeField);
		headerForm.add(Forms.adapter(I18N.CONSTANTS.adminProjectModelsDefaultProfilesForTeamMembers(), defaultProfiles));
		return headerForm;
	}

	@Override
	public float getDetailsHeaderFormHeight() {
		return 250f;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String loadModelHeader(final ProjectModelDTO model) {
		nameField.setValue(model.getName());
		statusField.setValue(new EnumModel<ProjectModelStatus>(model.getStatus()));
		modelTypeField.setValue(projectTypeProvider.getProjectModelType(model));
		getMaintenanceGroupField().setVisible(model.getStatus() == ProjectModelStatus.USED || model.isUnderMaintenance());
		getUnderMaintenanceField().setValue(model.getDateMaintenance() != null);
		getMaintenanceDateField().setValue(model.getDateMaintenance());
		getMaintenanceTimeField().setValue(model.getDateMaintenance() != null ? getMaintenanceTimeField().findModel(model.getDateMaintenance()) : null);
		statusField.setEnabled(model.getDateMaintenance() == null);

		defaultProfiles.getListStore().removeAll();
		defaultProfiles.getListStore().add(model.getDefaultTeamMemberProfiles());

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
	public Field<ProjectModelType> getProjectModelTypeField() {
		return modelTypeField;
	}

}
