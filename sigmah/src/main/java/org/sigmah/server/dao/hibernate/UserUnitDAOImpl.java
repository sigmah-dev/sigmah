/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.server.dao.hibernate;

import java.util.List;

import com.google.inject.Inject;
import org.sigmah.shared.dao.UserUnitDAO;
import org.sigmah.shared.domain.OrgUnit;
import org.sigmah.shared.domain.User;
import org.sigmah.shared.domain.profile.OrgUnitProfile;

import javax.persistence.EntityManager;

/**
 * @author nrebiai
 */
public class UserUnitDAOImpl extends GenericDAO<OrgUnitProfile, Integer> implements UserUnitDAO {

    @Inject
    public UserUnitDAOImpl(EntityManager em) {
        super(em);
    }
    
    @Override
    public boolean doesOrgUnitProfileExist(User user) {
        return em.createNamedQuery("findOrgUnitProfileByUser")
                .setParameter("user", user)
                .getResultList().size() == 1;
    }

    @Override
    public OrgUnitProfile findOrgUnitProfileByUser(User user){

        return (OrgUnitProfile) em.createNamedQuery("findOrgUnitProfileByUser")
                .setParameter("user", user)
                .getSingleResult();
    }
    
    @SuppressWarnings("unchecked")
	@Override
    public List<User> findUsersByOrgUnit(List<OrgUnit> orgUnits){

        return (List<User>) em.createNamedQuery("findUsersByOrgUnits")
                .setParameter("orgUnits", orgUnits)
                .getResultList();
    }

}
