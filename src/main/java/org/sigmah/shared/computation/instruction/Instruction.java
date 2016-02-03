package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputedValue;

/**
 * Instruction in a <code>Computation</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public interface Instruction {
	
	/**
	 * Executes this instruction.
	 * 
	 * @param stack Stack of values.
	 * @param variables Values of the variables.
	 */
	void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables);
	
}
