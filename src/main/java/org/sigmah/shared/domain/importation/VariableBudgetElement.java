package org.sigmah.shared.domain.importation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "importation_scheme_variable_budget_element")
public class VariableBudgetElement extends VariableFlexibleElement implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8993229869475933267L;

	private List<VariableBudgetSubField> variableBudgetSubFields = new ArrayList<VariableBudgetSubField>();

	/**
	 * @return the variableBudgetSubFields
	 */
	@OneToMany(mappedBy = "variableBudgetElement", cascade = CascadeType.ALL)
	public List<VariableBudgetSubField> getVariableBudgetSubFields() {
		return variableBudgetSubFields;
	}

	/**
	 * @param variableBudgetSubFields
	 *            the variableBudgetSubFields to set
	 */
	public void setVariableBudgetSubFields(List<VariableBudgetSubField> variableBudgetSubFields) {
		this.variableBudgetSubFields = variableBudgetSubFields;
	}

}
