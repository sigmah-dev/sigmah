package org.sigmah.shared.util;

import org.sigmah.client.util.ClientUtils;

/**
 * Utility class providing static methods for users data.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public final class Users {

	/**
	 * Utility class constructor.
	 */
	private Users() {
		// Only provides static methods.
	}

	/**
	 * Gets the formatted complete name of a user.
	 * <ul>
	 * <li>If the user has a first name and a last name, returns '<i>John Doe</i>'.</li>
	 * <li>If the user hasn't a first name and has a last name, returns '<i>Doe</i>'.</li>
	 * <li>If the user has neither a first name or a last name, returns an empty string.</li>
	 * </ul>
	 * 
	 * @param firstName
	 *          The user first name.
	 * @param lastName
	 *          The user last name.
	 * @return The complete name.
	 */
	public static String getUserCompleteName(final String firstName, final String lastName) {
		return buildUserName(firstName, lastName, true);
	}

	/**
	 * Gets the formatted short name of a user.
	 * <ul>
	 * <li>If the user has a first name and a last name, returns '<i>J. Doe</i>'.</li>
	 * <li>If the user hasn't a first name and has a last name, returns '<i>Doe</i>'.</li>
	 * <li>If the user has neither a first name or a last name, returns an empty string.</li>
	 * </ul>
	 * 
	 * @param firstName
	 *          The user first name.
	 * @param lastName
	 *          The user last name.
	 * @return The user's short name.
	 */
	public static String getUserShortName(final String firstName, final String lastName) {
		return buildUserName(firstName, lastName, false);
	}

	/**
	 * Builds the formatted <em>complete</em> or <em>short</em> name of a user.
	 * 
	 * @param firstName
	 *          The user first name.
	 * @param lastName
	 *          The user last name.
	 * @param completeName
	 *          {@code true} to build <em>complete</em> user name, {@code false} to build <em>short</em> user name.
	 * @return The <em>complete</em> or <em>short</em> user name.
	 */
	private static String buildUserName(final String firstName, final String lastName, boolean completeName) {

		final StringBuilder sb = new StringBuilder();

		if (ClientUtils.isNotBlank(firstName)) {
			sb.append(completeName ? firstName : firstName.charAt(0));
			sb.append(completeName ? ' ' : ". ");
		}
		if (ClientUtils.isNotBlank(lastName)) {
			sb.append(lastName);
		}

		return sb.toString();
	}

}
