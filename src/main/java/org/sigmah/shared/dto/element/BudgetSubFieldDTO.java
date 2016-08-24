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

import java.util.Map;
import org.sigmah.client.util.ToStringBuilder;
import org.sigmah.shared.dto.base.AbstractModelDataEntityDTO;
import org.sigmah.shared.dto.referential.BudgetSubFieldType;

/**
 * BudgetSubFieldDTO.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Deprecated
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
