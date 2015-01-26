package org.sigmah.shared.conf;

import org.sigmah.client.util.ClientUtils;

/**
 * Configuration properties names.
 * 
 * @author Tom Miette (tmiette@ideia.fr)
 */
public final class PropertyName {

	private PropertyName() {
		// Utility class.
	}

	public static final String PREFIX_VERSION = "version";
	public static final String PREFIX_MAPS = "maps";
	public static final String PREFIX_MAIL = "mail";
	public static final String PREFIX_MAIL_MODEL = n("mail", "model");

	private static final String ERROR_TAG = "???";

	/**
	 * Builds a property name which {@code parts} are separated by a dot character.
	 * 
	 * <pre>
	 * n(null) -> ""
	 * n("") -> ""
	 * n("my", "Key", " rocks ") -> "my.Key.rocks"
	 * n("my", "Key", "rocks.like.hell") -> "my.Key.rocks.like.hell"
	 * </pre>
	 * 
	 * @param parts
	 *          The property name parts.
	 * @return The property name.
	 */
	public static final String n(final String... parts) {

		final StringBuilder builder = new StringBuilder();

		if (parts == null) {
			return builder.toString();
		}

		for (final String part : parts) {
			if (builder.length() > 0 && ClientUtils.isNotBlank(part)) {
				builder.append('.');
			}
			builder.append(ClientUtils.trimToEmpty(part));
		}

		return builder.toString();
	}

	/**
	 * Returns the <em>error</em> value for the given {@code key}.
	 * 
	 * <pre>
	 * error("my.key") -> "???my.key???"
	 * </pre>
	 * 
	 * @param key
	 *          The key for which no value can be found.
	 * @return The <em>error</em> value for the given {@code key}.
	 */
	public static final String error(final String key) {
		return ERROR_TAG + key + ERROR_TAG;
	}

	/**
	 * Returns if the given {@code key} is an error key produced by {@link #error(String)} method.
	 * 
	 * <pre>
	 * isErrorKey("my.key") -> false
	 * isErrorKey("???my.key???") -> true
	 * </pre>
	 * 
	 * @param key
	 *          The key value.
	 * @return {@code true} if the given {@code key} is an error key, {@code false} otherwise.
	 */
	public static final boolean isErrorKey(final String key) {
		return key != null && key.startsWith(ERROR_TAG) && key.endsWith(ERROR_TAG);
	}

}
