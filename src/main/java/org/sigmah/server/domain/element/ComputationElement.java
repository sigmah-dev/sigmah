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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import org.sigmah.server.domain.util.EntityConstants;

/**
 * <p>
 * Computation element domain entity.
 * </p>
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
@Entity
@Table(name = EntityConstants.COMPUTATION_ELEMENT_TABLE)
public class ComputationElement extends FlexibleElement {
	
	/**
	 * Computation rule used to evaluate the value of this element.
	 */
	@Column(name = EntityConstants.COMPUTATION_ELEMENT_COLUMN_RULE, nullable = true, length = EntityConstants.COMPUTATION_ELEMENT_RULE_MAX_LENGTH)
	private String rule;
	
	/**
	 * Minimum value constraint.
	 */
	@Column(name = EntityConstants.COMPUTATION_ELEMENT_COLUMN_MINIMUM_VALUE, nullable = true, length = EntityConstants.COMPUTATION_ELEMENT_RULE_MAX_LENGTH)
	private String minimumValue;
	
	/**
	 * Maximum value constraint.
	 */
	@Column(name = EntityConstants.COMPUTATION_ELEMENT_COLUMN_MAXIMUM_VALUE, nullable = true, length = EntityConstants.COMPUTATION_ELEMENT_RULE_MAX_LENGTH)
	private String maximumValue;
	
	
	// --------------------------------------------------------------------------------
	//
	// METHODS.
	//
	// --------------------------------------------------------------------------------

	@Override
	@Transient
	public boolean isHistorable() {
		return true;
	}
	
	// --------------------------------------------------------------------------------
	//
	// GETTERS AND SETTERS.
	//
	// --------------------------------------------------------------------------------

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getMinimumValue() {
		return minimumValue;
	}

	public void setMinimumValue(String minimumValue) {
		this.minimumValue = minimumValue;
	}

	public String getMaximumValue() {
		return maximumValue;
	}

	public void setMaximumValue(String maximumValue) {
		this.maximumValue = maximumValue;
	}
	
}
