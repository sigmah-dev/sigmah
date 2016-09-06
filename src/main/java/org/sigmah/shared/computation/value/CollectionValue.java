package org.sigmah.shared.computation.value;

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
import org.sigmah.shared.command.result.Result;
import org.sigmah.shared.computation.instruction.Reductor;

/**
 * Contains multiple values. Can be used by functions.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public class CollectionValue extends ComputedValueAdapter implements Result {

	private Collection<ComputedValue> values;

	public CollectionValue() {
	}

	public CollectionValue(final Collection<ComputedValue> values) {
		this.values = values;
	}
	
	@Override
	public void feedToReductor(Reductor reductor) {
		for (final ComputedValue value : values) {
			value.feedToReductor(reductor);
		}
	}

	public Collection<ComputedValue> getValues() {
		return values;
	}
	
	public void setValues(Collection<ComputedValue> values) {
		this.values = values;
	}
	
}
