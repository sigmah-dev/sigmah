package org.sigmah.server.dao;

import java.util.List;

import org.sigmah.server.dao.base.DAO;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.Profile;

/**
 * Data Access Object for the {@link org.sigmah.server.domain.User} domain class.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public interface UserDAO extends DAO<User, Integer> {

	/**
	 * Returns if a {@link User} exist for the given {@code email}.
	 * 
	 * @param email
	 *          The user email, representing a <em>unique</em> key in the table.<br/>
	 *          Case insensitive.
	 * @return {@code true} if a user exist for the given {@code email}, {@code false} otherwise.
	 */
	boolean doesUserExist(final String email);

	/**
	 * Finds the given {@code email} corresponding {@link User}.
	 * 
	 * @param email
	 *          The user email, representing a <em>unique</em> key in the table.<br/>
	 *          Case insensitive.
	 * @return the given {@code email} corresponding {@link User}, or {@code null} if no user exists for this email.
	 */
	User findUserByEmail(final String email);

	/**
	 * Finds the given change password {@code key} corresponding {@link User}.
	 * 
	 * @param key
	 *          The change password generated key.<br/>
	 *          Case insensitive.
	 * @return the given change password {@code key} corresponding {@link User}, or {@code null} if no user exists for
	 *         this key.
	 */
	User findUserByChangePasswordKey(final String key);

	/**
	 * Returns the number of {@link User}(s) linked to the given {@code profileId}.
	 * 
	 * @param profileId
	 *          The {@link Profile} unique id.
	 * @return The number of {@link User}(s) linked to the given {@code profileId}.
	 */
	int countUsersByProfile(final Integer profileId);

	/**
	 * Returns the {@link User}'s linked to the given {@code profileId}.
	 * 
	 * @param profileId
	 *          The {@link Profile} unique id.
	 * @return A list of all {@link User}'s linked to the given {@code profileId}.
	 */
	List<User> findUsersByProfile(final Integer profileId);

}
