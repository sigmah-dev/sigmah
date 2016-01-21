package org.sigmah.server.dao.base;

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

import javax.persistence.EntityManager;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * <p>
 * All implementations requiring {@link EntityManager} should inherit this class.
 * </p>
 * <p>
 * Using its internal injected entity manager {@link Provider}, this instance provides an {@link #em()} method returning
 * a proper entity manager instance.
 * </p>
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class EntityManagerProvider {

	/**
	 * The injected entityManager provider.
	 */
	@Inject
	private Provider<EntityManager> entityManagerProvider;

	/**
	 * Returns the entity manager instance.
	 * 
	 * @return the entity manager instance.
	 */
	protected final EntityManager em() {
		return entityManagerProvider.get();
	}

}
