package org.sigmah.server.dao.impl;

import java.util.List;

import javax.persistence.TypedQuery;

import org.sigmah.server.dao.PrivacyGroupDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.element.FlexibleElement;
import org.sigmah.server.domain.profile.PrivacyGroup;
import org.sigmah.server.domain.profile.Profile;

/**
 * {@link PrivacyGroupDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr) (v2.0)
 */
public class PrivacyGroupHibernateDAO extends AbstractDAO<PrivacyGroup, Integer> implements PrivacyGroupDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countRelatedProfiles(final Integer privacyGroupId) {
		final TypedQuery<Number> query =
				em().createQuery(
					"SELECT COUNT(p) FROM Profile p WHERE EXISTS (SELECT 1 FROM PrivacyGroupPermission pgp WHERE pgp.profile.id = p.id AND pgp.privacyGroup.id = :pgId)",
					Number.class);
		query.setParameter("pgId", privacyGroupId);
		return query.getSingleResult().intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Profile> findRelatedProfiles(final Integer privacyGroupId) {
		final TypedQuery<Profile> query =
				em().createQuery(
					"SELECT p FROM Profile p WHERE EXISTS (SELECT 1 FROM PrivacyGroupPermission pgp WHERE pgp.profile.id = p.id AND pgp.privacyGroup.id = :pgId)",
					Profile.class);
		query.setParameter("pgId", privacyGroupId);
		return query.getResultList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int countRelatedFlexibleElements(final Integer privacyGroupId) {
		final TypedQuery<Number> query = em().createQuery("SELECT COUNT(fe) FROM FlexibleElement fe WHERE fe.privacyGroup.id = :pgId", Number.class);
		query.setParameter("pgId", privacyGroupId);
		return query.getSingleResult().intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<FlexibleElement> findRelatedFlexibleElements(final Integer privacyGroupId) {
		final TypedQuery<FlexibleElement> query = em().createQuery("SELECT fe FROM FlexibleElement fe WHERE fe.privacyGroup.id = :pgId", FlexibleElement.class);
		query.setParameter("pgId", privacyGroupId);
		return query.getResultList();
	}

}
