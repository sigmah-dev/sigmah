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
