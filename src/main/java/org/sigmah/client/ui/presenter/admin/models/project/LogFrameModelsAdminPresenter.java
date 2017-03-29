package org.sigmah.client.ui.presenter.admin.models.project;

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

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.CommandResultHandler;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.inject.Injector;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.presenter.admin.models.base.IsModelTabPresenter;
import org.sigmah.client.ui.presenter.base.AbstractPresenter;
import org.sigmah.client.ui.view.admin.models.project.LogFrameModelsAdminView;
import org.sigmah.client.ui.view.base.ViewInterface;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.util.AdminUtil;
import org.sigmah.client.util.ClientUtils;
import org.sigmah.client.util.IntegerModel;
import org.sigmah.shared.command.CreateEntity;
import org.sigmah.shared.command.result.CreateResult;
import org.sigmah.shared.dto.ProjectModelDTO;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.inject.ImplementedBy;
import com.google.inject.Inject;

/**
 * Logical frame models administration presenter that manages the {@link LogFrameModelsAdminView}.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class LogFrameModelsAdminPresenter extends AbstractPresenter<LogFrameModelsAdminPresenter.View>
																																																			implements
																																																			IsModelTabPresenter<ProjectModelDTO, LogFrameModelsAdminPresenter.View> {

	/**
	 * Description of the view managed by this presenter.
	 */
	public static interface View extends ViewInterface {

		/**
		 * Returns the form containing all the fields.
		 * 
		 * @return The form containing all the fields.
		 */
		FormPanel getForm();

		/**
		 * Returns the name field.
		 * 
		 * @return The name field.
		 */
		Field<String> getNameField();

		/**
		 * Returns the fields manipulating an {@code Integer} value (or unlimited) with their corresponding DTO key.
		 * 
		 * @return The fields manipulating an {@code Integer} value (or unlimited) with their corresponding DTO key.
		 */
		Map<String, ComboBox<IntegerModel>> getIntegerFields();

		/**
		 * Returns the fields manipulating a {@code Boolean} value (or unlimited) with their corresponding DTO key.
		 * 
		 * @return The fields manipulating a {@code Boolean} value (or unlimited) with their corresponding DTO key.
		 */
		Map<String, SimpleComboBox<Boolean>> getBooleanFields();

		/**
		 * Returns the save button.
		 * 
		 * @return The save button.
		 */
		Button getSaveButton();

		/**
		 * Sets the toolbar enable state.
		 * 
		 * @param enabled
		 *          {@code true} to enable/show the toolbar, {@code false} to disable/hide it.
		 */
		void setToolbarEnabled(boolean enabled);
		
		/**
		 * If readonly, disables all fields.
		 * 
		 * @param readOnly 
		 *			{@code true} to disable all fields, {@code false} to enable them.
		 */
		void setReadOnly(boolean readOnly);

	}

	/**
	 * Admin keys mapping.
	 */
	// TODO We should use the DTO keys directly.
	private static final Map<String, String> KEYS_MAPPING;

	static {
		KEYS_MAPPING = new HashMap<String, String>();

		KEYS_MAPPING.put(LogFrameModelDTO.ENABLE_SPECIFIC_OBJECTIVES_GROUPS, AdminUtil.PROP_OBJ_ENABLE_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.SPECIFIC_OBJECTIVES_MAX, AdminUtil.PROP_OBJ_MAX);
		KEYS_MAPPING.put(LogFrameModelDTO.SPECIFIC_OBJECTIVES_GROUPS_MAX, AdminUtil.PROP_OBJ_MAX_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.SPECIFIC_OBJECTIVES_PER_GROUP_MAX, AdminUtil.PROP_OBJ_MAX_PER_GROUP);

		KEYS_MAPPING.put(LogFrameModelDTO.ENABLE_EXPECTED_RESULTS_GROUPS, AdminUtil.PROP_R_ENABLE_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.EXPECTED_RESULTS_MAX, AdminUtil.PROP_R_MAX);
		KEYS_MAPPING.put(LogFrameModelDTO.EXPECTED_RESULTS_GROUPS_MAX, AdminUtil.PROP_R_MAX_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.EXPECTED_RESULTS_PER_GROUP_MAX, AdminUtil.PROP_R_MAX_PER_GROUP);
		KEYS_MAPPING.put(LogFrameModelDTO.EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX, AdminUtil.PROP_R_MAX_PER_OBJ);

		KEYS_MAPPING.put(LogFrameModelDTO.ENABLE_ACTIVITIES_GROUPS, AdminUtil.PROP_A_ENABLE_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.ACTIVITIES_MAX, AdminUtil.PROP_A_MAX);
		KEYS_MAPPING.put(LogFrameModelDTO.ACTIVITIES_GROUPS_MAX, AdminUtil.PROP_A_MAX_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.ACTIVITIES_PER_GROUP_MAX, AdminUtil.PROP_A_MAX_PER_GROUP);
		KEYS_MAPPING.put(LogFrameModelDTO.ACTIVITIES_PER_EXPECTED_RESULT_MAX, AdminUtil.PROP_A_MAX_PER_RESULT);

		KEYS_MAPPING.put(LogFrameModelDTO.ENABLE_PREREQUISITES_GROUPS, AdminUtil.PROP_P_ENABLE_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.PREREQUISITES_MAX, AdminUtil.PROP_P_MAX);
		KEYS_MAPPING.put(LogFrameModelDTO.PREREQUISITES_GROUPS_MAX, AdminUtil.PROP_P_MAX_GROUPS);
		KEYS_MAPPING.put(LogFrameModelDTO.PREREQUISITES_PER_GROUP_MAX, AdminUtil.PROP_P_MAX_PER_GROUP);
	}

	/**
	 * Numeric fields limit (not included).
	 */
	private static final int LIMIT = 20;

	/**
	 * The provided current model.
	 */
	private ProjectModelDTO currentModel;

	/**
	 * Presenter's initialization.
	 * 
	 * @param view
	 *          The view managed by this presenter.
	 * @param injector
	 *          The application injector.
	 */
	public LogFrameModelsAdminPresenter(final View view, final ClientFactory factory) {
		super(view, factory);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBind() {

		// --
		// Integer fields.
		// --

		final List<IntegerModel> models = new ArrayList<IntegerModel>(LIMIT);

		models.add(new IntegerModel(null)); // Unlimited option.
		for (int i = 1; i < LIMIT; i++) {
			models.add(new IntegerModel(i));
		}

		for (final ComboBox<IntegerModel> integerField : view.getIntegerFields().values()) {
			integerField.getStore().add(models);
		}

		// --
		// Boolean fields.
		// --

		for (final SimpleComboBox<Boolean> booleanField : view.getBooleanFields().values()) {
			booleanField.add(Boolean.FALSE);
			booleanField.add(Boolean.TRUE);
		}

		// --
		// Save button action handler.
		// --

		view.getSaveButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

			@Override
			public void componentSelected(final ButtonEvent ce) {
				onSaveAction();
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTabTitle() {
		return I18N.CONSTANTS.adminProjectModelLogFrame();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void loadTab(final ProjectModelDTO model) {

		this.currentModel = model;

		view.getForm().clear(); // Should not process a 'clearAll()'.
		
		// Toolbar enable state.
		view.setToolbarEnabled(currentModel.isEditable());

		// LogFrame model loading.
		loadLogFrameModel(currentModel.getLogFrameModel());
		
		// Read-only if the model is not editable.
		// BUGFIX #731: Using model.isEditable instead of status.isEditable
		// to handle the maintenance state.
		view.setReadOnly(!currentModel.isEditable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasValueChanged() {
		return view.getForm().isValueHasChanged();
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Loads the given {@code logFrameModel} into the form.
	 * 
	 * @param logFrameModel
	 *          The log frame model, may be {@code null}.
	 */
	private void loadLogFrameModel(final LogFrameModelDTO logFrameModel) {

		if (logFrameModel == null) {
			return;
		}

		view.getNameField().setValue(logFrameModel.getName());

		for (final Entry<String, ComboBox<IntegerModel>> integerField : view.getIntegerFields().entrySet()) {
			final Integer value = logFrameModel.get(integerField.getKey());
			integerField.getValue().setValue(new IntegerModel(value));
		}

		for (final Entry<String, SimpleComboBox<Boolean>> booleanField : view.getBooleanFields().entrySet()) {
			final Boolean value = logFrameModel.get(booleanField.getKey());
			booleanField.getValue().setSimpleValue(ClientUtils.isTrue(value));
		}
	}

	/**
	 * Callback executed on save button action.
	 */
	private void onSaveAction() {

		if (!view.getForm().isValid()) {
			return;
		}

		final Map<String, Object> logFrameProperties = new HashMap<String, Object>();

		logFrameProperties.put(AdminUtil.PROP_LOG_FRAME, true);
		logFrameProperties.put(AdminUtil.ADMIN_PROJECT_MODEL, currentModel);
		logFrameProperties.put(AdminUtil.PROP_LOG_FRAME_NAME, view.getNameField().getValue());

		// Integer fields.
		for (final Entry<String, ComboBox<IntegerModel>> integerField : view.getIntegerFields().entrySet()) {
			final Integer fieldValue = IntegerModel.getValue(integerField.getValue().getValue());
			logFrameProperties.put(KEYS_MAPPING.get(integerField.getKey()), fieldValue);
		}

		// Boolean fields.
		for (final Entry<String, SimpleComboBox<Boolean>> booleanField : view.getBooleanFields().entrySet()) {
			final Boolean fieldValue = ClientUtils.getSimpleValue(booleanField.getValue());
			logFrameProperties.put(KEYS_MAPPING.get(booleanField.getKey()), fieldValue);
		}

		dispatch.execute(new CreateEntity(ProjectModelDTO.ENTITY_NAME, logFrameProperties), new CommandResultHandler<CreateResult>() {

			@Override
			public void onCommandFailure(final Throwable caught) {
				N10N.error(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateFailure());
			}

			@Override
			public void onCommandSuccess(final CreateResult result) {

				if (result == null || result.getEntity() == null) {
					N10N.warn(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateFailure());
					return;
				}

				final ProjectModelDTO updatedProjectModel = (ProjectModelDTO) result.getEntity();

				N10N.infoNotif(I18N.CONSTANTS.adminLogFrameUpdate(), I18N.MESSAGES.adminLogFrameUpdateSuccess());

				// Resets the form flag.
				view.getForm().resetValueHasChanged();

				// Updates the log frame model into the current model.
				currentModel.setLogFrameModel(updatedProjectModel.getLogFrameModel());
			}
		}, view.getSaveButton());
	}
}
