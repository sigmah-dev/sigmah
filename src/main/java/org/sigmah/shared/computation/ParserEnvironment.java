package org.sigmah.shared.computation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.instruction.Instruction;
import org.sigmah.shared.computation.instruction.Operator;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Environment of the parser.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
class ParserEnvironment {
	
	/**
	 * Current parser state.
	 */
	private ParserState state = ParserState.WAITING_FOR_OPERAND;
	
	/**
	 * Parser context.
	 */
	private final Stack<Context> contexts = new Stack<Context>();
	
	/**
	 * Flexible elements of the parent model.
	 */
	private final Map<String, FlexibleElementDTO> elements;

	/**
	 * Create a new environment and push an initial context.
	 */
	ParserEnvironment(final Map<String, FlexibleElementDTO> elements) {
		this.elements = elements;
		this.contexts.push(new Context());
	}

	/**
	 * Add an instruction to the instruction list of the current context.
	 * 
	 * @param instruction Instruction to add.
	 */
	void add(final Instruction instruction) {
		contexts.peek().instructions.add(instruction);
	}
	
	/**
	 * Push the given operator to the operator stack of the current context.
	 * 
	 * @param operator Operator to push.
	 */
	void pushOnStack(final Operator operator) {
		contexts.peek().operators.push(operator);
	}
	
	/**
	 * Retrieve and remove from the stack the operator at the peek for the 
	 * current context.
	 * 
	 * @return Operator at the peek of the stack.
	 */
	Operator popFromStack() {
		return contexts.peek().operators.pop();
	}
	
	/**
	 * Retrieve the operator at the peek of the stack of the current context.
	 * 
	 * @return Operator at the peek of the stack.
	 */
	Operator peekOfStack() {
		if (!contexts.peek().operators.isEmpty()) {
			return contexts.peek().operators.peek();
		} else {
			return null;
		}
	}
	
	/**
	 * Empty the operator stack and adds everything to the instruction list.
	 */
	void popEverythingFromStackToInstructions() {
		while (!contexts.peek().operators.isEmpty()) {
			contexts.peek().instructions.add(contexts.peek().operators.pop());
		}
	}
	
	/**
	 * Push a new context.
	 */
	void pushContext() {
		contexts.push(new Context());
	}
	
	/**
	 * Pop the current context and merge its content with the previous one.
	 */
	void popContext() {
		popEverythingFromStackToInstructions();
		final Context context = contexts.pop();
		contexts.peek().instructions.addAll(context.instructions);
	}
	
	// GETTERS & SETTERS

	FlexibleElementDTO getElement(String key) {
		return elements.get(key);
	}
	
	ParserState getState() {
		return state;
	}

	void setState(final ParserState state) {
		this.state = state;
	}
	
	List<Instruction> getInstructions() {
		return contexts.peek().instructions;
	}
	
	// CONTEXT
	
	/**
	 * Execution context.
	 */
	private class Context {
		/**
		 * Instruction list.
		 */
		private final List<Instruction> instructions = new ArrayList<Instruction>();
		/**
		 * Operator stack.
		 */
		private final Stack<Operator> operators = new Stack<Operator>();
	}
	
}
