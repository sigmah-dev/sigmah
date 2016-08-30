package org.sigmah.shared.computation.instruction;

/**
 * Variadic function type. Accept a variable number of arguments.
 * The number of arguments is provided to the function by the expression parser.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
@Deprecated
public interface VariadicFunction extends Function {
	
	void setNumberOfArguments(int numberOfArguments);
}
