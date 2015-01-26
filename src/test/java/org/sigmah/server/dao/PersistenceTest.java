package org.sigmah.server.dao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.sigmah.server.domain.Authentication;
import org.sigmah.server.domain.User;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.Language;

import com.google.inject.Inject;

/**
 * Tests DAO {@code findById}, {@code persist} and {@code remove} actions.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class PersistenceTest extends AbstractDaoTest {

	@Inject
	private UserDAO userDAO;

	@Inject
	private AuthenticationDAO authenticationDAO;

	@Inject
	private Authenticator authenticator;

	/**
	 * Freshly persisted {@link User}.
	 */
	private User user;

	@Before
	public void before() {
		final User user = new User();
		user.setActive(Boolean.TRUE);
		user.setEmail("urd-sigmah+test@ideia.fr");
		user.setName("TestLastName");
		user.setFirstName("TestFirstName");
		user.setHashedPassword(authenticator.hashPassword("sigmah"));
		user.setLocale(Language.FR.getLocale());

		this.user = userDAO.persist(user, user);
	}

	@Test
	public void findById() {
		final User user = userDAO.findById(this.user.getId());
		Assert.assertEquals(this.user.getId(), user.getId());
		Assert.assertEquals(this.user.getName(), user.getName());

		final Authentication authentication = authenticationDAO.persist(new Authentication(user), null);
		Assert.assertNotNull(authentication.getId());

		authenticationDAO.remove(authentication, user);
	}

	@After
	public void after() {
		if (user.getId() != null) {
			userDAO.remove(user.getId(), user);
		}
	}
}
