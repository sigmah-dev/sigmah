package org.sigmah.server.auth;

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
