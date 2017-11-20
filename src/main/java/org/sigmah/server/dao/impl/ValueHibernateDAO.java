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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
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

	@Override
	public Value getValueByElementAndContainer(Integer elementId, Integer containerId) {
		try {
			return em().createQuery("SELECT v FROM Value v WHERE v.element.id = :elementId AND v.containerId = :containerId", Value.class)
					.setParameter("elementId", elementId)
					.setParameter("containerId", containerId)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Override
	public List<Value> findValuesByContainerId(Integer containerId) {
		return em().createQuery("" +
				"SELECT v " +
				"FROM Value v " +
				"WHERE v.containerId = :containerId", Value.class)
				.setParameter("containerId", containerId)
				.getResultList();
	}

	@Override
	public List<Value> findValuesByFlexibleElementId(Integer flexibleElementId) {
		return em().createQuery("" +
				"SELECT v " +
				"FROM Value v " +
				"WHERE v.element.id = :flexibleElementId", Value.class)
				.setParameter("flexibleElementId", flexibleElementId)
				.getResultList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Value> findValuesByIdInSerializedValue(Integer id) {
		return em().createNativeQuery("" +
				"SELECT v.* " +
				"FROM value v " +
				"WHERE v.value = :id " +
				"OR v.value ~ ('^(.*~)?'||:id||'(~.*)?$') ", Value.class)
				.setParameter("id", String.valueOf(id))
				.getResultList();
	}

	@Override
	public List<Integer> findContainerIdByElementAndValue(Integer elementId, String value){
		return em().createQuery("SELECT v.containerId from Value v WHERE v.element.id = :elementId AND v.value = :value", Integer.class)
				.setParameter("elementId", elementId)
				.setParameter("value", value)
				.getResultList();
	}
}
