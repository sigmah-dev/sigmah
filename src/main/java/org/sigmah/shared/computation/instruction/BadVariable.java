package org.sigmah.shared.computation.instruction;

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.value.ComputationError;
import org.sigmah.shared.computation.value.ComputedValue;

/**
 * Bad reference.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class BadVariable implements Instruction {
	
	/**
	 * Reference.
	 */
	private final String reference;

	/**
	 * Creates a new instance with the given reference.
	 * 
	 * @param reference Reference.
	 */
	public BadVariable(String reference) {
		this.reference = reference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		stack.push(ComputationError.BAD_REFERENCE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return reference;
	}

    /**
     * Retrieve the name of the bad reference.
     * 
     * @return Name of the bad reference.
     */
    public String getReference() {
        return reference;
    }

}
