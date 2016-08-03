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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.sigmah.shared.computation.instruction.Instructions;
import org.sigmah.shared.computation.value.ComputationError;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Parse and creates <code>Computation</code> objects.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public final class Computations {
	
	/**
	 * Parse the given rule.
	 * 
	 * @param rule Rule to read.
	 * @param allElements Flexible element that can be used in the rule.
	 * @return A new <code>Computation</code> object.
	 */
	public static Computation parse(String rule, Collection<FlexibleElementDTO> allElements) {
        try {
            final ParserEnvironment environment = new ParserEnvironment(createReferenceMap(allElements));

            final char[] array = rule.toCharArray();
            int index = 0;
            while (index < array.length) {
                index = environment.getState().execute(index, array, environment);
            }

            environment.popEverythingFromStackToInstructions();

            final Computation computation = new Computation(environment.getInstructions());
            
            // Check if everything is alright.
            computation.toString();
            
            return computation;
            
        } catch (RuntimeException e) {
            // Exception is ignored.
			e.printStackTrace();
            return new Computation(Collections.singletonList(
                    Instructions.getConstantWithValue(ComputationError.BAD_FORMULA)));
        }
	}
	
	public static Computation parse(String rule, Collection<FlexibleElementDTO> allElements, DependencyResolver resolver) {
		final Computation computation = parse(rule, allElements);
		return computation;
	}
	
	/**
	 * Parse the given rule and format it as a human readable rule.
	 * 
	 * @param rule Rule to format.
	 * @param allElements Elements.
	 * @return Formatted rule or <code>null</code> if the given rule is invalid.
	 */
	public static String formatRuleForEdition(String rule, Collection<FlexibleElementDTO> allElements) {
		if (rule == null || rule.trim().isEmpty()) {
			return null;
		}
		
		final Computation computation = parse(rule, allElements);
		return computation.toHumanReadableString();
	}
	
	/**
	 * Parse the given rule and format it with ids.
	 * 
	 * @param rule Rule to format.
	 * @param allElements Elements.
	 * @return Formatted rule or <code>null</code> if the given rule is invalid.
	 */
	public static String formatRuleForServer(String rule, Collection<FlexibleElementDTO> allElements) {
		if (rule == null || rule.trim().isEmpty()) {
			return null;
		}
		
		final Computation computation = parse(rule, allElements);
		return computation.toString();
	}
    
	/**
	 * Creates a map associating the identifier and the code of every element
	 * to its element.
	 * 
	 * @param allElements Flexible element that can be used in the rule.
	 * @return A map of id/code to its flexible element.
	 */
	private static Map<String, FlexibleElementDTO> createReferenceMap(Collection<FlexibleElementDTO> allElements) {
		final HashMap<String, FlexibleElementDTO> references = new HashMap<String, FlexibleElementDTO>();
		
		for (final FlexibleElementDTO element : allElements) {
			references.put(Instructions.ID_PREFIX + element.getId().toString(), element);
			references.put(element.getCode(), element);
		}
		
		return references;
	}
	
	/**
	 * Private constructor.
	 */
	private Computations() {
		// Nothing.
	}
	
}
