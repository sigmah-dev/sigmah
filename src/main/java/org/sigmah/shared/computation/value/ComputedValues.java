package org.sigmah.shared.computation.value;

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
		if (value == null) {
			return ZERO;
		}
		try {
			return new DoubleValue(Double.parseDouble(value));
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
			return from(value.getValueObject());
		} else {
			return ZERO;
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
	
}
