package org.sigmah.offline.js;

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
