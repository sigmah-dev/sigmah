package org.sigmah.shared.dto.element;

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

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.widget.HistoryTokenText;
import org.sigmah.client.ui.widget.form.Forms;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;
import org.sigmah.shared.util.ValueResultUtils;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import org.sigmah.client.ui.res.icon.IconImageBundle;
import org.sigmah.shared.dto.ProjectDTO;
import org.sigmah.shared.dto.referential.AmendmentState;
import org.sigmah.shared.dto.referential.GlobalPermissionEnum;

/**
 * BudgetElementDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BudgetElementDTO extends DefaultFlexibleElementDTO {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 9066323201865770116L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.BudgetElement";

	// DTO attributes keys.
	public static final String RATIO_DIVIDEND = "ratioDividend";
	public static final String RATIO_DIVISOR = "ratioDivisor";
	public static final String BUDGET_SUB_FIELDS = "budgetSubFields";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	public List<BudgetSubFieldDTO> getBudgetSubFields() {
		return get(BUDGET_SUB_FIELDS);
	}

	public void setBudgetSubFields(List<BudgetSubFieldDTO> budgetSubFields) {
		set(BUDGET_SUB_FIELDS, budgetSubFields);
	}

	public BudgetSubFieldDTO getRatioDividend() {
		return get(RATIO_DIVIDEND);
	}

	public void setRatioDividend(BudgetSubFieldDTO ratioDividend) {
		set(RATIO_DIVIDEND, ratioDividend);
	}

	public BudgetSubFieldDTO getRatioDivisor() {
		return get(RATIO_DIVISOR);
	}

	public void setRatioDivisor(BudgetSubFieldDTO ratioDivisor) {
		set(RATIO_DIVISOR, ratioDivisor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Component getComponent(ValueResult valueResult, boolean enabled) {
		if (currentContainerDTO instanceof DefaultFlexibleElementContainer) {
			container = (DefaultFlexibleElementContainer) currentContainerDTO;
		} else {
			throw new IllegalArgumentException(
				"The flexible elements container isn't an instance of DefaultFlexibleElementContainer. The default flexible element connot be instanciated.");
		}
		
		final Component component;

		// Creates choices store.
		final ListStore<BudgetSubFieldDTO> store = new ListStore<BudgetSubFieldDTO>();
		store.add(getBudgetSubFields());

		final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

		// Spent ratio.
		final LabelField ratioLabel = Forms.label(I18N.CONSTANTS.adminBudgetRatio());

		final Map<BudgetSubFieldDTO, Field<?>> fields = new HashMap<BudgetSubFieldDTO, Field<?>>();
		final List<BudgetSubFieldDTO> budgetSubFields = getBudgetSubFields();

		final boolean disabledBecauseAmendable = isDisabledBecauseAmendable();
		if (enabled || disabledBecauseAmendable) {

			for (BudgetSubFieldDTO subField : budgetSubFields) {
				final HistoryWrapper<Number> input = new HistoryWrapper(createNumberField(false));
				fields.put(subField, input);
				input.setFieldLabel(generateBudgetSubFieldLabel(subField));
				
				if (values.get(subField.getId()) != null) {
					// Sets the value to the fields.
					input.setValue(Double.valueOf(values.get(subField.getId())));
				} else {
					input.setValue(0);
				}
				
				// Show history.
				input.getHistoryButton().addSelectionListener(new SelectionListener<ButtonEvent>() {

					@Override
					public void componentSelected(ButtonEvent ce) {
						loadAndShowHistory(input.getHistoryButton());
					}
					
				});
				
				if(disabledBecauseAmendable && isRatioDivisor(subField)) {
					input.setEnabled(false);
				}
			}

			// Listener.
			final Listener<BaseEvent> listener = new Listener<BaseEvent>() {

				final double minValue = 0.0;

				@Override
				public void handleEvent(BaseEvent be) {

					boolean isValueOn = true;
					final Map<BudgetSubFieldDTO, String> budgetStringValues = new HashMap<BudgetSubFieldDTO, String>();

					for (BudgetSubFieldDTO budgetField : fields.keySet()) {
						final Field<Number> budgetNumberField = (Field<Number>) fields.get(budgetField);
						final Double doubleValue;
						if (budgetNumberField.getValue() != null) {
							doubleValue = budgetNumberField.getValue().doubleValue();
						} else {
							doubleValue = 0.0;
						}
						isValueOn = isValueOn && doubleValue >= minValue;
						budgetStringValues.put(budgetField, String.valueOf(doubleValue));
					}

					final String rawValue = ValueResultUtils.mergeElements(budgetStringValues);
					fireEvents(rawValue, isValueOn);
				}
			};

			for (Field<?> field : fields.values()) {
				field.addListener(Events.Change, listener);
			}
		} else {
			for (BudgetSubFieldDTO bf : budgetSubFields) {
				final LabelField budgetLabelField = createLabelField();
				fields.put(bf, budgetLabelField);
				if (bf.getType() != null) {
					budgetLabelField.setFieldLabel(BudgetSubFieldType.getName(bf.getType()));
				} else {
					budgetLabelField.setFieldLabel(bf.getLabel());
				}
				if (values.get(bf.getId()) != null) {
					// Sets the value to the fields.
					budgetLabelField.setValue(Double.valueOf(values.get(bf.getId())));
				}
			}
		}

		String rationBudgetSubFieldNames = "";
		if (getRatioDividend() != null && getRatioDivisor() != null) {
			rationBudgetSubFieldNames = " (" + generateBudgetSubFieldLabel(getRatioDividend()) + " / " + generateBudgetSubFieldLabel(getRatioDivisor()) + ")";
			if (values.get(getRatioDividend().getId()) != null && values.get(getRatioDivisor().getId()) != null) {
				ratioLabel.setValue(NumberUtils.ratioAsString(Double.valueOf(values.get(getRatioDividend().getId())),
					Double.valueOf(values.get(getRatioDivisor().getId())))
					+ rationBudgetSubFieldNames);

			} else {
				ratioLabel.setValue(NumberUtils.ratioAsString(0, 0) + rationBudgetSubFieldNames);
			}
		}
		// Fieldset.
		final FieldSet fieldset = new FieldSet();
		fieldset.setCollapsible(true);
		fieldset.setLayout(Forms.layout(170, null));

		for (Field<?> budgetField : fields.values()) {
			fieldset.add(budgetField);
		}

		fieldset.add(ratioLabel);

		// Sets the field label.
		setLabel(I18N.CONSTANTS.projectBudget());
		
		if(getAmendable()) {
			fieldset.setHeadingHtml(getLabel() + "&nbsp;" + IconImageBundle.ICONS.DNABrownGreen().getHTML());
		} else {
			fieldset.setHeadingHtml(getLabel());
		}

		component = fieldset;
		return component;
	}

	@Override
	protected Component getComponentInBanner(ValueResult valueResult) {

		if (currentContainerDTO instanceof DefaultFlexibleElementContainer) {
			container = (DefaultFlexibleElementContainer) currentContainerDTO;
		} else {
			throw new IllegalArgumentException(
				"The flexible elements container isn't an instance of DefaultFlexibleElementContainer. The default flexible element connot be instanciated.");
		}

		Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

		final LabelField budgetLabelField = createLabelField();
		budgetLabelField.setFieldLabel(I18N.CONSTANTS.projectBannerBudget());

		final String dividendValue = values.get(getRatioDividend().getId());
		final String divisorValue = values.get(getRatioDivisor().getId());
		if (dividendValue != null && divisorValue != null) {
			budgetLabelField.setValue(dividendValue + " / " + divisorValue);
		} else {
			budgetLabelField.setValue("0.0 / 0.0");
		}
		final LabelField ratioLabel = Forms.label(I18N.CONSTANTS.adminBudgetRatio());
		String rationBudgetSubFieldNames = "";
		if (getRatioDividend() != null && getRatioDivisor() != null) {
			rationBudgetSubFieldNames = " (" + generateBudgetSubFieldLabel(getRatioDividend()) + " / " + generateBudgetSubFieldLabel(getRatioDivisor()) + ")";
		}
		if (values.get(getRatioDividend().getId()) != null && values.get(getRatioDivisor().getId()) != null) {
			ratioLabel.setValue(NumberUtils.ratioAsString(Double.valueOf(values.get(getRatioDividend().getId())),
				Double.valueOf(values.get(getRatioDivisor().getId())))
				+ rationBudgetSubFieldNames);

		} else {
			ratioLabel.setValue(NumberUtils.ratioAsString(0, 0) + rationBudgetSubFieldNames);
		}
		return budgetLabelField;

	}

	private List<String> toLabels(String value) {
		final Map<Integer, String> budgets = ValueResultUtils.splitMapElements(value);
		final List<String> stringValues = new ArrayList<String>();

		for (BudgetSubFieldDTO budgetField : getBudgetSubFields()) {
			// BUGFIX #788: Verifying if an entry exists before using it to avoid NullPointerExceptions
			final String currentBudget = budgets.size() > 0 && budgets.containsKey(budgetField.getId()) ? budgets.get(budgetField.getId()) : "0";
			final String label = budgetField.getType() != null ? BudgetSubFieldType.getName(budgetField.getType()) : budgetField.getLabel();

			stringValues.add(label + ": " + Double.parseDouble(currentBudget));
		}
		
		return stringValues;
	}
	
	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		ensureHistorable();

		final String value = token.getTokens().get(0).getValue();
		
		if (Log.isDebugEnabled()) {
			Log.debug("[renderHistoryToken] Case BUDGET ; value to split '" + value + "'.");
		}

		return new HistoryTokenText(toLabels(value));
	}
	
	@Override
	public String toHTML(String value) {
		if(value == null || value.length() == 0) {
			return "";
		}
		
		final StringBuilder htmlBuilder = new StringBuilder();
		for(final String entry : toLabels(value)) {
			htmlBuilder.append(" -").append(entry).append("<br>");
		}
		return htmlBuilder.toString();
	}

	@Override
	protected Component getComponentWithValue(ValueResult valueResult, boolean enabled) {
		return getComponent(valueResult, enabled);
	}

	public String generateBudgetSubFieldLabel(BudgetSubFieldDTO bf) {
		String label = "";
		if (bf.getType() != null) {
			label = BudgetSubFieldType.getName(bf.getType());
		} else {
			label = bf.getLabel();
		}
		return label;
	}

	public BudgetSubFieldDTO getPlannedBudget() {
		for (BudgetSubFieldDTO budgetSubField : getBudgetSubFields()) {
			if (BudgetSubFieldType.PLANNED.equals(budgetSubField.getType())) {
				return budgetSubField;
			}
		}
		return null;
	}
	
	private boolean isDisabledBecauseAmendable() {
		// BUGFIX #794: Checking if this element is amendable before going further.
		if(currentContainerDTO instanceof ProjectDTO && getAmendable()) {
			final ProjectDTO project = (ProjectDTO)currentContainerDTO;
			
			if(project.getAmendmentState() == AmendmentState.LOCKED && project.getCloseDate() == null) {
				return authenticationProvider.get().getAggregatedProfile().getGlobalPermissions().contains(GlobalPermissionEnum.EDIT_PROJECT);
			}
		}
		return false;
	}
	
	private boolean isRatioDivisor(BudgetSubFieldDTO subField) {
		final BudgetSubFieldDTO divisor = getRatioDivisor();
		
		return subField != null && divisor != null && subField.getId() != null 
			&& subField.getId().equals(divisor.getId());
	}
}
