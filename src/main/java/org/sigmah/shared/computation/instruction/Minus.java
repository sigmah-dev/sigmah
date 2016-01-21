package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.DoubleValue;

/**
 * Minus unary operator.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class Minus implements Operator {
	
	private static final DoubleValue MINUS_ONE = new DoubleValue(-1);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "-";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		stack.push(stack.pop().multiplyWith(MINUS_ONE));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OperatorPriority getPriority() {
		return OperatorPriority.UNARY;
	}
	
}
