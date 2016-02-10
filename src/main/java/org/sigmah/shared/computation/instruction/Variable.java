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
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Reference an existing flexible element.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class Variable implements Instruction, HasHumanReadableFormat {
	
	private final FlexibleElementDTO flexibleElement;

	public Variable(FlexibleElementDTO flexibleElement) {
		this.flexibleElement = flexibleElement;
	}
	
	public FlexibleElementDTO getFlexibleElement() {
		return flexibleElement;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(Stack<ComputedValue> stack, Map<Integer, ComputedValue> variables) {
		stack.push(variables.get(flexibleElement.getId()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new StringBuilder()
                .append(Instructions.ID_PREFIX)
                .append(flexibleElement.getId())
                .toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toHumanReadableString() {
		return flexibleElement.getCode();
	}
	
}
