package org.sigmah.server.domain.importation;

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


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.element.BudgetSubField;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Importation Variable Budget Sub Field domain entity.
 * </p>
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.VARIABLE_BUDGET_SUB_FIELD_TABLE)
public class VariableBudgetSubField extends AbstractEntityId<VariableBudgetSubFieldId> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -1916908867025317181L;

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "varId", column = @Column(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_ID, nullable = false)),
		@AttributeOverride(name = "budgetSubFieldId", column = @Column(name = EntityConstants.BUDGET_SUB_FIELD_COLUMN_ID, nullable = false)),
		@AttributeOverride(name = "variableFlexibleId", column = @Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false))
	})
	private VariableBudgetSubFieldId id;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private Variable variable;

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.BUDGET_SUB_FIELD_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private BudgetSubField budgetSubField;

	@ManyToOne
	@JoinColumn(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private VariableBudgetElement variableBudgetElement;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * @return The {@code VariableBudgetSubFieldId}.
	 */
	@Override
	public VariableBudgetSubFieldId getId() {
		return id;
	}

	/**
	 * @param id
	 *          The {@code VariableBudgetSubFieldId} to set.
	 */
	@Override
	public void setId(VariableBudgetSubFieldId id) {
		this.id = id;
	}

	/**
	 * @return the variable
	 */
	public Variable getVariable() {
		return variable;
	}

	/**
	 * @param variable
	 *          the variable to set
	 */
	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	/**
	 * @return the budgetField
	 */
	public BudgetSubField getBudgetSubField() {
		return budgetSubField;
	}

	/**
	 * @param budgetSubField
	 *          the budgetField to set
	 */
	public void setBudgetSubField(BudgetSubField budgetSubField) {
		this.budgetSubField = budgetSubField;
	}

	/**
	 * @return the variableBudgetElement
	 */
	public VariableBudgetElement getVariableBudgetElement() {
		return variableBudgetElement;
	}

	/**
	 * @param variableBudgetElement
	 *          the variableBudgetElement to set
	 */
	public void setVariableBudgetElement(VariableBudgetElement variableBudgetElement) {
		this.variableBudgetElement = variableBudgetElement;
	}

}
