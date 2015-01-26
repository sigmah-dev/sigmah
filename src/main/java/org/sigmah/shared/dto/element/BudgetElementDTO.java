package org.sigmah.shared.dto.element;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.i18n.I18N;
import org.sigmah.client.util.HistoryTokenText;
import org.sigmah.client.util.NumberUtils;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.command.result.ValueResultUtils;
import org.sigmah.shared.domain.element.BudgetSubFieldType;
import org.sigmah.shared.dto.history.HistoryTokenListDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class BudgetElementDTO extends DefaultFlexibleElementDTO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9066323201865770116L;

	@Override
	public String getEntityName() {
		return "element.BudgetElement";
	}

	public List<BudgetSubFieldDTO> getBudgetSubFieldsDTO() {
		return get("budgetSubFieldsDTO");
	}

	public void setBudgetSubFieldsDTO(List<BudgetSubFieldDTO> budgetSubFieldsDTO) {
		set("budgetSubFieldsDTO", budgetSubFieldsDTO);
	}

	public BudgetSubFieldDTO getRatioDividend() {
		return get("ratioDividend");
	}

	public void setRatioDividend(BudgetSubFieldDTO ratioDividend) {
		set("ratioDividend", ratioDividend);
	}

	public BudgetSubFieldDTO getRatioDivisor() {
		return get("ratioDivisor");
	}

	public void setRatioDivisor(BudgetSubFieldDTO ratioDivisor) {
		set("ratioDivisor", ratioDivisor);
	}

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
		store.add(getBudgetSubFieldsDTO());

		final Map<Integer, String> values = ValueResultUtils.splitMapElements(valueResult.getValueObject());

		// Spent ratio.
		final Label ratioLabel = new Label();
		ratioLabel.addStyleName("project-label-10");
		ratioLabel.addStyleName("flexibility-label");

		final Map<BudgetSubFieldDTO, Field<?>> fields = new HashMap<BudgetSubFieldDTO, Field<?>>();
		final List<BudgetSubFieldDTO> bdfDTO = getBudgetSubFieldsDTO();

		if (enabled) {

			for (BudgetSubFieldDTO bf : bdfDTO) {
				final NumberField budgetField = createNumberField(false);
				fields.put(bf, budgetField);
				budgetField.setFieldLabel(generateBudgetSubFieldLabel(bf));

				if (values.get(bf.getId()) != null) {
					// Sets the value to the fields.
					budgetField.setValue(Double.valueOf(values.get(bf.getId())));
				} else {
					budgetField.setValue(0);
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
						final NumberField budgetNumberField = (NumberField) fields.get(budgetField);
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
			for (BudgetSubFieldDTO bf : bdfDTO) {
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
			rationBudgetSubFieldNames = " (" + generateBudgetSubFieldLabel(getRatioDividend()) + " / "
			                + generateBudgetSubFieldLabel(getRatioDivisor()) + ")";
			if (values.get(getRatioDividend().getId()) != null && values.get(getRatioDivisor().getId()) != null) {
				ratioLabel.setText(I18N.CONSTANTS.adminBudgetRatio()
				                + ":"
				                + NumberUtils.ratioAsString(Double.valueOf(values.get(getRatioDividend().getId())),
				                                Double.valueOf(values.get(getRatioDivisor().getId())))
				                + rationBudgetSubFieldNames);

			} else {
				ratioLabel.setText(I18N.CONSTANTS.adminBudgetRatio() + ":" + NumberUtils.ratioAsString(0, 0)
				                + rationBudgetSubFieldNames);
			}
		}
		// Fieldset.
		final FieldSet fieldset = new FieldSet();
		fieldset.setCollapsible(true);
		fieldset.setLayout(new FormLayout());

		for (Field<?> budgetField : fields.values()) {
			fieldset.add(budgetField);
		}

		fieldset.add(ratioLabel);

		// Sets the field label.
		setLabel(I18N.CONSTANTS.projectBudget());
		fieldset.setHeading(getLabel());

		component = fieldset;
		return component;
	}

	@Override
	protected Component getComponentInBanner(ValueResult valueResult, boolean enabled) {

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
		final Label ratioLabel = new Label();
		String rationBudgetSubFieldNames = "";
		if (getRatioDividend() != null && getRatioDivisor() != null) {
			rationBudgetSubFieldNames = " (" + generateBudgetSubFieldLabel(getRatioDividend()) + " / "
			                + generateBudgetSubFieldLabel(getRatioDivisor()) + ")";
		}
		if (values.get(getRatioDividend().getId()) != null && values.get(getRatioDivisor().getId()) != null) {
			ratioLabel.setText(I18N.CONSTANTS.adminBudgetRatio()
			                + ":"
			                + NumberUtils.ratioAsString(Double.valueOf(values.get(getRatioDividend().getId())),
			                                Double.valueOf(values.get(getRatioDivisor().getId())))
			                + rationBudgetSubFieldNames);

		} else {
			ratioLabel.setText(I18N.CONSTANTS.adminBudgetRatio() + ":" + NumberUtils.ratioAsString(0, 0)
			                + rationBudgetSubFieldNames);
		}
		return budgetLabelField;

	}

	@Override
	public Object renderHistoryToken(HistoryTokenListDTO token) {

		ensureHistorable();

		final String value = token.getTokens().get(0).getValue();

		if (Log.isDebugEnabled()) {
			Log.debug("[renderHistoryToken] Case BUDGET ; value to split '" + value + "'.");
		}

		final Map<Integer, String> budgets = ValueResultUtils.splitMapElements(value);
		final List<String> stringValues = new ArrayList<String>();

		if (Log.isDebugEnabled()) {
			Log.debug("[renderHistoryToken] Case BUDGET ; splitted values (" + budgets.size() + ") '" + budgets + "'.");
		}

		for (BudgetSubFieldDTO budgetField : getBudgetSubFieldsDTO()) {
			String currentBudget = (budgets.size() > 0) ? budgets.get(budgetField.getId()) : "0";
			if (Log.isDebugEnabled()) {
				Log.debug("[renderHistoryToken] Case BUDGET ; " + budgetField.getLabel() + "'" + currentBudget + "'.");
			}

			stringValues.add(budgetField.getLabel() + ":" + Double.parseDouble(currentBudget));
		}

		return new HistoryTokenText(stringValues);
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
		for (BudgetSubFieldDTO budgetSubField : getBudgetSubFieldsDTO()) {
			if (BudgetSubFieldType.PLANNED.equals(budgetSubField.getType())) {
				return budgetSubField;
			}
		}
		return null;
	}
}
