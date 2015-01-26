package org.sigmah.server.dao.base;

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
