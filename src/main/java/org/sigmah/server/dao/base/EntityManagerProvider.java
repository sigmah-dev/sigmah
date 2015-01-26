package org.sigmah.server.dao.base;

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
