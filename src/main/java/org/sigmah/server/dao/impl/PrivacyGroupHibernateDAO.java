package org.sigmah.server.dao.impl;

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
