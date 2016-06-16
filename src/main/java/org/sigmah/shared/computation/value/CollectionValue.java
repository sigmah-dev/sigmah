package org.sigmah.shared.computation.value;

import org.sigmah.shared.computation.instruction.Reductor;
import org.sigmah.shared.dto.element.ComputationElementDTO;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue implements ComputedValue {

	@Override
	public void feedToReductor(Reductor reductor) {
		// TODO: Itérer sur les valeurs présentes en base de données et les ajouter au réducteur.
		reductor.feed(this);
	}
	
	@Override
	public Double get() {
		return null;
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
