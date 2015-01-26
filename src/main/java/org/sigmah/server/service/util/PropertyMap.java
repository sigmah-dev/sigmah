package org.sigmah.server.service.util;

import java.util.Map;
import java.util.Set;

/**
 * Property map.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PropertyMap {

	/**
	 * Inner map.
	 */
	private final Map<String, Object> map;

	public PropertyMap(Map<String, Object> map) {
		this.map = map;
	}

	@SuppressWarnings("unchecked")
	public <X> X get(String propertyName) {
		return (X) map.get(propertyName);
	}

	public boolean containsKey(String propertyName) {
		return map.containsKey(propertyName);
	}

	public Set<Map.Entry<String, Object>> entrySet() {
		return map.entrySet();
	}
}
