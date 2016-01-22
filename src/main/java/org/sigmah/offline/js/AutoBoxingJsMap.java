package org.sigmah.offline.js;

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

import com.google.gwt.core.client.JsArrayString;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Wrap a {@link JsMap} to implements the Map interface.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <K> Type of keys.
 * @param <V> Type of values.
 */
public class AutoBoxingJsMap<K, V> extends AbstractMap<K, V> {

	public interface Boxer<K> {
		String toString(K object);
		K fromString(String string);
	}
	
	public final static Boxer<String> STRING_BOXER = new Boxer<String>() {

		@Override
		public String toString(String object) {
			return object;
		}

		@Override
		public String fromString(String string) {
			return string;
		}
	};
	
	private final JsMap<String, V> nativeMap;
	private final Boxer<K> boxer;

	public AutoBoxingJsMap(Boxer<K> boxer) {
		this.nativeMap = JsMap.createMap();
		this.boxer = boxer;
	}
	
	public AutoBoxingJsMap(JsMap<String, V> nativeMap, Boxer<K> boxer) {
		this.nativeMap = nativeMap;
		this.boxer = boxer;
	}
	
	private String boxKey(K key) {
		if(key != null) {
			return boxer.toString(key);
		} else {
			return null;
		}
	}

	public JsMap<String, V> getNativeMap() {
		return nativeMap;
	}
	
	@Override
	public V put(K key, V value) {
		return nativeMap.put(boxKey(key), value);
	}
	
	@Override
	public V get(Object key) {
		return nativeMap.get(boxKey((K)key));
	}
	
	@Override
	public V remove(Object key) {
		return nativeMap.remove(boxKey((K)key));
	}

	@Override
	public int size() {
		return nativeMap.size();
	}
	
	@Override
	public Set<Entry<K, V>> entrySet() {
		final HashSet<Entry<K, V>> set = new HashSet<Entry<K, V>>();
		
		final JsArrayString keys = nativeMap.keyArray();
		for(int index = 0; index < keys.length(); index++) {
			final String keyAsString = keys.get(index);
			final Entry<K, V> entry = new JsMapEntry<K, V>(boxer.fromString(keyAsString), keyAsString, nativeMap);
			set.add(entry);
		}
		
		return set;
	}
}
