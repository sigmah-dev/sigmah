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

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Computed value containing a double.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class DoubleValue implements ComputedValue {
	
	public static final DoubleValue ZERO = new DoubleValue(0);
    
    private static final int DECIMAL_PART_MAX_LENGTH = 4;

	private final double value;

	public DoubleValue(double value) {
		this.value = value;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Double get() {
		return value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void feedToReductor(Reductor reductor) {
		reductor.feed(this);
	}
	
    /**
	 * {@inheritDoc}
	 */
	@Override
	public int matchesConstraints(ComputedValue minimum, ComputedValue maximum) {
        
		final Double minimumValue = minimum.get();
		if (minimumValue != null && value < minimumValue) {
            return -1;
		}
		
        final Double maximumValue = maximum.get();
		if (maximumValue != null && value > maximumValue) {
            return 1;
		}
		
		return 0;
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
		final Double otherValue = other.get();
		if (otherValue != null) {
			return new DoubleValue(value + otherValue);
		} else {
			return other;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue multiplyWith(ComputedValue other) {
		final Double otherValue = other.get();
		if (otherValue != null) {
			return new DoubleValue(value * otherValue);
		} else {
			return other;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue divide(ComputedValue other) {
		final Double otherValue = other.get();
		if (otherValue == null) {
			return other;
		} else if (value == 0) {
			return ComputationError.DIVISON_BY_ZERO;
		} else {
			return new DoubleValue(otherValue / value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ComputedValue substractFrom(ComputedValue other) {
		final Double otherValue = other.get();
		if (otherValue != null) {
			return new DoubleValue(otherValue - value);
		} else {
			return other;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
        if ((int) value == value) {
			return Integer.toString((int) value);
		} else {
            return doubleToString(value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return 71 + 7 * Double.valueOf(value).hashCode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		return this.value == ((DoubleValue) obj).value;
	}
    
    /**
     * Returns the given value with a decimal part reduced to a length of {@link #DECIMAL_PART_MAX_LENGTH}.
     * If client-side, also replace the decimal separator by the one specified in the current locale.
     * 
     * @param value Value to convert.
     * @return the given double as a <code>String</code>.
     */
    private String doubleToString(double value) {
        final String base = Double.toString(value);
        final int index = base.indexOf('.');
        
        final String decimalSeparator;
        if (GWT.isClient()) {
            decimalSeparator = LocaleInfo.getCurrentLocale().getNumberConstants().decimalSeparator();
        } else {
            decimalSeparator = ".";
        }
        
        if (base.length() - index - 1 > DECIMAL_PART_MAX_LENGTH) {
            return base.substring(0, index) + decimalSeparator + base.substring(index + 1, index + 1 + DECIMAL_PART_MAX_LENGTH);
        } else {
            return base.replace(".", decimalSeparator);
        }
    }
	
}
