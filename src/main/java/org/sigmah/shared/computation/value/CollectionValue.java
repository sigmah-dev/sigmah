package org.sigmah.shared.computation.value;

import java.util.Collection;
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.computation.instruction.Reductor;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue extends ComputedValueAdapter implements Result {

	private Collection<ComputedValue> values;

	public CollectionValue() {
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

	public Collection<ComputedValue> getValues() {
		return values;
	}
	
	public void setValues(Collection<ComputedValue> values) {
		this.values = values;
	}
	
}
