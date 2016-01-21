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


import java.util.HashMap;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.admin.models.project.LogFrameModelsAdminPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.panel.Panels;
import org.sigmah.client.util.IntegerModel;
import org.sigmah.shared.dto.logframe.LogFrameModelDTO;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Unit;
import java.util.Arrays;

/**
 * {@link LogFrameModelsAdminPresenter}'s view implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class LogFrameModelsAdminView extends AbstractView implements LogFrameModelsAdminPresenter.View {

	/**
	 * Top margin of each block.
	 */
	private static final int BLOCK_MARGIN_TOP = 10;

	/**
	 * Form fields label width.
	 */
	private static final int FIELDS_LABEL_WIDTH = 140;

	private Map<String, ComboBox<IntegerModel>> integerFields;
	private Map<String, SimpleComboBox<Boolean>> booleanFields;

	private FormPanel formPanel;
	private ToolBar toolbar;
	private Button saveButton;

	private TextField<String> name;

	private ComboBox<IntegerModel> objectivesMax;
	private SimpleComboBox<Boolean> objectivesEnableGroups;
	private ComboBox<IntegerModel> objectivesMaxPerGroup;
	private ComboBox<IntegerModel> objectivesMaxGroups;

	private ComboBox<IntegerModel> activitiesMax;
	private SimpleComboBox<Boolean> activitiesEnableGroups;
	private ComboBox<IntegerModel> activitiesMaxPerResult;
	private ComboBox<IntegerModel> activitiesMaxGroups;
	private ComboBox<IntegerModel> activitiesMaxPerGroup;

	private ComboBox<IntegerModel> resultsMax;
	private SimpleComboBox<Boolean> resultsEnableGroups;
	private ComboBox<IntegerModel> resultsMaxPerObjective;
	private ComboBox<IntegerModel> resultsMaxGroups;
	private ComboBox<IntegerModel> resultsMaxPerGroup;

	private ComboBox<IntegerModel> prerequisitesMax;
	private SimpleComboBox<Boolean> prerequisitesEnableGroups;
	private ComboBox<IntegerModel> prerequisitesMaxGroups;
	private ComboBox<IntegerModel> prerequisitesMaxPerGroup;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		integerFields = new HashMap<String, ComboBox<IntegerModel>>();
		booleanFields = new HashMap<String, SimpleComboBox<Boolean>>();

		// --
		// Main panel.
		// --

		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());

		toolbar = new ToolBar();
		toolbar.add(saveButton);

		formPanel = Forms.panel(170);
		formPanel.setTopComponent(toolbar);

		// --
		// Name field.
		// --

		name = Forms.text(I18N.CONSTANTS.adminLogFrameName(), true);

		formPanel.add(name);

		// --
		// Specific objectives fields.
		// --

		objectivesMax = buildIntegerComboBox(LogFrameModelDTO.SPECIFIC_OBJECTIVES_MAX, I18N.CONSTANTS.adminLogFrameMaxOS());
		objectivesEnableGroups = buildBooleanComboBox(LogFrameModelDTO.ENABLE_SPECIFIC_OBJECTIVES_GROUPS, I18N.CONSTANTS.adminLogFrameEnableOSGroups());
		objectivesMaxGroups = buildIntegerComboBox(LogFrameModelDTO.SPECIFIC_OBJECTIVES_GROUPS_MAX, I18N.CONSTANTS.adminLogFrameMaxOSGroups());
		objectivesMaxPerGroup = buildIntegerComboBox(LogFrameModelDTO.SPECIFIC_OBJECTIVES_PER_GROUP_MAX, I18N.CONSTANTS.adminLogFrameMaxAPerGroup());

		formPanel.add(buildColumnContainer(I18N.CONSTANTS.adminLogFrameObjectives(), new Field<?>[][] {
																																																		new Field<?>[] {
																																																										objectivesMax,
																																																										objectivesEnableGroups
																																																		},
																																																		new Field<?>[] { objectivesMaxGroups
																																																		},
																																																		new Field<?>[] { objectivesMaxPerGroup
																																																		}
		}));

		// --
		// Activities fields.
		// --

		activitiesMax = buildIntegerComboBox(LogFrameModelDTO.ACTIVITIES_MAX, I18N.CONSTANTS.adminLogFrameMaxA());
		activitiesEnableGroups = buildBooleanComboBox(LogFrameModelDTO.ENABLE_ACTIVITIES_GROUPS, I18N.CONSTANTS.adminLogFrameEnableAGroups());
		activitiesMaxGroups = buildIntegerComboBox(LogFrameModelDTO.ACTIVITIES_GROUPS_MAX, I18N.CONSTANTS.adminLogFrameMaxAGroups());
		activitiesMaxPerGroup = buildIntegerComboBox(LogFrameModelDTO.ACTIVITIES_PER_GROUP_MAX, I18N.CONSTANTS.adminLogFrameMaxAPerGroup());
		activitiesMaxPerResult = buildIntegerComboBox(LogFrameModelDTO.ACTIVITIES_PER_EXPECTED_RESULT_MAX, I18N.CONSTANTS.adminLogFrameMaxAPerRA());

		formPanel.add(buildColumnContainer(I18N.CONSTANTS.adminLogFrameActivities(), new Field<?>[][] {
																																																		new Field<?>[] {
																																																										activitiesMax,
																																																										activitiesEnableGroups
																																																		},
																																																		new Field<?>[] {
																																																										activitiesMaxGroups,
																																																										activitiesMaxPerGroup
																																																		},
																																																		new Field<?>[] { activitiesMaxPerResult
																																																		}
		}));

		// --
		// Expected results fields.
		// --

		resultsMax = buildIntegerComboBox(LogFrameModelDTO.EXPECTED_RESULTS_MAX, I18N.CONSTANTS.adminLogFrameMaxRA());
		resultsEnableGroups = buildBooleanComboBox(LogFrameModelDTO.ENABLE_EXPECTED_RESULTS_GROUPS, I18N.CONSTANTS.adminLogFrameEnableRAGroups());
		resultsMaxGroups = buildIntegerComboBox(LogFrameModelDTO.EXPECTED_RESULTS_GROUPS_MAX, I18N.CONSTANTS.adminLogFrameMaxRAGroups());
		resultsMaxPerGroup = buildIntegerComboBox(LogFrameModelDTO.EXPECTED_RESULTS_PER_GROUP_MAX, I18N.CONSTANTS.adminLogFrameMaxRAPerGroup());
		resultsMaxPerObjective = buildIntegerComboBox(LogFrameModelDTO.EXPECTED_RESULTS_PER_SPECIFIC_OBJECTIVE_MAX, I18N.CONSTANTS.adminLogFrameMaxRAPerOS());

		formPanel.add(buildColumnContainer(I18N.CONSTANTS.adminLogFrameResults(), new Field<?>[][] {
																																																new Field<?>[] {
																																																								resultsMax,
																																																								resultsEnableGroups
																																																},
																																																new Field<?>[] {
																																																								resultsMaxGroups,
																																																								resultsMaxPerGroup
																																																},
																																																new Field<?>[] { resultsMaxPerObjective
																																																}
		}));

		// --
		// Prerequisites fields.
		// --

		prerequisitesMax = buildIntegerComboBox(LogFrameModelDTO.PREREQUISITES_MAX, I18N.CONSTANTS.adminLogFrameMaxP());
		prerequisitesEnableGroups = buildBooleanComboBox(LogFrameModelDTO.ENABLE_PREREQUISITES_GROUPS, I18N.CONSTANTS.adminLogFrameEnablePGroups());
		prerequisitesMaxGroups = buildIntegerComboBox(LogFrameModelDTO.PREREQUISITES_GROUPS_MAX, I18N.CONSTANTS.adminLogFrameMaxPGroups());
		prerequisitesMaxPerGroup = buildIntegerComboBox(LogFrameModelDTO.PREREQUISITES_PER_GROUP_MAX, I18N.CONSTANTS.adminLogFrameMaxPPerGroup());

		formPanel.add(buildColumnContainer(I18N.CONSTANTS.adminLogFramePrerequisites(), new Field<?>[][] {
																																																			new Field<?>[] {
																																																											prerequisitesMax,
																																																											prerequisitesEnableGroups
																																																			},
																																																			new Field<?>[] { prerequisitesMaxGroups
																																																			},
																																																			new Field<?>[] { prerequisitesMaxPerGroup
																																																			}
		}));

		add(formPanel);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FormPanel getForm() {
		return formPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> getNameField() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ComboBox<IntegerModel>> getIntegerFields() {
		return integerFields;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, SimpleComboBox<Boolean>> getBooleanFields() {
		return booleanFields;
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
	public void setToolbarEnabled(final boolean enabled) {
		if (enabled) {
			toolbar.show();
		} else {
			toolbar.hide();
		}
		toolbar.setEnabled(enabled);
		saveButton.setEnabled(enabled);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setReadOnly(boolean readOnly) {
		for(final Field<?> field : Arrays.asList(name,
			objectivesMax, objectivesEnableGroups, objectivesMaxPerGroup, objectivesMaxGroups,
			activitiesMax, activitiesEnableGroups, activitiesMaxPerResult, activitiesMaxGroups, activitiesMaxPerGroup,
			resultsMax, resultsEnableGroups, resultsMaxPerObjective, resultsMaxGroups, resultsMaxPerGroup,
			prerequisitesMax, prerequisitesEnableGroups, prerequisitesMaxGroups, prerequisitesMaxPerGroup)) {
			field.setEnabled(!readOnly);
		}
	}

	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	/**
	 * Builds a new {@link IntegerModel} comboBox field.<br>
	 * The field is mandatory.
	 * 
	 * @param fieldKey
	 *          The field key referencing the DTO property.
	 * @param label
	 *          The field label.
	 * @return The field.
	 */
	private ComboBox<IntegerModel> buildIntegerComboBox(final String fieldKey, final String label) {

		final ComboBox<IntegerModel> field = Forms.combobox(label, true, IntegerModel.VALUE_FIELD, IntegerModel.DISPLAY_FIELD);

		integerFields.put(fieldKey, field);

		return field;
	}

	/**
	 * Builds a new {@code Boolean} {@link SimpleComboBox} field.<br>
	 * The field is mandatory.
	 * 
	 * @param fieldKey
	 *          The field key referencing the DTO property.
	 * @param label
	 *          The field label.
	 * @return The field.
	 */
	private SimpleComboBox<Boolean> buildBooleanComboBox(final String fieldKey, final String label) {

		final SimpleComboBox<Boolean> field = Forms.simpleCombobox(label, true);

		booleanFields.put(fieldKey, field);

		return field;
	}

	/**
	 * Builds a new layout container (block) for the given {@code columns} configuration.
	 * 
	 * @param title
	 *          The container title.
	 * @param columns
	 *          The columns configuration (should not be {@code null} or empty).<br>
	 *          The table size defines the total number of columns. Each column contains its fields.
	 * @return The layout container.
	 */
	private static LayoutContainer buildColumnContainer(final String title, final Field<?>[]... columns) {

		final LayoutContainer columnsContainer = Panels.content(title, new ColumnLayout());
		columnsContainer.setBorders(true);
		columnsContainer.setWidth("100%");
		columnsContainer.setStyleAttribute("marginTop", BLOCK_MARGIN_TOP + Unit.PX.getType());

		final double columnWidth = 1.0d / columns.length; // Percentage.

		for (final Field<?>[] column : columns) {

			if (column == null) {
				continue;
			}

			final LayoutContainer columnContainer = Forms.panel(FIELDS_LABEL_WIDTH);

			for (final Field<?> field : column) {
				columnContainer.add(field, Forms.data());
			}

			columnsContainer.add(columnContainer, new ColumnData(columnWidth));
		}

		return columnsContainer;
	}
}
