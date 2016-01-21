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


import java.util.Objects;
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

	@Column(name = EntityConstants.BUDGET_SUB_FIELD_COLUMN_ID, nullable = false)
	@NotNull
	private Integer budgetSubFieldId;
	
	@Column(name = EntityConstants.VARIABLE_FLEXIBLE_ELEMENT_COLUMN_ID, nullable = false)
	@NotNull
	private Integer variableFlexibleId;

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
		builder.append("variableFlexibleId", variableFlexibleId);

		return builder.toString();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.varId);
		hash = 41 * hash + Objects.hashCode(this.budgetSubFieldId);
		hash = 41 * hash + Objects.hashCode(this.variableFlexibleId);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final VariableBudgetSubFieldId other = (VariableBudgetSubFieldId) obj;
		if (!Objects.equals(this.varId, other.varId)) {
			return false;
		}
		if (!Objects.equals(this.budgetSubFieldId, other.budgetSubFieldId)) {
			return false;
		}
		if (!Objects.equals(this.variableFlexibleId, other.variableFlexibleId)) {
			return false;
		}
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

	public Integer getVariableFlexibleId() {
		return variableFlexibleId;
	}

	public void setVariableFlexibleId(Integer variableFlexibleId) {
		this.variableFlexibleId = variableFlexibleId;
	}
	
}
