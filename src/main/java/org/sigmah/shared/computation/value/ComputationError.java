package org.sigmah.shared.computation.value;

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

import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Erroned values during a computation.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public enum ComputationError implements ComputedValue {
	
	BAD_REFERENCE,
    BAD_FORMULA,
	DIVISON_BY_ZERO,
	BAD_VALUE,
	NO_VALUE;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double get() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int matchesConstraints(ComputedValue minimum, ComputedValue maximum) {
		return -1;
	}

    /**
	 * {@inheritDoc}
	 */
    @Override
    public int matchesConstraints(ComputationElementDTO element) {
        return matchesConstraints(element.getMinimumValueConstraint(), element.getMaximumValueConstraint());
    }
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue addTo(ComputedValue other) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue multiplyWith(ComputedValue other) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue divide(ComputedValue other) {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue substractFrom(ComputedValue other) {
		return this;
	}
	
}
