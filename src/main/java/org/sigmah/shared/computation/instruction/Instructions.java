package org.sigmah.shared.computation.instruction;

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

import java.util.HashMap;
import java.util.Map;
import org.sigmah.shared.computation.value.ComputedValue;

/**
 * List of every instructions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Instructions {
    
    /**
     * Prefix of the identifiers.
     */
    public static final char ID_PREFIX = '$';
	
    /**
     * Map each alias to its instruction.
     */
	private static final Map<String, Instruction> INSTRUCTIONS;
	
	static {
		final HashMap<String, Instruction> map = new HashMap<String, Instruction>();
		
		put(map, new Add(), "+");
		put(map, new Substract(), "-");
		put(map, new Multiply(), "*", "×");
		put(map, new Divide(), "/", "÷");
		put(map, new Minus(), "minus");
		
		put(map, new FundingSources(), "fundingSources");
		put(map, new FundedProjects(), "fundedProjects");
		put(map, new Average(), "avg");
		put(map, new Sum(), "sum");
		
		INSTRUCTIONS = map;
	}
	
    /**
     * Add aliases for the given instruction.
     * 
     * @param map Instruction map.
     * @param instruction Instruction to add.
     * @param aliases Aliases of the instructions.
     */
	private static void put(final Map<String, Instruction> map, final Instruction instruction, final String... aliases) {
		for (final String alias : aliases) {
			map.put(alias, instruction);
		}
	}

    /**
     * Private constructor.
     */
	private Instructions() {
		// Not accessible.
	}
	
    /**
     * Search for an instruction with the given alias.
     * 
     * @param name Alias of an instruction.
     * @return The instruction or <code>null</code> if none matches.
     */
	public static Instruction getInstructionNamed(final String name) {
		return INSTRUCTIONS.get(name);
	}
	
    /**
     * Search for an operator with the given alias.
     * 
     * @param name Alias of an operator.
     * @return The operator or <code>null</code> if none matches.
     */
	public static Operator getOperatorNamed(final String name) {
		final Instruction instruction = INSTRUCTIONS.get(name);
		
		if (instruction instanceof Operator) {
			return (Operator) instruction;
		} else {
			return null;
		}
	}
	
	/**
     * Search for a function with the given alias and returns a new instance.
     * 
     * @param name Alias of a function.
     * @return A new instance of the function or <code>null</code> if none matches.
     */
	public static Function getFunctionNamed(final String name) {
		final Instruction instruction = INSTRUCTIONS.get(name);
		
		if (instruction instanceof Function) {
			return ((Function) instruction).instantiate();
		} else {
			return null;
		}
	}
	
    /**
     * Creates a new constant with the given value.
     * 
     * @param value Value of the constant.
     * @return A new constant.
     */
	public static Instruction getConstantWithValue(final String value) {
		return new Constant(value);
	}
	
    /**
     * Creates a new constant with the given value.
     * 
     * @param value Value of the constant.
     * @return A new constant.
     */
	public static Instruction getConstantWithValue(final ComputedValue value) {
		return new Constant(value);
	}
	
}
