package org.sigmah.shared.computation.value;

import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Computed value containing a double.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class DoubleValue implements ComputedValue {
	
	public static final DoubleValue ZERO = new DoubleValue(0);

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
			return Double.toString(value);
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
	
}
