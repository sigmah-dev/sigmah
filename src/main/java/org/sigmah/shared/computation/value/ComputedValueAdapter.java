package org.sigmah.shared.computation.value;

import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ComputedValueAdapter implements ComputedValue {

	@Override
	public Double get() {
		return null;
	}

	@Override
	public void feedToReductor(Reductor reductor) {
		reductor.feed(this);
	}

	@Override
	public int matchesConstraints(ComputedValue minimum, ComputedValue maximum) {
		return 0;
	}

	@Override
	public int matchesConstraints(ComputationElementDTO element) {
		return matchesConstraints(element.getMinimumValueConstraint(), element.getMaximumValueConstraint());
	}

	@Override
	public ComputedValue addTo(ComputedValue other) {
		return ComputationError.BAD_FORMULA;
	}

	@Override
	public ComputedValue multiplyWith(ComputedValue other) {
		return ComputationError.BAD_FORMULA;
	}

	@Override
	public ComputedValue divide(ComputedValue other) {
		return ComputationError.BAD_FORMULA;
	}

	@Override
	public ComputedValue substractFrom(ComputedValue other) {
		return ComputationError.BAD_FORMULA;
	}
	
}
