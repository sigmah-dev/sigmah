package org.sigmah.client.util;

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


/**
 * Utility class to manipulate numbers.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class NumberUtils {

	/**
	 * Provides only static methods.
	 */
	private NumberUtils() {
		// Only provides static constants.
	}

	/**
	 * Truncate the decimal part of a number to keep only 2 decimals.
	 * 
	 * @param n
	 *          The number, must not be <code>null</code>.
	 * @return The truncated number.
	 */
	public static String truncate(Number n) {
		return truncate(n, 2);
	}

	/**
	 * Truncate the decimal part of a number.
	 * 
	 * @param n
	 *          The number, must not be <code>null</code>.
	 * @param decimals
	 *          The number of decimals. <code>0</code> will truncate all the decimal part.
	 * @return The truncated number.
	 */
	public static String truncate(Number n, int decimals) {

		if (n == null) {
			throw new IllegalArgumentException("n must not be null.");
		}

		if (decimals < 0) {
			throw new IllegalArgumentException("decimals must not be lower than 0.");
		}

		// Retrieves the number as double.
		final Double d = n.doubleValue();
		final String asString = d.toString();

		// Searches the decimal separator.
		final int index = asString.indexOf('.');

		final String truncatedDoubleAsString;

		// If the number as no decimal part, nothing to do.
		if (index == -1) {
			truncatedDoubleAsString = asString;
		}
		// Truncates the decimal part.
		else {

			// Truncates all the decimal part.
			if (decimals == 0) {
				truncatedDoubleAsString = asString.substring(0, index);
			} else {

				final int last = index + 1 + decimals;

				if (last > asString.length()) {
					truncatedDoubleAsString = asString;
				} else {
					truncatedDoubleAsString = asString.substring(0, last);
				}
			}
		}

		return truncatedDoubleAsString;
	}

	/**
	 * Computes a ratio and returns it as string.
	 * 
	 * @param n
	 *          The number.
	 * @param in
	 *          The ratio number.
	 * @return The ratio.
	 */
	public static String ratioAsString(Number n, Number in) {
		
		if (n == null || in == null) {
			return "-";
		}
		return truncate(ratio(n, in)) + " %";
	}

	/**
	 * Computes a ratio.
	 * 
	 * @param n
	 *          The number.
	 * @param in
	 *          The ratio number.
	 * @return The ratio.
	 */
	public static double ratio(Number n, Number in) {

		if (n == null) {
			throw new IllegalArgumentException("n must not be null.");
		}

		if (in == null) {
			throw new IllegalArgumentException("in must not be null.");
		}
		
		if(in.doubleValue() == 0.0) {
			return 0.0;
		}
		
		return Double.valueOf( truncate( n.doubleValue() / in.doubleValue() * 100 ) ); 
	}

	public static double adjustRatio(Double r) {

		final double ratio;

		// Adjusts the ration.
		if (Double.isNaN(r)) {
			ratio = 0;
		} else if (r < 0) {
			ratio = 0;
		} else if (r == Double.POSITIVE_INFINITY  ) {  
			ratio = 100;
		} else if (r == Double.NEGATIVE_INFINITY) {
			ratio = 0;
		} else {
			ratio = r;
		}

		return ratio;
	}
}
