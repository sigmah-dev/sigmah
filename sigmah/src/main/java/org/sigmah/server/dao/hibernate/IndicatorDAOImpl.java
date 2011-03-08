package org.sigmah.server.dao.hibernate;

import javax.persistence.EntityManager;

import org.sigmah.shared.dao.IndicatorDAO;
import org.sigmah.shared.domain.Indicator;

import com.google.inject.Inject;

public class IndicatorDAOImpl extends GenericDAO<Indicator, Integer> implements IndicatorDAO {
	
	@Inject
	public IndicatorDAOImpl(EntityManager em) {
		super(em);
	}

	@Override
	public void persist(Indicator entity) {
		em.persist(entity);
	}

	@Override
	public Indicator findById(Integer primaryKey) {
		return null;
	}

	@Override
	public Indicator findIndicatorsByDatabaseId(int userDatabaseId) {
		return (Indicator) em.createNamedQuery("findIndicatorsByDatabaseId")
	      .setParameter("key", userDatabaseId)
	      .getSingleResult();
	}
}
