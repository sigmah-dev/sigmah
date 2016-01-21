package org.sigmah.server.domain.value;

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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.sigmah.server.domain.base.AbstractEntityId;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Budget Part domain entity.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@Entity
@Table(name = EntityConstants.BUDGET_PART_TABLE)
public class BudgetPart extends AbstractEntityId<Integer> {

	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 3320704577030726087L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = EntityConstants.BUDGET_PART_COLUMN_ID)
	private Integer id;

	@Column(name = EntityConstants.BUDGET_PART_COLUMN_TOTAL_AMOUNT, nullable = false, precision = EntityConstants.BUDGET_PART_TOTAL_AMOUNT_PRECISION)
	@NotNull
	private Float amount;

	@Column(name = EntityConstants.BUDGET_PART_COLUMN_LABEL, nullable = false, length = EntityConstants.BUDGET_PART_LABEL_MAX_LENGTH)
	@Size(max = EntityConstants.BUDGET_PART_LABEL_MAX_LENGTH)
	@NotNull
	private String label;

	// --------------------------------------------------------------------------------
	//
	// FOREIGN KEYS.
	//
	// --------------------------------------------------------------------------------

	@ManyToOne(optional = false)
	@JoinColumn(name = EntityConstants.BUDGET_PART_LIST_VALUE_COLUMN_ID, nullable = false)
	@NotNull
	private BudgetPartsListValue parentBugetList;

	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	protected void appendToString(ToStringBuilder builder) {
		builder.append("amount", amount);
		builder.append("label", label);
	}

	// --------------------------------------------------------------------------------
	//
	// GETTERS & SETTERS.
	//
	// --------------------------------------------------------------------------------

	@Override
	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public Integer getId() {
		return id;
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	public BudgetPartsListValue getParentBugetList() {
		return parentBugetList;
	}

	public void setParentBugetList(BudgetPartsListValue parentBugetList) {
		this.parentBugetList = parentBugetList;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
