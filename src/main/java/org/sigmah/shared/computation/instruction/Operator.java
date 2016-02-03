package org.sigmah.shared.computation.instruction;

/**
 * Defines an operator in a computation.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface Operator extends Instruction {
	
	/**
	 * Priority of this operator.
	 * 
	 * @return Priority.
	 */
	OperatorPriority getPriority();
	
}
