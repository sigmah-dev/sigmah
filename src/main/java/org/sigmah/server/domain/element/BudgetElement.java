package org.sigmah.server.domain.element;

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

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Budget element domain entity.
 * </p>
 * 
 * @author Guerline Jean-Baptiste, gjbaptiste@ideia.fr
 */
@Entity
@Table(name = EntityConstants.BUDGET_ELEMENT_TABLE)
@Inheritance(strategy = InheritanceType.JOINED)
public class BudgetElement extends DefaultFlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 7471247661332358221L;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@OneToMany(mappedBy = "budgetElement", cascade = CascadeType.ALL)
	@OrderBy("fieldOrder")
	private List<BudgetSubField> budgetSubFields = new ArrayList<BudgetSubField>();

	@OneToOne
	@JoinColumn(name = EntityConstants.BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVIDEND)
	private BudgetSubField ratioDividend;

	@OneToOne
	@JoinColumn(name = EntityConstants.BUDGET_ELEMENT_COLUMN_ID_RATIO_DIVISOR)
	private BudgetSubField ratioDivisor;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	// Add methods here.

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public List<BudgetSubField> getBudgetSubFields() {
		return budgetSubFields;
	}

	public void setBudgetSubFields(List<BudgetSubField> budgetSubFields) {
		this.budgetSubFields = budgetSubFields;
	}

	public BudgetSubField getRatioDividend() {
		return ratioDividend;
	}

	public void setRatioDividend(BudgetSubField ratioDividend) {
		this.ratioDividend = ratioDividend;
	}

	public BudgetSubField getRatioDivisor() {
		return ratioDivisor;
	}

	public void setRatioDivisor(BudgetSubField ratioDivisor) {
		this.ratioDivisor = ratioDivisor;
	}

}
