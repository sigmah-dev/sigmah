package org.sigmah.shared.command.result;

import java.util.Map;

import org.sigmah.client.util.ClientUtils;

/**
 * An action result which returns a map.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @param <K>
 *          The type of the keys.
 * @param <V>
 *          The type of the values.
 */
public class MapResult<K, V> implements Result {

	/**
	 * The map.
	 */
	private Map<K, V> map;

	public MapResult() {
		// Serialization.
	}

	public MapResult(final Map<K, V> map) {
		this.map = map;
	}

	public Map<K, V> getMap() {
		return map;
	}

	public void setMap(final Map<K, V> map) {
		this.map = map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return map.toString();
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	/**
	 * Returns if the {@code MapResult} inner {@link #map} is not {@code null} and not {@code empty}.
	 * 
	 * @return {@code true} if the {@code MapResult} inner {@link #map} is not {@code null} and not {@code empty}.
	 */
	public boolean isNotEmpty() {
		return map != null && !map.isEmpty();
	}

	/**
	 * Returns the literal {@code String} value of the parameter corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the literal {@code String} value of the parameter corresponding to the given {@code key}.
	 */
	public String get(K key) {
		return map != null ? (String) map.get(key) : null;
	}

	/**
	 * Returns the {@code Integer} value of the parameter corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the {@code Integer} value of the parameter corresponding to the given {@code key}.
	 * @throws NumberFormatException
	 *           If the value is not a valid integer.
	 */
	public Integer getInteger(final K key) {

		if (map == null) {
			return null;
		}

		final V value = map.get(key);

		if (value instanceof Integer) {
			return (Integer) value;

		} else if (value instanceof String) {
			return Integer.parseInt((String) value);

		} else {
			return null;
		}
	}

	/**
	 * Returns the {@code Decimal} value of the parameter corresponding to the given {@code key}.
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the {@code Decimal} value of the parameter corresponding to the given {@code key}.
	 * @throws NumberFormatException
	 *           If the value is not a valid decimal.
	 */
	public Double getDecimal(final K key) {

		if (map == null) {
			return null;
		}

		final V value = map.get(key);

		if (value instanceof Double) {
			return (Double) value;

		} else if (value instanceof String) {
			return Double.parseDouble((String) value);

		} else {
			return null;
		}
	}

	/**
	 * Returns the {@code Boolean} value of the parameter corresponding to the given {@code key}.<br/>
	 * The parameter value must be <b>true</b>, <b>on</b> or <b>1</b> (see {@link ClientUtils#isTrue(Object)} for
	 * details).
	 * 
	 * @param key
	 *          The parameter key.
	 * @return the {@code Boolean} value of the parameter corresponding to the given {@code key}.
	 * @see ClientUtils#isTrue(Object)
	 */
	public boolean getBoolean(final K key) {

		if (map == null) {
			return false;
		}

		final V value = map.get(key);

		if (value instanceof Boolean) {
			return (Boolean) value;

		} else {
			return ClientUtils.isTrue(value);
		}
	}

}
