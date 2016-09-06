package org.sigmah.offline.indexeddb;

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

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class to handle stores in an IndexedDB database.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Stores {
	
	private Stores() {
		// No initialization.
	}
	
	/**
	 * Calculate the version of the given database schema.
	 * 
	 * @param <S> Schema type.
	 * @param stores Schema type class.
	 * @return Version number.
	 */
	public static <S extends Enum<S> & Schema> int getVersion(final Class<S> stores) {
		int version = 0;
		for (final S store : stores.getEnumConstants()) {
			version += 100 + store.getIndexes().size();
		}
		return version;
	}
	
	/**
	 * Creates a map from the given array.
	 * 
	 * @param indexes Array of String containing name, path couple.
	 * @return A Map.
	 */
	public static Map<String, String> toIndexMap(final String[] indexes) {
		final HashMap<String, String> map = new HashMap<String, String>();
		
		for (int i = 0; i < indexes.length; i += 2) {
			map.put(indexes[i], indexes[i + 1]);
		}
		
		return map;
	}
	
}
