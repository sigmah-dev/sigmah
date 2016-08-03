package org.sigmah.shared.computation;

import org.sigmah.shared.computation.dependency.Dependency;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface DependencyResolver {
	
	void resolve(Dependency dependency);
}
