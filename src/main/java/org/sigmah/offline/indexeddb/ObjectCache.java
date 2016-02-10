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
