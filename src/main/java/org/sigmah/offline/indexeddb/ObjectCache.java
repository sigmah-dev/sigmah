package org.sigmah.offline.indexeddb;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ObjectCache {
	
	private final Map<Class<?>, Map<Integer, ?>> cache = new HashMap<Class<?>, Map<Integer, ?>>();
	
	public <T> T get(Class<T> clazz, int id) {
		return getCache(clazz).get(id);
	}
	
	public <T> void put(int id, T t) {
		final Class<T> clazz = (Class<T>) t.getClass();
		getCache(clazz).put(id, t);
	}
	
	private <T> Map<Integer, T> getCache(Class<T> clazz) {
		Map<Integer, T> map = (Map<Integer, T>) cache.get(clazz);
		if(map == null) {
			map = new HashMap<Integer, T>();
			cache.put(clazz, map);
		}
		return map;
	}
}
