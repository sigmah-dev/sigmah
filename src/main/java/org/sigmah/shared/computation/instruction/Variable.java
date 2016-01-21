package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Reference an existing flexible element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class Variable implements Instruction, HasHumanReadableFormat {
	
	private final FlexibleElementDTO flexibleElement;

	public Variable(FlexibleElementDTO flexibleElement) {
		this.flexibleElement = flexibleElement;
	}
	
	public FlexibleElementDTO getFlexibleElement() {
		return flexibleElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		stack.push(variables.get(flexibleElement.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "#" + flexibleElement.getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toHumanReadableString() {
		return flexibleElement.getCode();
	}
	
}
