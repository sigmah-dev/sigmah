package org.sigmah.server.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.UserUnitDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.User;
import org.sigmah.server.domain.profile.OrgUnitProfile;

/**
 * UserUnitDAO implementation.
 * 
 * @author nrebiai
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class UserUnitDAOImpl extends AbstractDAO<OrgUnitProfile, Integer> implements UserUnitDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean doesOrgUnitProfileExist(final User user) {
		return CollectionUtils.isNotEmpty(em().createQuery("SELECT o from OrgUnitProfile o WHERE o.user = :user").setParameter("user", user).getResultList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrgUnitProfile findOrgUnitProfileByUser(final User user) {
		try {

			return em().createQuery("SELECT o from OrgUnitProfile o WHERE o.user = :user", entityClass).setParameter("user", user).getSingleResult();

		} catch (final NoResultException e) {
			return null;

		} catch (final Throwable t) {
			throw t;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<User> findUsersByOrgUnit(final List<OrgUnit> orgUnits) {
		return em().createQuery("SELECT o.user FROM OrgUnitProfile o WHERE o.orgUnit IN (:orgUnits)", User.class).setParameter("orgUnits", orgUnits)
			.getResultList();
	}

}
