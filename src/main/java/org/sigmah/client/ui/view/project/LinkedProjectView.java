package org.sigmah.client.ui.view.project;

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
import org.sigmah.client.ui.presenter.project.LinkedProjectPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider;
import org.sigmah.client.ui.res.icon.dashboard.funding.FundingIconProvider.IconSize;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.ProjectFundingDTO.LinkedProjectType;
import org.sigmah.shared.dto.country.CountryDTO;
import org.sigmah.shared.dto.referential.ProjectModelType;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.inject.Singleton;

/**
 * Linked project edit frame view used to link a new funding/funded project or edit an existing link.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Singleton
public class LinkedProjectView extends AbstractPopupView<PopupWidget> implements LinkedProjectPresenter.View {

	// CSS style names.
	private static final String STYLE_HEADER_LABEL = "header-label";

	/**
	 * The projects list template.
	 */
	private static final String PROJECTS_LIST_TEMPLATE;

	static {
		final StringBuilder template = new StringBuilder();
		template.append("<tpl for=\".\">");
		template.append("  <tpl if=\"!values.").append(ProjectDTO.TYPE_ICON_HTML).append("\">");
		template.append("    <div class=\"x-combo-list-item x-combo-list-item-disabled\">");
		template.append("      {values.").append(CountryDTO.NAME).append("}");
		template.append("    </div>");
		template.append("  </tpl>");
		template.append("  <tpl if=\"values.").append(ProjectDTO.TYPE_ICON_HTML).append("\">");
		template.append("    <div class=\"x-combo-list-item\">");
		template.append("      {values.").append(ProjectDTO.TYPE_ICON_HTML).append("} {values.").append(ProjectDTO.COMPLETE_NAME).append("}");
		template.append("    </div>");
		template.append("  </tpl>");
		template.append("</tpl>");

		PROJECTS_LIST_TEMPLATE = template.toString();
	}

	private FormPanel form;
	private Label headerLabel;
	private ComboBox<ModelData> projectsField;
	private LabelField projectTypeField;
	private NumberField amountField;
	private LabelField percentageField;

	private Button saveButton;
	private Button deleteButton;

	/**
	 * Builds the view.
	 */
	public LinkedProjectView() {
		super(new PopupWidget(true), 600);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {

		form = Forms.panel(180);

		headerLabel = new Label(I18N.CONSTANTS.createProjectTypeFunding());
		headerLabel.setStyleName(STYLE_HEADER_LABEL);
		projectsField = Forms.combobox(I18N.CONSTANTS.createProjectTypeFunding(), true, ProjectDTO.ID, ProjectDTO.COMPLETE_NAME, new ListStore<ModelData>());

		projectsField.setTemplate(PROJECTS_LIST_TEMPLATE);

		projectTypeField = Forms.label(I18N.CONSTANTS.createProjectType());
		projectTypeField.setHeight(25);

		amountField = Forms.number(I18N.MESSAGES.projectFundedByDetails(null), true);

		percentageField = Forms.label(I18N.CONSTANTS.createProjectPercentage());

		saveButton = Forms.button(I18N.CONSTANTS.formWindowSubmitAction(), IconImageBundle.ICONS.save());
		deleteButton = Forms.button(I18N.CONSTANTS.formWindowDeleteAction(), IconImageBundle.ICONS.linkDelete());

		form.add(headerLabel);
		form.add(projectsField);
		form.add(projectTypeField);
		form.add(amountField);
		form.add(percentageField);

		form.addButton(deleteButton);
		form.addButton(saveButton);

		initPopup(form);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInitializationMode(final LinkedProjectType projectType, final boolean selection, final String projectName) {

		// --
		// Form labels & fields.
		// --

		final String headerText;
		final String amountFieldLabel;

		switch (projectType) {

			case FUNDING_PROJECT:
				headerText = selection ? I18N.CONSTANTS.createProjectTypeFundingSelectDetails() : I18N.CONSTANTS.createProjectFundingProjectEditDetails();

				projectsField.setFieldLabel(I18N.CONSTANTS.createProjectTypeFunding());
				amountFieldLabel = I18N.MESSAGES.projectFinancesDetails(projectName);
				break;

			case FUNDED_PROJECT:
				headerText = selection ? I18N.CONSTANTS.createProjectTypePartnerSelectDetails() : I18N.CONSTANTS.createProjectPartnerProjectEditDetails();

				projectsField.setFieldLabel(I18N.CONSTANTS.createProjectTypePartner());
				amountFieldLabel = I18N.MESSAGES.projectFundedByDetails(projectName);
				break;

			default:
				throw new IllegalArgumentException("Invalid linked project type.");
		}

		headerLabel.setHtml(headerText + " \"" + projectName + '\"');
		amountField.setFieldLabel(amountFieldLabel + " (" + I18N.CONSTANTS.currencyEuro() + ')');

		projectsField.setVisible(selection);
		projectsField.setEnabled(selection);
		projectTypeField.setVisible(selection);
		projectTypeField.setEnabled(selection);

		// --
		// Form delete button.
		// --

		deleteButton.setEnabled(!selection);
		deleteButton.setVisible(!selection);
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
	public ComboBox<ModelData> getProjectsField() {
		return projectsField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LabelField getProjectTypeField() {
		return projectTypeField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public NumberField getAmountField() {
		return amountField;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public LabelField getPercentageField() {
		return percentageField;
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProjectType(final ProjectModelType type) {

		final Grid iconGrid = new Grid(1, 2);
		iconGrid.setCellPadding(0);
		iconGrid.setCellSpacing(0);

		iconGrid.setWidget(0, 0, FundingIconProvider.getProjectTypeIcon(type, IconSize.MEDIUM).createImage());
		DOM.setStyleAttribute(iconGrid.getCellFormatter().getElement(0, 0), "paddingTop", "2px");
		iconGrid.setText(0, 1, ProjectModelType.getName(type));
		DOM.setStyleAttribute(iconGrid.getCellFormatter().getElement(0, 1), "paddingLeft", "5px");

		projectTypeField.setValue(iconGrid.getElement().getString());
	}

}
