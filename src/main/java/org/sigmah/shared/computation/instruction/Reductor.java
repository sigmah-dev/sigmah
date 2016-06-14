package org.sigmah.shared.computation.instruction;

import org.sigmah.shared.computation.value.ComputedValue;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface Reductor {
	
	void feed(ComputedValue value);
	
	ComputedValue reduce();
	
}
