package org.sigmah.shared.domain.importation;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.sigmah.shared.domain.element.BudgetSubField;

@Entity
@Table(name = "importation_variable_budget_sub_field")
public class VariableBudgetSubField implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1916908867025317181L;

	private VariableBudgetSubFieldId id;
	private Variable variable;
	private BudgetSubField budgetSubField;
	private VariableBudgetElement variableBudgetElement;

	/**
	 * @return the vbfId
	 */
	@EmbeddedId
	@AttributeOverrides({
	                @AttributeOverride(name = "varId", column = @Column(name = "var_id", nullable = false)),
	                @AttributeOverride(name = "budgetSubFieldId", column = @Column(name = "id_budget_sub_field", nullable = false)) })
	public VariableBudgetSubFieldId getId() {
		return id;
	}

	/**
	 * @param vbfId
	 *            the vbfId to set
	 */
	public void setId(VariableBudgetSubFieldId id) {
		this.id = id;
	}

	/**
	 * @return the variable
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "var_id", nullable = false, insertable = false, updatable = false)
	public Variable getVariable() {
		return variable;
	}

	/**
	 * @param variable
	 *            the variable to set
	 */
	public void setVariable(Variable variable) {
		this.variable = variable;
	}

	/**
	 * @return the budgetField
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "id_budget_sub_field", nullable = false, insertable = false, updatable = false)
	public BudgetSubField getBudgetSubField() {
		return budgetSubField;
	}

	/**
	 * @param budgetSubField
	 *            the budgetField to set
	 */
	public void setBudgetSubField(BudgetSubField budgetSubField) {
		this.budgetSubField = budgetSubField;
	}

	/**
	 * @return the variableBudgetElement
	 */
	@ManyToOne
	@JoinColumn(name = "var_fle_id", nullable = false)
	public VariableBudgetElement getVariableBudgetElement() {
		return variableBudgetElement;
	}

	/**
	 * @param variableBudgetElement
	 *            the variableBudgetElement to set
	 */
	public void setVariableBudgetElement(VariableBudgetElement variableBudgetElement) {
		this.variableBudgetElement = variableBudgetElement;
	}

}
