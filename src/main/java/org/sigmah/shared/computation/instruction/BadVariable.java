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

import java.util.Map;
import java.util.Stack;
import org.sigmah.shared.computation.dependency.Dependency;
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
	public void execute(Stack<ComputedValue> stack, Map<Dependency, ComputedValue> variables) {
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
