package org.sigmah.shared.dto.element;

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
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.form.NumberField;

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
		final List<BudgetSubFieldDTO> bdfDTO = getBudgetSubFields();

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
		fieldset.setHeadingHtml(getLabel());

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

		for (BudgetSubFieldDTO budgetField : getBudgetSubFields()) {
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
		for (BudgetSubFieldDTO budgetSubField : getBudgetSubFields()) {
			if (BudgetSubFieldType.PLANNED.equals(budgetSubField.getType())) {
				return budgetSubField;
			}
		}
		return null;
	}
}
