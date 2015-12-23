package org.sigmah.server.dao;

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
	 *          The user email, representing a <em>unique</em> key in the table.
	 *          Case insensitive.
	 * @return {@code true} if a user exist for the given {@code email}, {@code false} otherwise.
	 */
	boolean doesUserExist(final String email);

	/**
	 * Finds the given {@code email} corresponding {@link User}.
	 *
	 * @param email
	 *          The user email, representing a <em>unique</em> key in the table.
	 *          Case insensitive.
	 * @return the given {@code email} corresponding {@link User}, or {@code null} if no user exists for this email.
	 */
	User findUserByEmail(final String email);

	/**
	 * Finds the given change password {@code key} corresponding {@link User}.
	 *
	 * @param key
	 *          The change password generated key.
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


	/**
	 * Retrieves the manager of the defined project
	 */
	User getProjectManager(Integer projectId);

	/**
	 * Retrieves all team members for the defined project
	 */
	List<User> getProjectTeamMembers(Integer projectId);
}
