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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import org.sigmah.server.dao.base.EntityManagerProvider;
import org.sigmah.server.domain.Project;
import org.sigmah.server.domain.ProjectFunding;
import org.sigmah.shared.computation.ValueResolver;
import org.sigmah.shared.computation.dependency.CollectionDependency;
import org.sigmah.shared.computation.dependency.ContributionDependency;
import org.sigmah.shared.computation.dependency.Dependency;
import org.sigmah.shared.computation.dependency.DependencyVisitor;
import org.sigmah.shared.computation.dependency.SingleDependency;
import org.sigmah.shared.computation.value.CollectionValue;
import org.sigmah.shared.computation.value.ComputedValue;
import org.sigmah.shared.computation.value.ComputedValues;
import org.sigmah.shared.computation.value.DoubleValue;
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
	public void resolve(final Collection<Dependency> dependencies, final int containerId, final AsyncCallback<Map<Dependency, ComputedValue>> callback) {
		final HashMap<Dependency, ComputedValue> values = new HashMap<>();
		
		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId = :containerId AND v.element.id = :elementId", String.class);
		query.setParameter("containerId", containerId);
		
		for (final Dependency dependency : dependencies) {
			dependency.accept(new DependencyVisitor() {
				@Override
				public void visit(SingleDependency dependency) {
					resolve(dependency, query, values);
				}

				@Override
				public void visit(CollectionDependency dependency) {
					resolve(dependency, containerId, values);
				}

				@Override
				public void visit(ContributionDependency dependency) {
					resolve(dependency, containerId, values);
				}
			});
		}
		
		callback.onSuccess(values);
	}
	
	/**
	 * Resolve the value of a <code>SingleDependency</code> with the given query.
	 * 
	 * @param dependency
	 *			Dependency to resolve.
	 * @param query
	 *			Query to use to fetch the value from the database.
	 * @param values
	 *			Map where to associate the given dependency with the retrieved value.
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
	
	/**
	 * Resolve the values of a <code>CollectionDependency</code>.
	 * 
	 * @param dependency
	 *			Dependency to resolve.
	 * @param containerId
	 *			Identifier of the container executing the computation.
	 * @param values
	 *			Map where to associate the given dependency with the retrieved values.
	 */
	private void resolve(final CollectionDependency dependency, final Integer containerId, final Map<Dependency, ComputedValue> values) {
		final ArrayList<Integer> containerIds = new ArrayList<>();
		
		final Project project = em().find(Project.class, containerId);
		switch (dependency.getScope().getLinkedProjectType()) {
			case FUNDED_PROJECT:
				for (final ProjectFunding link : project.getFunded()) {
					containerIds.add(link.getFunded().getId());
				}
				break;
			case FUNDING_PROJECT:
				for (final ProjectFunding link : project.getFunding()) {
					containerIds.add(link.getFunded().getId());
				}
				break;
			default:
				throw new UnsupportedOperationException("Linked project type not supported: " + dependency.getScope().getLinkedProjectType());
		}

		final TypedQuery<String> query = em().createQuery("SELECT v.value FROM Value v WHERE v.containerId IN :containerIds AND v.element.id = :elementId", String.class);
		query.setParameter("containerIds", containerIds);

		final ArrayList<ComputedValue> computedValues = new ArrayList<>();
		for (final String value : query.getResultList()) {
			computedValues.add(ComputedValues.from(value));
		}
		values.put(dependency, new CollectionValue(computedValues));
	}
	
	/**
	 * Resolve the values of a <code>ContributionDependency</code>.
	 * 
	 * @param dependency
	 *			Dependency to resolve.
	 * @param containerId
	 *			Identifier of the container executing the computation.
	 * @param values
	 *			Map where to associate the given dependency with the retrieved values.
	 */
	private void resolve(final ContributionDependency dependency, final Integer containerId, final Map<Dependency, ComputedValue> values) {
		final List<ComputedValue> computedValues;
					
		final Project project = em().find(Project.class, containerId);
		switch (dependency.getScope().getLinkedProjectType()) {
			case FUNDED_PROJECT:
				computedValues = getContributionsFromProjectFundings(project.getFunded());
				break;
			case FUNDING_PROJECT:
				computedValues = getContributionsFromProjectFundings(project.getFunding());
				break;
			default:
				throw new UnsupportedOperationException("Linked project type not supported: " + dependency.getScope().getLinkedProjectType());
		}

		values.put(dependency, new CollectionValue(computedValues));
	}
	
	/**
	 * Returns a list of every not <code>null</code> contribution in the given
	 * list of <code>ProjectFunding</code>s.
	 * 
	 * @param projectFundings
	 *			List of project fundings.
	 * @return A list every contribution as <code>ComputedValue</code>s.
	 */
	private List<ComputedValue> getContributionsFromProjectFundings(List<ProjectFunding> projectFundings) {
		final ArrayList<ComputedValue> computedValues = new ArrayList<>();
		
		for (final ProjectFunding projectFunding : projectFundings) {
			final Double contribution = projectFunding.getPercentage();
			if (contribution != null) {
				computedValues.add(new DoubleValue(contribution));
			}
		}
		
		return computedValues;
	}

}
