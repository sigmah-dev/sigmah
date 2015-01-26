package org.sigmah.shared.domain.importation;

import java.io.Serializable;

public class VariableBudgetSubFieldId implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8983372480244816810L;
	private Long varId;
	private Long budgetSubFieldId;

	public Long getVarId() {
		return varId;
	}

	public void setVarId(Long varId) {
		this.varId = varId;
	}

	public Long getBudgetSubFieldId() {
		return budgetSubFieldId;
	}

	public void setBudgetSubFieldId(Long bfId) {
		this.budgetSubFieldId = bfId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((budgetSubFieldId == null) ? 0 : budgetSubFieldId.hashCode());
		result = prime * result + ((varId == null) ? 0 : varId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof VariableBudgetSubFieldId))
			return false;
		VariableBudgetSubFieldId other = (VariableBudgetSubFieldId) obj;
		if (budgetSubFieldId == null) {
			if (other.budgetSubFieldId != null)
				return false;
		} else if (!budgetSubFieldId.equals(other.budgetSubFieldId))
			return false;
		if (varId == null) {
			if (other.varId != null)
				return false;
		} else if (!varId.equals(other.varId))
			return false;
		return true;
	}

}
