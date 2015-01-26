package org.sigmah.server.security.impl;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.sigmah.server.dao.UserDAO;
import org.sigmah.server.domain.User;
import org.sigmah.server.security.Authenticator;
import org.sigmah.shared.security.AuthenticationException;

import com.google.inject.Inject;

/**
 * Authenticator service database implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class DatabaseAuthenticator implements Authenticator {

	private static final char[] letters = {
																					'a',
																					'b',
																					'c',
																					'd',
																					'e',
																					'f',
																					'g',
																					'h',
																					'i',
																					'j',
																					'k',
																					'l',
																					'm',
																					'n',
																					'p',
																					'q',
																					'r',
																					's',
																					't',
																					'u',
																					'v',
																					'w',
																					'x',
																					'y',
																					'z'
	};

	private static final char[] caps = {
																			'A',
																			'B',
																			'C',
																			'D',
																			'E',
																			'F',
																			'G',
																			'H',
																			'I',
																			'J',
																			'K',
																			'L',
																			'M',
																			'N',
																			'P',
																			'Q',
																			'R',
																			'S',
																			'T',
																			'U',
																			'V',
																			'W',
																			'X',
																			'Y',
																			'Z'
	};

	private static final char[] numbers = {
																					'1',
																					'2',
																					'3',
																					'4',
																					'5',
																					'6',
																					'7',
																					'8',
																					'9'
	};

	private static final char[] symbols = {
																					'$',
																					'+',
																					'-',
																					'=',
																					'_',
																					'!',
																					'%',
																					'@'
	};

	private static final char[][] alphabets = {
																							letters,
																							caps,
																							numbers,
																							symbols
	};

	/**
	 * Injected {@link UserDAO}.
	 */
	private final UserDAO userDAO;

	@Inject
	public DatabaseAuthenticator(final UserDAO userDAO) {
		this.userDAO = userDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public User authenticate(final String login, final String password) throws AuthenticationException {

		final User user = userDAO.findUserByEmail(login);

		if (user == null) {
			throw new AuthenticationException();
		}

		if (BooleanUtils.isNotTrue(user.getActive())) {
			throw new AuthenticationException();
		}

		if (StringUtils.isBlank(user.getHashedPassword()) || !BCrypt.checkpw(password, user.getHashedPassword())) {
			throw new AuthenticationException();
		}

		return user;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String hashPassword(final String plainTextPassword) {
		return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generatePassword() {

		final StringBuilder password = new StringBuilder();

		final int[] remainings = new int[] {
																				4,
																				2,
																				1,
																				1
		};
		int size = 8;

		while (size > 0) {
			int nextChar = -1;
			while (nextChar == -1) {
				int alphabet = (int) (Math.random() * remainings.length);
				if (remainings[alphabet] > 0) {
					nextChar = alphabets[alphabet][(int) (Math.random() * alphabets[alphabet].length)];
					remainings[alphabet]--;
				}
			}
			password.append((char) nextChar);

			size--;
		}

		return password.toString();
	}

}
