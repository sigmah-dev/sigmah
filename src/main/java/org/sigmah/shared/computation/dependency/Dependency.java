package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.computation.instruction.HasHumanReadableFormat;
import org.sigmah.shared.util.Visitable;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface Dependency extends Visitable<DependencyVisitor>, HasHumanReadableFormat {
	
	boolean isResolved();
}
