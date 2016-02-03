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

import org.sigmah.shared.command.result.ValueResult;

/**
 * Utility class to handle <code>ComputedValue</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class ComputedValues {
	
	private static final DoubleValue ZERO = new DoubleValue(0);
	
	/**
	 * Private constructor.
	 */
	private ComputedValues() {
		// Nothing.
	}
	
	/**
	 * Creates a <code>ComputedValue</code> from the given <code>String</code>.
	 * 
	 * @param value Value to parse.
	 * @return A new <code>ComputedValue</code>.
	 */
	public static ComputedValue from(final String value) {
		return from(value, true);
	}
	
	/**
	 * Creates a <code>ComputedValue</code> from the given <code>String</code>.
	 * 
	 * @param value Value to parse.
	 * @param zeroIfNull <code>true</code> to return zero if <code>value</code>
	 * is <code>null</code>, <code>false</code> to return an error.
	 * @return A new <code>ComputedValue</code>.
	 */
	public static ComputedValue from(final String value, final boolean zeroIfNull) {
		if (value == null) {
			return noValue(zeroIfNull);
		}
        final String correctedValue = value.replace(" ", "").replace(',', '.');
		try {
			return new DoubleValue(Double.parseDouble(correctedValue));
		} catch (IllegalArgumentException e) {
			return valueError(value);
		}
	}
	
	/**
	 * Creates a <code>ComputedValue</code> from the given <code>ValueResult</code>.
	 * 
	 * @param value Value to wrap.
	 * @return A new <code>ComputedValue</code>.
	 */
	public static ComputedValue from(final ValueResult value) {
		if (value != null) {
			return from(value.getValueObject(), false);
		} else {
			return ComputationError.NO_VALUE;
		}
	}
	
	/**
	 * Creates a <code>ComputedValue</code> from the given error <code>String</code>.
	 * 
	 * @param error Error.
	 * @return A new <code>ComputationError</code>.
	 */
	private static ComputedValue valueError(String error) {
		try {
			return ComputationError.valueOf(error);
		} catch (IllegalArgumentException e) {
			return ComputationError.BAD_VALUE;
		}
	}
	
	/**
	 * Returns an empty value.
	 * 
	 * @param zero <code>true</code> to return 0, <code>false</code> to return 
	 * an error.
	 * @return 0 if the given argument is <code>true</code>, 
	 * {@link ComputationError#NO_VALUE} otherwise.
	 */
	private static ComputedValue noValue(final boolean zero) {
		if (zero) {
			return ZERO;
		} else {
			return ComputationError.NO_VALUE;
		}
	}
	
}
