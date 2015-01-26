package org.sigmah.server.domain.importation;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.EmbeddableEntity;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Variable Budget Sub Field Id.
 * </p>
 * 
 * @author Jérémie BRIAND (jbriand@ideia.fr)
 */
@Embeddable
public class VariableBudgetSubFieldId implements EmbeddableEntity {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = -8983372480244816810L;

	@Column(name = EntityConstants.IMPORTATION_VARIABLE_COLUMN_ID, nullable = false)
	@NotNull
	private Integer varId;

	@Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false)
	@NotNull
	private Integer budgetSubFieldId;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public String toString() {
		ToStringBuilder builder = new ToStringBuilder(this);
		builder.append("varId", varId);
		builder.append("budgetSubFieldId", budgetSubFieldId);

		return builder.toString();
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

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public Integer getVarId() {
		return varId;
	}

	public void setVarId(Integer varId) {
		this.varId = varId;
	}

	public Integer getBudgetSubFieldId() {
		return budgetSubFieldId;
	}

	public void setBudgetSubFieldId(Integer bfId) {
		this.budgetSubFieldId = bfId;
	}
}
