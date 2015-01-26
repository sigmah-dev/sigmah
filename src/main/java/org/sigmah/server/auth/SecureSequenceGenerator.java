package org.sigmah.server.auth;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;

/**
 * <p>
 * Identifier Generator that generates unique IDs for our authentication that are sufficiently random so that they
 * cannot be guessed.
 * </p>
 * <p>
 * <em>This class is referenced by name by the {@link org.sigmah.server.domain.Authentication} domain object.</em>
 * </p>
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
// This class is referenced by name by the Authentication domain object.
public class SecureSequenceGenerator implements org.hibernate.id.IdentifierGenerator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Serializable generate(final SessionImplementor session, final Object object) throws HibernateException {
		return SecureTokenGenerator.generate();
	}

}
