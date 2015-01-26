package org.sigmah.server.domain.importation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Importation Scheme Variable Budget Element domain entity.
 * </p>
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.VARIABLE_BUDGET_ELEMENT_TABLE)
public class VariableBudgetElement extends VariableFlexibleElement implements Serializable {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 8993229869475933267L;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "variableBudgetElement", cascade = CascadeType.ALL)
	private List<VariableBudgetSubField> variableBudgetSubFields = new ArrayList<VariableBudgetSubField>();

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
	 * @return the variableBudgetSubFields
	 */
	public List<VariableBudgetSubField> getVariableBudgetSubFields() {
		return variableBudgetSubFields;
	}

	/**
	 * @param variableBudgetSubFields
	 *          the variableBudgetSubFields to set
	 */
	public void setVariableBudgetSubFields(List<VariableBudgetSubField> variableBudgetSubFields) {
		this.variableBudgetSubFields = variableBudgetSubFields;
	}

}
