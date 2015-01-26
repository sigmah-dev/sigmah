package org.sigmah.server.domain.importation;

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
												@AttributeOverride(name = "budgetSubFieldId", column = @Column(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_ID, nullable = false))
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
	@JoinColumn(name = EntityConstants.BUDGET_SUB_FIELD_COLMUN_ID, nullable = false, insertable = false, updatable = false)
	@NotNull
	private BudgetSubField budgetSubField;

	@ManyToOne
	@JoinColumn(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false)
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
