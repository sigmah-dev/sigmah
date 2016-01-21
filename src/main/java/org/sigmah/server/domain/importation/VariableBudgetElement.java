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
