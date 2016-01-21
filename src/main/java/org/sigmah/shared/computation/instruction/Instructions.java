package org.sigmah.shared.computation.instruction;

import java.util.HashMap;
import java.util.Map;

/**
 * List of every instructions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Instructions {
	
	private static final Map<String, Instruction> INSTRUCTIONS;
	
	static {
		final HashMap<String, Instruction> map = new HashMap<String, Instruction>();
		
		put(map, new Add(), "+");
		put(map, new Substract(), "-");
		put(map, new Multiply(), "*");
		put(map, new Divide(), "/", "÷");
		put(map, new Minus(), "minus");
		
		INSTRUCTIONS = map;
	}
	
	private static void put(final Map<String, Instruction> map, final Instruction instruction, final String... aliases) {
		for (final String alias : aliases) {
			map.put(alias, instruction);
		}
	}

	private Instructions() {
		// Not accessible.
	}
	
	public static Instruction getInstructionNamed(final String name) {
		return INSTRUCTIONS.get(name);
	}
	
	public static Operator getOperatorNamed(final String name) {
		final Instruction instruction = INSTRUCTIONS.get(name);
		
		if (instruction instanceof Operator) {
			return (Operator) instruction;
		} else {
			return null;
		}
	}
	
	public static Instruction getConstantWithValue(final String value) {
		return new Constant(value);
	}
	
}
