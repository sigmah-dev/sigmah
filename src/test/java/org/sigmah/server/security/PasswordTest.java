package org.sigmah.server.security;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.domain.User;
import org.sigmah.server.inject.GuiceJUnitRunner;
import org.sigmah.server.inject.GuiceJUnitRunner.GuiceModules;
import org.sigmah.server.inject.MapperModule;
import org.sigmah.server.inject.PersistenceModule;
import org.sigmah.server.inject.SecurityModule;
import org.sigmah.server.security.impl.BCrypt;
import org.sigmah.shared.Language;
import org.sigmah.shared.security.AuthenticationException;

import com.google.inject.Inject;

/**
 * Password security related tests.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
@RunWith(GuiceJUnitRunner.class)
@GuiceModules({
								SecurityModule.class,
								PersistenceModule.class,
								MapperModule.class
})
public class PasswordTest {

	private static final String TEST_USER_LOGIN = "test-dumb-email@email.net";
	private static final String TEST_USER_PASSWORD = "sigmah";
	private static final String TEST_USER_NAME = "TestUserName";

	@Inject
	private UserDAO userDAO;

	@Inject
	private Authenticator authenticator;

	private User user;

	@Before
	public void initTestUser() {

		user = new User();
		user.setActive(Boolean.TRUE);
		user.setNewUser(Boolean.FALSE);
		user.setName(TEST_USER_NAME);
		user.setFirstName("TestUserFirstName");
		user.setEmail(TEST_USER_LOGIN);
		user.setLocale(Language.FR.getLocale());
		user.setHashedPassword(authenticator.hashPassword(TEST_USER_PASSWORD));

		userDAO.persist(user, null);
	}

	@After
	public void deleteTestUser() {
		if (user != null) {
			userDAO.remove(user.getId(), null);
		}
	}

	@Test
	public void encryptionTest() {
		System.out.println("One possible hash for '" + TEST_USER_PASSWORD + "': " + authenticator.hashPassword(TEST_USER_PASSWORD));
		Assert.assertTrue(BCrypt.checkpw(TEST_USER_PASSWORD, authenticator.hashPassword(TEST_USER_PASSWORD)));
		Assert.assertTrue(BCrypt.checkpw(TEST_USER_PASSWORD, authenticator.hashPassword(TEST_USER_PASSWORD)));
		Assert.assertFalse(BCrypt.checkpw(TEST_USER_PASSWORD + ' ', authenticator.hashPassword(TEST_USER_PASSWORD)));
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAuthentication1() throws AuthenticationException {
		authenticator.authenticate(TEST_USER_LOGIN, null);
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAuthentication2() throws AuthenticationException {
		authenticator.authenticate(TEST_USER_LOGIN, "");
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAuthentication3() throws AuthenticationException {
		authenticator.authenticate(TEST_USER_LOGIN, " ");
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAuthentication4() throws AuthenticationException {
		authenticator.authenticate(TEST_USER_LOGIN, TEST_USER_PASSWORD + ' ');
	}

	@Test
	public void validAuthentication1() throws AuthenticationException {
		Assert.assertEquals(TEST_USER_NAME, authenticator.authenticate(TEST_USER_LOGIN, TEST_USER_PASSWORD).getName());
	}
}
