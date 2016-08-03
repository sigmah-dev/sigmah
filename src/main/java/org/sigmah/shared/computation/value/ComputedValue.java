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

import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Value computed by a <code>Computation</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface ComputedValue {
	
	/**
	 * Retrieves the double value of this object.
	 * 
	 * @return Double value or <code>null</code> if no value is present.
	 */
	Double get();
	
	/**
	 * Add the current value to the given reductor.
	 * 
	 * @param reductor
	 *			Reductor to feed.
	 */
	void feedToReductor(Reductor reductor);
	
	/**
	 * Returns <code>true</code> if this value matches the given constraints.
	 * 
	 * @param minimum
	 *			Minimum value.
	 * @param maximum
	 *			Maximum value.
	 * @return <code>0</code> if this value matches the given constraints,
	 * <code>-1</code> if the value is too low,
     * <code>1</code> if the value is too high.
	 */
	int matchesConstraints(ComputedValue minimum, ComputedValue maximum);
    
    /**
     * Returns <code>true</code> if this value matches the constraints of the given element.
     * 
     * Identical to <code>matchesConstraints(element.getMinimumConstraint(), 
     * element.getMaximumConstraint())</code>.
     * 
     * @param element
	 *			Computation element.
     * @return <code>0</code> if this value matches the given constraints,
	 * <code>-1</code> if the value is too low,
     * <code>1</code> if the value is too high.
     */
    int matchesConstraints(ComputationElementDTO element);
	
	/**
	 * Add the given value to this one.
	 * <p>
	 * Result will be equals to "<code>other + this</code>".
	 * </p>
	 * 
	 * @param other
	 *			Value to add.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue addTo(ComputedValue other);
	
	/**
	 * Multiply the given value to this one.
	 * <p>
	 * Result will be equals to "<code>other * this</code>".
	 * </p>
	 * 
	 * @param other
	 *			Value to multiply.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue multiplyWith(ComputedValue other);
	
	/**
	 * Divide the given value by this one.
	 * <p>
	 * Result will be equals to "<code>other / this</code>".
	 * </p>
	 * 
	 * @param other
	 *			Value to divide.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue divide(ComputedValue other);
	
	/**
	 * Substract this value from the given one.
	 * <p>
	 * Result will be equals to "<code>other - this</code>".
	 * </p>
	 * 
	 * @param other
	 *			Value to substract from.
	 * @return A new value combining this value and the given one.
	 */
	ComputedValue substractFrom(ComputedValue other);
    
}
