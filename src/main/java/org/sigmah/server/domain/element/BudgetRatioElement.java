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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Question element domain entity.
 * </p>
 * 
 * @author Cihan Yagan (cihan.yagan@netapsys.fr)
 */
@Entity
@Table(name = EntityConstants.BUDGET_RATIO_ELEMENT_TABLE)
public class BudgetRatioElement extends DefaultFlexibleElement {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 2228874550756273338L;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.BUDGET_RATIO_ELEMENT_COLUMN_SPENT, nullable = true)
	private FlexibleElement spentBudget;
	
	@ManyToOne(optional = true)
	@JoinColumn(name = EntityConstants.BUDGET_RATIO_ELEMENT_COLUMN_PLANNED, nullable = true)
	private FlexibleElement plannedBudget;


	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------



	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transient
	public boolean isHistorable() {
		return true;
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	public void setPlannedBudget(FlexibleElement plannedBudget) {
		this.plannedBudget = plannedBudget;
	}

	public FlexibleElement getPlannedBudget() {
		return plannedBudget;
	}

	public void setSpentBudget(FlexibleElement spentBudget) {
		this.spentBudget = spentBudget;
	}

	public FlexibleElement getSpentBudget() {
		return spentBudget;
	}
}
