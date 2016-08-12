package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.computation.instruction.HasHumanReadableFormat;
import org.sigmah.shared.util.Visitable;

/**
 * Describes a dependency to a variable value.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 * @since 2.2
 */
public interface Dependency extends Visitable<DependencyVisitor>, HasHumanReadableFormat {
	
	/**
	 * Returns <code>true</code> if this dependency is resolved and can be used
	 * client side, <code>false</code> otherwise. <br>
	 * <br>
	 * Unresolved dependency should be resolved using a
	 * {@link org.sigmah.shared.computation.DependencyResolver} server-side.
	 * 
	 * @return 
	 */
	boolean isResolved();
}
