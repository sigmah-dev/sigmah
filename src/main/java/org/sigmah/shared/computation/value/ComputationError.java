package org.sigmah.shared.computation.value;

import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Erroned values during a computation.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public enum ComputationError implements ComputedValue {
	
	BAD_REFERENCE,
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
