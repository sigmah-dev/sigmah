package org.sigmah.shared.computation.dependency;

import org.sigmah.shared.util.Visitor;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface DependencyVisitor extends Visitor {
	
	void visit(SingleDependency dependency);
	void visit(CollectionDependency dependency);
	void visit(ContributionDependency dependency);
}
