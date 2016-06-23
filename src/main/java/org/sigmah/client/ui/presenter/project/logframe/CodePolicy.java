package org.sigmah.client.ui.presenter.project.logframe;

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
 * Defines a code displayer.
 * 
 * @author tmi (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class CodePolicy {

	private CodePolicy() {
		// Only provides static methods.
	}

	/**
	 * Gets the character indexed by the given <code>index</code> in the alphabet. Indexes start at <code>0</code> and
	 * ends at <code>25</code>.
	 *
	 * <ul>
	 * <li>0 → a</li>
	 * <li>1 → b</li>
	 * <li>2 → c</li>
	 * <li>...</li>
	 * <li>25 → z</li>
	 * </ul>
	 * 
	 * @param index
	 *          The character index.
	 * @param upper
	 *          Upper character?
	 * @return The corresponding character.
	 */
	public static char getLetter(int index, boolean upper) {
		return getLetter(index, upper, 0);
	}

	/**
	 * Gets the character indexed by the given <code>index</code> in the alphabet. Indexes start at <code>start</code> and
	 * ends at <code>start + 25</code>.
	 *
	 * <ul>
	 * <li>start → a</li>
	 * <li>start + 1 → b</li>
	 * <li>start + 2 → c</li>
	 * <li>...</li>
	 * <li>start + 25 → z</li>
	 * </ul>
	 * 
	 * @param index
	 *          The character index.
	 * @param upper
	 *          Upper character?
	 * @param start
	 *          The index of the first letter <code>a</code>.
	 * @return The corresponding character.
	 */
	public static char getLetter(int index, boolean upper, int start) {

		// Adjusts index.
		index = start > 0 ? index - start : index + start;

		// Checks the index.
		if (index < 0 || index > 25) {
			throw new IllegalArgumentException("The index #" + index + " doesn't not refer to a valid alphabetical character (must be between 0 and 25).");
		}

		// Computes the ascii code shift to get alphabetical characters.
		final int shift;
		if (upper) {
			shift = 65;
		} else {
			shift = 97;
		}

		return (char) (index + shift);
	}

}
