package org.sigmah.shared.computation.instruction;

/**
 * Defines a function.
 * <br>
 * Should not be used directly. See {@link NaryFunction} and
 * {@link VariadicFuntion}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface Function extends Instruction {
	
	/**
	 * Creates a new instance of this function.
	 * 
	 * @return A new instance.
	 */
	public Function instantiate();
	
}