package org.sigmah.shared.dto.element;

import java.util.Map;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

/**
 * BudgetSubFieldDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class BudgetSubFieldDTO extends AbstractModelDataEntityDTO<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2072338520863126538L;

	/**
	 * DTO corresponding entity name.
	 */
	public static final String ENTITY_NAME = "element.BudgetField";

	// DTO attributes keys.
	public static final String LABEL = "label";
	public static final String FIELD_ORDER = "fieldOrder";
	public static final String TYPE = "type";
	public static final String BUDGET_ELEMENT = "budgetElement";

	public BudgetSubFieldDTO() {
	}

	public BudgetSubFieldDTO(Integer id) {
		setId(id);
	}

	public BudgetSubFieldDTO(Map<String, Object> properties) {
		super(properties);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEntityName() {
		return ENTITY_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void appendToString(final ToStringBuilder builder) {
		builder.append(LABEL, getLabel());
		builder.append(FIELD_ORDER, getFieldOrder());
		builder.append(TYPE, getType());
	}

	public String getLabel() {
		return get(LABEL);
	}

	public void setLabel(String label) {
		set(LABEL, label);
	}

	public BudgetElementDTO getBudgetElement() {
		return get(BUDGET_ELEMENT);
	}

	public void setBudgetElement(BudgetElementDTO budgetElement) {
		set(BUDGET_ELEMENT, budgetElement);
	}

	public Integer getFieldOrder() {
		return get(FIELD_ORDER);
	}

	public void setFieldOrder(Integer order) {
		set(FIELD_ORDER, order);
	}

	public BudgetSubFieldType getType() {
		return get(TYPE);
	}

	public void setType(BudgetSubFieldType type) {
		set(TYPE, type);
	}

}
