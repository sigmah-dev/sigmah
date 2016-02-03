package org.sigmah.shared.computation.instruction;

/**
 * Priority of the operators.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since Sigmah 2.1
 */
public enum OperatorPriority {

	/**
	 * Add or substract operator.
	 */
	ADD_SUBSTRACT,
	/**
	 * Multiply or divide operator.
	 */
	MULTIPLY_DIVIDE,
	/**
	 * Function operator (min, max, sum, etc.).
	 */
	FUNCTION,
	/**
	 * Unary operator (minus).
	 */
	UNARY

}
