package org.sigmah.shared.computation;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Scope;
import org.sigmah.shared.computation.instruction.Function;
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
	
	// TODO: À utiliser.
	private boolean hasUnresolvedDependencies;

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
     * Retrieves the last instruction added.
     * 
     * @return The last instruction.
     */
    Instruction lastInstruction() {
        final List<Instruction> instructions = contexts.peek().instructions;
        
        if (instructions == null || instructions.isEmpty()) {
            return null;
        } else {
            return instructions.get(instructions.size() - 1);
        }
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
	
	Scope getCurrentScope() {
		return contexts.peek().scope;
	}
	
	void setCurrentScope(Scope scope) {
		contexts.peek().scope = scope;
	}
	
	Function popLastFunction() {
		final Context peek = contexts.peek();
		final Function function = peek.function;
		peek.function = null;
		return function;
	}
	
	void setLastFunction(Function function) {
		contexts.peek().function = function;
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
		
		private Function function;
		
		/**
		 * Current scope of the context. May be null.
		 */
		private Scope scope;
	}
	
}
