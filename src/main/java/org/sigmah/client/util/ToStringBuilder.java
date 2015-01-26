package org.sigmah.client.util;

/**
 * <p>
 * Client-side {@code toString} builder implementation.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 * @see org.apache.commons.lang3.builder.ToStringBuilder
 */
public final class ToStringBuilder {

	/**
	 * To string properties separator.
	 */
	private static final String SEPARATOR = ", ";

	/**
	 * The inner buffer.
	 */
	private final StringBuilder buffer = new StringBuilder();

	/**
	 * The object instance (may be {@code null}).
	 */
	private final Object object;

	/**
	 * <p>
	 * Initializes a new {@code ToStringBuilder} for the given {@code object} instance.
	 * </p>
	 * 
	 * @param object
	 *          The object instance (may be {@code null}).
	 */
	public ToStringBuilder(final Object object) {
		this.object = object;
	}

	/**
	 * Appends the given {@code name} and {@code value} to the current builder.
	 * 
	 * @param name
	 *          The property name.
	 * @param value
	 *          The property value.
	 * @return the current builder instance.
	 */
	public ToStringBuilder append(final String name, final Object value) {

		appendSeparator();

		buffer.append(name);
		buffer.append('=');
		buffer.append(value);

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {

		final String className;

		if (object == null) {
			className = null;

		} else {
			className = object.getClass().getName();
		}

		return className + '[' + buffer + ']';
	}

	/**
	 * Appends comma separator if necessary.
	 * 
	 * @see #SEPARATOR
	 */
	private void appendSeparator() {
		if (buffer.length() > 0) {
			buffer.append(SEPARATOR);
		}
	}

}
