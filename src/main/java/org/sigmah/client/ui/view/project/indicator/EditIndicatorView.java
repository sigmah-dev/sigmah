package org.sigmah.client.ui.view.project.indicator;

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
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.sigmah.client.ClientFactory;
import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.presenter.project.indicator.EditIndicatorPresenter;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.client.ui.view.base.AbstractPopupView;
import org.sigmah.client.ui.view.pivot.table.PivotGridPanel;
import org.sigmah.client.ui.widget.button.Button;
import org.sigmah.client.ui.widget.form.DatasourceField;
import org.sigmah.client.ui.widget.form.FormPanel;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.ui.widget.form.ValueLabelField;
import org.sigmah.client.ui.widget.popup.PopupWidget;
import org.sigmah.shared.dto.IndicatorDTO;
import org.sigmah.shared.dto.IndicatorGroup;

/**
 * {@link EditIndicatorPresenter}'s view implementation.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class EditIndicatorView extends AbstractPopupView<PopupWidget> implements EditIndicatorPresenter.View {
	
	private static final String RADIO_GROUP_TYPE = "type";
	private static final String RADIO_GROUP_QUANTITATIVE_AGGREGATION = "quantitativeAggregation";
	
	private Grid mainContainer;
	
	private FormPanel form;
	private Field<String> codeField;
	private Field<String> nameField;
	private ComboBox<IndicatorGroup> indicatorGroupField;
	private RadioGroup typeField;
	private Radio typeQuantitativeRadio;
	private Radio typeQualitativeTypeRadio;
	private ValueLabelField labelsField;
	private RadioGroup aggregationField;
	private Radio aggregationSumRadio;
	private Radio aggregationAverageTypeRadio;
	private Field<String> unitsField;
	private Field<Number> objectiveField;
	private Field<String> verificationField;
	private Field<String> descriptionField;
	private DatasourceField datasourceField;
	
	private Button saveButton;
	private Button cancelButton;
	
	private PivotGridPanel pivotGridPanel;
	private Widget pivotLabel;
	
	// Required by DatasourceField
	private DispatchAsync dispatchAsync;
	
	
	/**
	 * Builds the view.
	 */
	public EditIndicatorView(DispatchAsync dispatch, PivotGridPanel pivotGridPanel) {
		super(new PopupWidget(true), 800);
		this.dispatchAsync = dispatch;
		this.pivotGridPanel = pivotGridPanel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize() {
		form = Forms.panel(130);
		
		// Code field.
		codeField = Forms.text(I18N.CONSTANTS.indicatorCode(), true, 30);
		
		// Name field.
		nameField = Forms.text(I18N.CONSTANTS.name(), true, 1024);
		
		// Indicator group field.
		indicatorGroupField = Forms.combobox(I18N.CONSTANTS.group(), false, IndicatorGroup.ID, IndicatorGroup.NAME);
		
		// Type field.
		typeQuantitativeRadio = Forms.radio(I18N.CONSTANTS.quantitative());
		typeQualitativeTypeRadio = Forms.radio(I18N.CONSTANTS.qualitative());
		typeField = Forms.radioGroup(I18N.CONSTANTS.type(), 
			RADIO_GROUP_TYPE, Style.Orientation.HORIZONTAL,
			typeQuantitativeRadio,
			typeQualitativeTypeRadio);
		
		// Possible values field.
		labelsField = new ValueLabelField();
		labelsField.setFieldLabel(I18N.CONSTANTS.possibleValues());
		
		// Aggregation field.
		aggregationSumRadio = Forms.radio(I18N.CONSTANTS.sum());
		aggregationAverageTypeRadio = Forms.radio(I18N.CONSTANTS.average());
		aggregationField = Forms.radioGroup(I18N.CONSTANTS.aggregationMethod(), 
			RADIO_GROUP_QUANTITATIVE_AGGREGATION, Style.Orientation.HORIZONTAL,
			aggregationSumRadio,
			aggregationAverageTypeRadio);
		
		// Units field.
		unitsField = Forms.text(I18N.CONSTANTS.units(), true, 15);
		
		// Objective field.
		objectiveField = Forms.number(I18N.CONSTANTS.targetValue(), false);
		
		// Verification field.
		verificationField = Forms.textarea(I18N.CONSTANTS.sourceOfVerification(), false);
		
		// Description field.
		descriptionField = Forms.textarea(I18N.CONSTANTS.indicatorComments(), false);
		
		// Datasource field.
		datasourceField = new DatasourceField(dispatchAsync);
		datasourceField.setFieldLabel(I18N.CONSTANTS.datasources());
		
		// Save button.
		saveButton = Forms.button(I18N.CONSTANTS.save(), IconImageBundle.ICONS.save());
		
		// Cancel button.
		cancelButton = Forms.button(I18N.CONSTANTS.cancel(), IconImageBundle.ICONS.cancel());
		
		// Building form.
		form.add(codeField);
		form.add(nameField);
		form.add(indicatorGroupField);
		form.add(typeField);
		form.add(labelsField);
		form.add(aggregationField);
		form.add(unitsField);
		form.add(objectiveField);
		form.add(verificationField);
		form.add(descriptionField);
		form.add(datasourceField);
		
		// Value editor.
		pivotGridPanel.setHeaderVisible(false);
		pivotGridPanel.setHeight("380px");
		pivotLabel = Forms.header(I18N.CONSTANTS.value());
		
		// Building tabs
		mainContainer = new Grid(2, 2);
		mainContainer.setWidth("100%");
		mainContainer.getElement().getStyle().setTableLayout(com.google.gwt.dom.client.Style.TableLayout.FIXED);
		mainContainer.getColumnFormatter().getElement(0).getStyle().setProperty("width", "50%");
		mainContainer.getColumnFormatter().getElement(1).getStyle().setProperty("width", "50%");
		mainContainer.getRowFormatter().setVerticalAlign(1, HasVerticalAlignment.ALIGN_TOP);

		mainContainer.setWidget(0, 0, Forms.header("Definition"));
		mainContainer.setWidget(0, 1, pivotLabel);
		mainContainer.setWidget(1, 0, form);
		mainContainer.setWidget(1, 1, pivotGridPanel);
		getPopup().addButton(cancelButton);
		getPopup().addButton(saveButton);
		
		initPopup(mainContainer);
	}

	@Override
	public void loadIndicator(Integer projectId, IndicatorDTO indicator) {
		codeField.setValue(indicator != null ? indicator.getCode() : null);
		nameField.setValue(indicator != null ? indicator.getName() : null);
		// Indicator group field is directly loaded by the presenter.
		selectTypeAndAggregation(indicator != null ? indicator.getAggregation() : IndicatorDTO.AGGREGATE_SUM);
		labelsField.setValue(indicator != null ? indicator.getLabels() : null);
		unitsField.setValue(indicator != null ? indicator.getUnits() : null);
		objectiveField.setValue(indicator != null ? indicator.getObjective() : null);
		verificationField.setValue(indicator != null ? indicator.getSourceOfVerification() : null);
		descriptionField.setValue(indicator != null ? indicator.getDescription() : null);
		datasourceField.load(projectId, indicator);
	}
	
	@Override
	public void setDataEntryVisible(boolean visible) {
		pivotLabel.setVisible(visible);
		pivotGridPanel.setVisible(visible);
		
		if(!visible) {
			mainContainer.getColumnFormatter().getElement(0).getStyle().setProperty("width", "100%");
			mainContainer.getColumnFormatter().getElement(1).getStyle().setProperty("width", "0%");
			
		} else {
			mainContainer.getColumnFormatter().getElement(0).getStyle().setProperty("width", "50%");
			mainContainer.getColumnFormatter().getElement(1).getStyle().setProperty("width", "50%");
		}
		
		// TODO: Resize the popup size to 400 or 800 depending of "visible".
	}
	
	@Override
	public FormPanel getForm() {
		return form;
	}

	@Override
	public Field<String> getCodeField() {
		return codeField;
	}
	
	@Override
	public Field<String> getNameField() {
		return nameField;
	}

	@Override
	public ComboBox<IndicatorGroup> getIndicatorGroupField() {
		return indicatorGroupField;
	}

	@Override
	public RadioGroup getTypeField() {
		return typeField;
	}
	
	@Override
	public Radio getTypeQuantitativeRadio() {
		return typeQuantitativeRadio;
	}

	@Override
	public Radio getTypeQualitativeTypeRadio() {
		return typeQualitativeTypeRadio;
	}

	@Override
	public ValueLabelField getLabelsField() {
		return labelsField;
	}

	@Override
	public RadioGroup getAggregationField() {
		return aggregationField;
	}

	@Override
	public Radio getAggregationSumRadio() {
		return aggregationSumRadio;
	}

	@Override
	public Radio getAggregationAverageTypeRadio() {
		return aggregationAverageTypeRadio;
	}

	@Override
	public Field<String> getUnitsField() {
		return unitsField;
	}

	@Override
	public Field<Number> getObjectiveField() {
		return objectiveField;
	}

	@Override
	public Field<String> getVerificationField() {
		return verificationField;
	}

	@Override
	public Field<String> getDescriptionField() {
		return descriptionField;
	}

	@Override
	public DatasourceField getDatasourceField() {
		return datasourceField;
	}

	@Override
	public Button getSaveButton() {
		return saveButton;
	}

	@Override
	public Button getCancelButton() {
		return cancelButton;
	}

	@Override
	public PivotGridPanel getPivotGridPanel() {
		return pivotGridPanel;
	}
	
	// ---------------------------------------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// ---------------------------------------------------------------------------------------------------------------

	private void selectTypeAndAggregation(int aggregationType) {
		switch(aggregationType) {
			case IndicatorDTO.AGGREGATE_MULTINOMIAL:
				typeField.setValue(typeQualitativeTypeRadio);
				break;
			case IndicatorDTO.AGGREGATE_AVG:
				typeField.setValue(typeQuantitativeRadio);
				aggregationField.setValue(aggregationAverageTypeRadio);
				break;
			case IndicatorDTO.AGGREGATE_SUM:
				typeField.setValue(typeQuantitativeRadio);
				aggregationField.setValue(aggregationSumRadio);
				break;
		}
	}
}
