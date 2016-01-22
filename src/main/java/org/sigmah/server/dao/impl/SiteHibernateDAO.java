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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.sigmah.server.dao.SiteDAO;
import org.sigmah.server.dao.base.AbstractDAO;
import org.sigmah.server.domain.Site;
import org.sigmah.server.domain.util.EntityConstants;

import com.google.inject.persist.Transactional;

/**
 * SiteDAO implementation.
 * 
 * @author Alex Bertram
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public class SiteHibernateDAO extends AbstractDAO<Site, Integer> implements SiteDAO {

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional
	public void updateAttributeValues(final int siteId, final Map<Integer, Boolean> changes) {

		final Set<Integer> falseValues = new HashSet<Integer>();
		final Set<Integer> trueValues = new HashSet<Integer>();
		final Set<Integer> nullValues = new HashSet<Integer>();

		for (final Map.Entry<Integer, Boolean> change : changes.entrySet()) {

			final int attributeId = change.getKey();
			final Boolean value = change.getValue();

			if (value == null) {
				nullValues.add(attributeId);
			} else if (value) {
				trueValues.add(attributeId);
			} else {
				falseValues.add(attributeId);
			}
		}

		if (!nullValues.isEmpty()) {
			removeAttributeValueRowsFor(siteId, nullValues);
		}

		if (!trueValues.isEmpty() || !falseValues.isEmpty()) {
			final Set<Integer> knownValues = new HashSet<Integer>();
			knownValues.addAll(trueValues);
			knownValues.addAll(falseValues);

			insertMissingRows(siteId, knownValues);

			// now set the values

			if (trueValues.isEmpty()) {
				updateValuesSetAllToFalse(siteId, knownValues);
			} else {
				updateValues(siteId, trueValues, knownValues);
			}
		}
	}

	// --------------------------------------------------------------------------------
	//
	// UTILITY METHODS.
	//
	// --------------------------------------------------------------------------------

	private void updateValuesSetAllToFalse(int siteId, Set<Integer> knownValues) {

		final StringBuilder builder = new StringBuilder();

		builder.append("UPDATE ").append(EntityConstants.ATTRIBUTE_VALUE_TABLE);
		builder.append(" SET ").append(EntityConstants.ATTRIBUTE_VALUE_COLUMN_VALUE).append(" = 0 ");
		builder.append(" WHERE ").append(EntityConstants.SITE_COLUMN_ID).append(" = :siteId ");
		builder.append(" AND ").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(" IN ").append(attributeList(knownValues));

		// TODO [DAO] Should use JPQL query.
		update(em().createNativeQuery(builder.toString()).setParameter("siteId", siteId));
	}

	private void updateValues(int siteId, Set<Integer> trueValues, Set<Integer> knownValues) {

		final StringBuilder builder = new StringBuilder();

		builder.append("UPDATE ").append(EntityConstants.ATTRIBUTE_VALUE_TABLE);
		builder.append(" SET ").append(EntityConstants.ATTRIBUTE_VALUE_COLUMN_VALUE).append(" = ");
		builder.append(" (CASE ");
		builder.append("   WHEN (").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(" IN ").append(attributeList(trueValues)).append(") THEN 1 ");
		builder.append("   ELSE 0");
		builder.append(" END)");
		builder.append(" WHERE ").append(EntityConstants.SITE_COLUMN_ID).append(" = :siteId ");
		builder.append(" AND ").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(" IN ").append(attributeList(knownValues));

		// TODO [DAO] Should use JPQL query.
		update(em().createNativeQuery(builder.toString()).setParameter("siteId", siteId));
	}

	private void insertMissingRows(int siteId, Set<Integer> knownValues) {

		final StringBuilder builder = new StringBuilder();

		builder.append("INSERT INTO ").append(EntityConstants.ATTRIBUTE_VALUE_TABLE).append('(');
		builder.append(EntityConstants.SITE_COLUMN_ID).append(',');
		builder.append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(',');
		builder.append(EntityConstants.ATTRIBUTE_VALUE_COLUMN_VALUE);
		builder.append(')');
		builder.append(" SELECT ");
		builder.append("   :siteId AS ").append(EntityConstants.SITE_COLUMN_ID).append(',');
		builder.append("   SELECT ").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(',');
		builder.append("   0 AS ").append(EntityConstants.ATTRIBUTE_VALUE_COLUMN_VALUE);
		builder.append(" FROM ").append(EntityConstants.ATTRIBUTE_TABLE).append(" AS a ");
		builder.append(" WHERE ").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(" IN ").append(attributeList(knownValues));
		builder.append(" AND ").append(EntityConstants.ATTRIBUTE_COLUMN_ID).append(" NOT IN (");
		builder.append("   SELECT v.").append(EntityConstants.ATTRIBUTE_COLUMN_ID);
		builder.append("   FROM ").append(EntityConstants.ATTRIBUTE_VALUE_TABLE).append(" AS v ");
		builder.append("   WHERE ").append(EntityConstants.SITE_COLUMN_ID).append(" = :siteId)");

		update(em().createNativeQuery(builder.toString()).setParameter("siteId", siteId));
	}

	private void removeAttributeValueRowsFor(int siteId, Set<Integer> nullValues) {
		// The values for this attribute group are "unknown" and need to be removed from the database.
		update(em().createQuery("DELETE AttributeValue v WHERE v.site.id = ?1 AND v.attribute.id IN " + attributeList(nullValues)).setParameter(1, siteId));
	}

	private static String attributeList(Set<Integer> attributes) {

		final StringBuilder sb = new StringBuilder();
		sb.append("(");

		for (final Integer id : attributes) {
			if (sb.length() > 1) {
				sb.append(", ");
			}
			sb.append(id);
		}
		sb.append(")");

		return sb.toString();
	}
}
