package org.sigmah.shared.computation;

import org.sigmah.shared.computation.dependency.Dependency;

/**
 * Describes a resolver of computation dependencies external to a model.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface DependencyResolver {
	
	/**
	 * Resolve every unresolved dependencies of the given computation.
	 * 
	 * @param computation 
	 *			Computation to resolve.
	 */
	void resolve(Computation computation);
	
	/**
	 * Resolve the given dependency.
	 * 
	 * @param dependency 
	 *			Dependency to resolve.
	 */
	void resolve(Dependency dependency);
}
