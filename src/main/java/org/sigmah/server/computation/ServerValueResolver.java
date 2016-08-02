package org.sigmah.server.computation;

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

import com.google.gwt.user.client.rpc.AsyncCallback;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.shared.computation.ValueResolver;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.dto.element.DefaultFlexibleElementDTO;
import org.sigmah.shared.dto.element.FlexibleElementDTO;

/**
 * Server implementation of {@link ValueResolver}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.1
 */
public class ServerValueResolver extends EntityManagerProvider implements ValueResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolve(Collection<Dependency> dependencies, int containerId, AsyncCallback<Map<Dependency, ComputedValue>> callback) {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		
		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :containerId AND v.element.id = :elementId", String.class);
		query.setParameter("containerId", containerId);
		
		for (final Dependency dependency : dependencies) {
			dependency.visitBy(new DependencyVisitor() {
				@Override
				public void visit(SingleDependency dependency) {
					resolve(dependency, query, values);
				}

				@Override
				public void visit(CollectionDependency dependency) {
					throw new UnsupportedOperationException("Not supported yet.");
				}

				@Override
				public void visit(ContributionDependency dependency) {
					throw new UnsupportedOperationException("Not supported yet.");
				}
			});
		}
		
		callback.onSuccess(values);
	}
	
	/**
	 * Resolve a <code>SingleDependency</code> with the given query.
	 * 
	 * @param dependency Dependency to resolve.
	 * @param query Query to use to fetch the value from the database.
	 * @param values Map where to associate the given dependency with the retrieved value.
	 */
	private void resolve(final SingleDependency dependency, final TypedQuery<String> query, final Map<Dependency, ComputedValue> values) {
		final FlexibleElementDTO element = dependency.getFlexibleElement();
		
		if (!(element instanceof DefaultFlexibleElementDTO)) {
			final Integer id = element.getId();
			query.setParameter("elementId", id);

			String result = null;
			try {
				result = query.getSingleResult();
			} catch(NoResultException e) {
				// Ignored.
			}

			values.put(dependency, ComputedValues.from(result));
		} else {
			// NOTE: In the future, this method should also handle DefaultFlexibleElement.
			throw new UnsupportedOperationException("DefaultFlexibleElement are not supported yet.");
		}
	}

}
