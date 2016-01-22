package org.sigmah.server.dao.base;

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

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.sigmah.server.domain.base.Entity;
import org.sigmah.server.domain.layout.Layout;

/**
 * <p>
 * Abstract implementation for DAO manipulating a {@link Layout}.
 * </p>
 * 
 * @param <E>
 *          Entity type.
 * @param <K>
 *          Entity id type (primary key).
 * @author Denis Colliot (dcolliot@ideia.fr)
 */
public abstract class AbstractLayoutDAO<E extends Entity, K extends Serializable> extends AbstractDAO<E, K> implements LayoutDAO<E, K> {

	/**
	 * Retrieves the parent entity referencing the given {@code flexibleElementId}.
	 * 
	 * @param flexibleElementId
	 *          The flexible element id.
	 * @return The parent entity referencing the given {@code flexibleElementId}, or {@code null}.
	 */
	@Override
	public E findFromFlexibleElement(final Integer flexibleElementId) {

		final StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");
		builder.append("  DISTINCT e ");
		builder.append("FROM ");
		builder.append("  ").append(entityClass.getName()).append(" e ");
		builder.append("  LEFT JOIN e.layout layout ");
		builder.append("  LEFT JOIN layout.groups layoutGroups ");
		builder.append("  LEFT JOIN layoutGroups.constraints layoutConstraints ");
		builder.append("WHERE ");
		builder.append("  layoutConstraints.element.id  = :flexibleElementId");

		final TypedQuery<E> query = em().createQuery(builder.toString(), entityClass);
		query.setParameter("flexibleElementId", flexibleElementId);

		final List<E> results = query.getResultList(); // Can return multiple entities ?
		return CollectionUtils.isNotEmpty(results) ? results.get(0) : null;
	}

}
