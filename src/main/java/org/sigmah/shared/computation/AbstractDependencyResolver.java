package org.sigmah.shared.computation;

import org.sigmah.shared.computation.dependency.Dependency;

/**
 * Implementation of the common methods for every {@link DependencyResolver}.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public abstract class AbstractDependencyResolver implements DependencyResolver {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resolve(final Computation computation) {
		for (final Dependency dependency : computation.getDependencies()) {
			if (!dependency.isResolved()) {
				resolve(dependency);
			}
		}
	}

}
