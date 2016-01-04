package org.sigmah.offline.indexeddb;

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
			version += 1 + store.getIndexes().size();
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
