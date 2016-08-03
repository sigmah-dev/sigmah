package org.sigmah.shared.computation.value;

import java.util.Collection;
import java.util.Collections;
import org.sigmah.shared.computation.instruction.Reductor;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue extends ComputedValueAdapter {

	private final Collection<ComputedValue> values;

	public CollectionValue() {
		this.values = Collections.emptyList();
	}

	public CollectionValue(final Collection<ComputedValue> values) {
		this.values = values;
	}
	
	@Override
	public void feedToReductor(Reductor reductor) {
		for (final ComputedValue value : values) {
			value.feedToReductor(reductor);
		}
	}
	
}
