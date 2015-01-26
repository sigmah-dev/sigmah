package org.sigmah.server.dao.impl;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.dao.ValueDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.OrgUnit;
import org.sigmah.server.domain.value.Value;

/**
 * {@link ValueDAO} implementation.
 * 
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class ValueHibernateDAO extends AbstractDAO<Value, Integer> implements ValueDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Value> findValuesForOrgUnits(final Collection<OrgUnit> orgUnits) {

		if (CollectionUtils.isEmpty(orgUnits)) {
			throw new IllegalArgumentException("Invalid OrgUnits collection.");
		}

		final StringBuilder builder = new StringBuilder();
		builder.append("SELECT v ");
		builder.append(" FROM Value v ");
		builder.append(" WHERE v.containerId IN (");
		builder.append("   SELECT ");
		builder.append("     ud.id ");
		builder.append("   FROM ");
		builder.append("     OrgUnit o");
		builder.append("     INNER JOIN o.databases ud ");
		builder.append("   WHERE o IN (:orgUnits)");
		builder.append(" )");
		builder.append(" OR v.containerId IN (:orgUnits)");

		final TypedQuery<Value> query = em().createQuery(builder.toString(), Value.class);
		query.setParameter("orgUnits", orgUnits);

		return query.getResultList();
	}

}
