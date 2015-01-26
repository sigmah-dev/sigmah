package org.sigmah.offline.js;

import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class JsMapEntry<K, V> implements Map.Entry<K, V> {
	
	private final K key;
	private final String keyAsString;
	private final JsMap<String, V> nativeMap;

	public JsMapEntry(K key, String keyAsString, JsMap<String, V> nativeMap) {
		this.key = key;
		this.keyAsString = keyAsString;
		this.nativeMap = nativeMap;
	}

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return nativeMap.get(keyAsString);
	}

	@Override
	public V setValue(V value) {
		return nativeMap.put(keyAsString, value);
	}
	
}
