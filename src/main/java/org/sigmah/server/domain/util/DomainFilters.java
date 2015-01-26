package org.sigmah.server.domain.util;

import javax.persistence.EntityManager;

import org.hibernate.Filter;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.User;

/**
 * <p>
 * Domain filters utility class.
 * </p>
 * <p>
 * Relies on pure Hibernate session.
 * </p>
 * 
 * @author Alex Bertram (v1.3)
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public final class DomainFilters {

	private DomainFilters() {
		// Only provides static methods.
	}

	/**
	 * Applies following filters in the Hibernate session using the given arguments:
	 * <ul>
	 * <li>Deleted filter: see {@link #applyDeletedFilter(org.hibernate.Session)}.</li>
	 * <li>Visible filter: see {@link #applyVisibleFilter(User, org.hibernate.Session)}.</li>
	 * </ul>
	 * 
	 * @param user
	 *          The user which id is used in <em>visible</em> filter.
	 * @param em
	 *          The entity manager used to retrieve Hibernate session.
	 */
	public static void applyUserFilter(final User user, final EntityManager em) {
		applyDeletedFilter(AbstractDAO.getSession(em));
		applyVisibleFilter(user, AbstractDAO.getSession(em));
	}

	/**
	 * Disables the {@link EntityFilters#USER_VISIBLE} filter into the given {@code em} inner session.
	 * 
	 * @param em
	 *          The entity manager instance.
	 */
	public static void disableUserFilter(final EntityManager em) {
		disableFilter(em, EntityFilters.USER_VISIBLE);
	}

	/**
	 * Disables the {@link EntityFilters#HIDE_DELETED} filter into the given {@code em} inner session.
	 * 
	 * @param em
	 *          The entity manager instance.
	 */
	public static void disableDeletedFilter(final EntityManager em) {
		disableFilter(em, EntityFilters.HIDE_DELETED);
	}

	/**
	 * Applies the <em>deleted</em> filter in the Hibernate session.
	 * 
	 * @param session
	 *          The entity manager inner session.
	 * @see EntityFilters#HIDE_DELETED
	 */
	private static void applyDeletedFilter(final org.hibernate.Session session) {
		// Hide entities deleted by users.
		session.enableFilter(EntityFilters.HIDE_DELETED);
	}

	/**
	 * Applies the <em>visible</em> filter in the Hibernate session.
	 * 
	 * @param user
	 *          The user which id is used in <em>visible</em> filter.
	 * @param session
	 *          The entity manager inner session.
	 * @see EntityFilters#USER_VISIBLE
	 * @see EntityFilters#CURRENT_USER_ID
	 */
	private static void applyVisibleFilter(final User user, final org.hibernate.Session session) {
		// Hide entities that this user does not have permission to view.
		final Filter filter = session.enableFilter(EntityFilters.USER_VISIBLE);
		final int currentUserid = user.getId() == null ? -1 : user.getId(); // Cannot be null.
		filter.setParameter(EntityFilters.CURRENT_USER_ID, currentUserid);
	}

	/**
	 * Disables the given {@code filterKey} into the given {@code em} inner session.
	 * 
	 * @param em
	 *          The entity manager instance.
	 */
	private static void disableFilter(final EntityManager em, final String filterKey) {
		AbstractDAO.getSession(em).disableFilter(filterKey);
	}

}
